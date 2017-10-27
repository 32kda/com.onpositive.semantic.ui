package com.onpositive.semantic.model.binding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.commons.xml.language.HandlesParent;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.access.IExternalizer;
import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.INotifyableCollection;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.DefaultCommandFactory;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.IExpandableFunction;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.factory.IFactoryProvider;
import com.onpositive.semantic.model.api.labels.ILabelLookup;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.CommonPropertyProvider;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.ITargetDependentReadonly;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.RealmAccess;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.IValidator;

public class Binding extends AbstractBinding  {

	private final class CustomIValueListener implements IValueListener<Object> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void valueChanged(Object oldValue, Object newValue) {
			Binding.this.notifyEnablement(!ValueUtils.toBoolean(newValue));
		}
	}

	private final class CustomRealmProvider implements IRealmProvider,
			Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public IRealm getRealm(IHasMeta model, Object parentObject,
				Object object) {
			return realm;
		}
	}
	
	
	
	private final class ExternalChangeListener implements
			IValueListener<Object> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public String toString() {
			return Binding.this.toString();
		}

		public void valueChanged(Object oldValue, Object newValue) {
			if (Binding.this.changing) {
				return;
			}
			Object value = null;
			if (Binding.this.property != null) {
				value = Binding.this.property.getValue(Binding.this.object);
//				if (value instanceof IExpandableFunction){
//					IExpandableFunction m=(IExpandableFunction) value;
//					value=m.getValue(object);
//				}
			}
			else{
				return;
			}
			if (value == null) {
				if (Binding.this.value != null) {
					Binding.this.fireChanges(value, null, null);
				}
			} else if ((Binding.this.value == null) && (value != null)) {
				Binding.this.fireChanges(value, null, null);
			} else if (!value.equals(Binding.this.value)||isAlwaysFire()) {
				Binding.this.fireChanges(value, null, null);
			}
			Binding.this.validate(Binding.this.getValue());
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	boolean alwaysFire=false;
	
	private boolean changing;

	private ICommandExecutor commandExecutor;

	protected IConverter converter;

	transient private IRealm<Object> cRealm;

	String enablement;

	IListenableExpression<Boolean> exp;

	private final IValueListener<Object> externalChangeListener = new ExternalChangeListener();

	protected IFunction factory;

	protected String id;

	protected ILabelLookup lookup;

	protected Object object;

	IRealmProvider object2 = new CustomRealmProvider();

	protected String path;

	transient protected IProperty property;

	transient private IRealm<Object> realm;

	private boolean registerListeners = true;

	protected Class<?> subjectClass;

	private Object undoContext;

	IValueListener<?> vl = new CustomIValueListener();

	private boolean wc;

	public Binding() {
		this(null, "", null); //$NON-NLS-1$
		this.setAutoCommit(true);
	}

	public Binding(AbstractBinding abstractBinding, String id) {
		super();
		this.parent = abstractBinding;
		this.setWorkingCopiesEnabled(abstractBinding!=null?abstractBinding.isWorkingCopiesEnabled():false);
		
		if (abstractBinding instanceof Binding) {
			this.registerListeners = ((Binding) abstractBinding).registerListeners;
		}
		this.object = abstractBinding.getValue();
		this.id = id;
		abstractBinding.childBindings.put(id, this);
		this.hParent=abstractBinding;
		this.init(object);
	}

	public Binding(AbstractBinding abstractBinding, String id, Object object) {
		super();
		
		this.parent = abstractBinding;
			this.setWorkingCopiesEnabled(abstractBinding!=null?abstractBinding.isWorkingCopiesEnabled():false);
			
		
		if (abstractBinding instanceof Binding) {
			this.registerListeners = ((Binding) abstractBinding).registerListeners;
		}
		this.object = object;
		this.id = id;
		this.path = id;
		if (abstractBinding != null) {
			Binding put = abstractBinding.childBindings.put(id, this);
			try {
				this.init(object);
			} finally {
				if (put == null) {
					abstractBinding.childBindings.remove(id);
				} else {
					abstractBinding.childBindings.put(id, put);
				}
			}
			this.hParent=abstractBinding;
			return;
		}
		this.init(object);
	}

	public Binding(Class<?> c) {
		this.provider = this.getPropertyProvider(c);
		this.id = ""; //$NON-NLS-1$
		this.init(this.object);
		this.subjectClass = c;
		this.setAutoCommit(true);
	}

	public Binding(Object object) {
		this(null, "", object); //$NON-NLS-1$
		this.setAutoCommit(true);
	}

	public Binding(Object base, IProperty property, Object value) {
		this.object = base;
		this.value = value;
		this.property = (IProperty) property;
		if (property != null) {
			this.defaultMeta = property.getMeta();
		}
	}
	
	public void actionPerformed(Object object, Object extras) {
		BindingStack.callStarted(this);
		try {
			if (IFunction.class.isAssignableFrom(this.getSubjectClass())) {
				final IFunction a = (IFunction) this.actualGetValue();
				a.getValue(object);
			} else if (Runnable.class.isAssignableFrom(this.getSubjectClass())) {
				final Runnable a = (Runnable) this.value;
				a.run();
			} else {
				final Runnable adapter = (Runnable) Platform.getAdapter(
						this.value, Runnable.class);
				if (adapter != null) {
					adapter.run();
				} else {
					throw new RuntimeException();
				}
			}
		} finally {
			BindingStack.callEnded(this);
		}
	}

	@Override
	protected Object actualGetValue() {
		if (this.property != null) {
			Object vvl = this.property.getValue(this.object);

			if (converter != null) {
				Object from = converter.from(vvl);
				return from;
			} else {
				return vvl;
			}
		}
		return null;
	}

	public void addCommitCommand(CompositeCommand cmd) {
		if (this.isDirty()) {			
			ICommand createCommand = this.createCommand(this.value);
			if (createCommand != null) {
				cmd.addCommand(createCommand);
			}
		}
		super.addCommitCommand(cmd);
	}

	public void addForceCommitCommand(CompositeCommand cmd) {
		ICommand createCommand = this.createCommand(this.value);
		if (createCommand != null) {
			cmd.addCommand(createCommand);
		}
		super.addCommitCommand(cmd);
	}

	public boolean allowsMultiValues() {
		return DefaultMetaKeys.isMultivalue(this);
	}

	public Binding bindingFromExpression(String string) {
		IListenableExpression<?> parse = ExpressionAccess.parse(string, this);
		if (parse == null) {
			return null;
		}
		Binding binding = new Binding(parse);
		binding.id = string;
		addChild(binding);
		Binding binding2 = (Binding) binding.getBinding("value");
		binding2.setReadOnly(true);
		return binding2;
	}

	protected void commit(Object value) {
		this.changing = true;
		try {
			BindingStack.callStarted(this);
			try {
				innerCommit(value);
			} finally {
				BindingStack.callEnded(this);
			}
		} finally {
			this.changing = false;
		}
	}

	private boolean compareVals(final Object value2) {

		if (this.value instanceof Collection) {
			Collection c0 = (Collection) this.value;
			if (value2 instanceof Collection) {

				Collection c1 = (Collection) value2;
				if (c0.size() == c1.size()) {
					return c0.containsAll(c1);
				}
				return false;
			}
			if (c0.size() == 1) {
				if (c0.contains(value2)) {
					return true;
				}
			}
			if (c0.size() == 0) {
				if (value2 == null) {
					return true;
				}
			}
			return false;
		}
		if (value2 instanceof Collection) {
			Collection c0 = (Collection) value2;
			if (c0.size() == 1) {
				if (c0.contains(value)) {
					return true;
				}
			}
			if (c0.size() == 0) {
				if (value == null) {
					return true;
				}
			}
			return false;
		}

		if (this.value == null && value2 == null) {
			return true;
		}

		Object compareValue1 = this.value;
		if (compareValue1 == null)
			compareValue1 = "";

		Object compareValue2 = value2;
		if (compareValue2 == null)
			compareValue2 = "";

		return compareValue2.equals(this.value);
	}

	public ICommand createCommand(Object value) {
		Object newValue = this.object;
		ICommand cmd = createCommandForValue(value, newValue);
		return cmd;
	}

	protected ICommand createCommandForValue(Object value, Object baseObject) {
		ICommand cmd;
		boolean multivalue = DefaultMetaKeys.isMultivalue(this.property);
		
		value = doConvert(value, multivalue);
		if ((baseObject==null||baseObject==value)&&this.property==null){
			return null;
		}
		if( value==null&&property!=null&&DefaultMetaKeys.isRequired(property)){
			return null;//can not commit null to required property
		}
		// root bindins
		ICommandFactory factory = DefaultMetaKeys.getService(property!=null?property:this,
				ICommandFactory.class);
		if (factory == null) {
			factory = DefaultCommandFactory.INSTANCE;
		}
		IHasCommandExecutor property = DefaultMetaKeys.getService(this.property!=null?this.property:this,
				IHasCommandExecutor.class);
		
		if (property == null) {
			return null;
		}

		
		if (multivalue&&DefaultMetaKeys.getValue(this, DefaultMetaKeys.USE_ADD_REMOVE__KEY)) {
			
			if (value instanceof Collection) {
				final Collection<Object> values = (Collection<Object>) PropertyAccess
						.getValues(this.property, baseObject);
				final HashDelta buildFrom = HashDelta.buildFrom(values,
						(Collection) value);
				final CompositeCommand cm = new CompositeCommand();
				for (final Object o : buildFrom.getAddedElements()) {
					ICommand createAddValueCommand = factory
							.createAddValueCommand(property, baseObject, o);
					cm.addCommand(createAddValueCommand);
				}
				for (final Object o : buildFrom.getRemovedElements()) {
					cm.addCommand(factory.createRemoveValueCommand(property,
							baseObject, o));
				}
				cmd = cm;
			} else {
				if (value == null) {
					final Collection<Object> values = (Collection<Object>) PropertyAccess
							.getValues(this.property, baseObject);
					final CompositeCommand cm = new CompositeCommand();
					for (final Object o : values) {
						cm.addCommand(factory.createRemoveValueCommand(
								property, baseObject, o));
					}
					cmd = cm;
				} else {
					final ICommand createSetValueCommand = factory
							.createSetValueCommand(property, baseObject, value);
					cmd = createSetValueCommand;
				}
			}
		} else {
			if (factory == null) {
				return null;
			}
			final ICommand createSetValueCommand = factory
					.createSetValueCommand(property, baseObject, value);
			cmd = createSetValueCommand;

		}
		UndoMetaUtils.setUndoContext(cmd, this.getUndoContext());
		return cmd;
	}

	protected Object doConvert(Object value, boolean multivalue) {
		if (converter != null) {
			value = converter.to(value);
		}
		if (value == null)
			return null;
		//TODO FIXME we need to estabilish more robust autoconversion infrastucture
		if (property!=null&&!multivalue){
			Class<?> subjectClass2 = DefaultMetaKeys.getSubjectClass(property);
			if( subjectClass2!=null){
				if (value==null){
					if (subjectClass.isPrimitive()){
						return null;	
					}
				}
				else{
					if (!subjectClass2.isInstance(value)){
						if (Number.class.isAssignableFrom(subjectClass2)){
							if (value instanceof String){
								try{
								value=NumberFormat.getInstance().parseObject((String) value);
								}catch (NumberFormatException e) {
									try{
									value=Double.parseDouble((String) value);
									}catch (Exception ex) {
										return null;
									}
								} catch (ParseException e) {
									try{
									value=Double.parseDouble((String) value);
									}catch (Exception ex) {
										return null;
									}
								}
							}
							if (value instanceof Number){
								Number x=(Number) value;
								if (subjectClass2==Integer.class){
									value=x.intValue();
								}
								if (subjectClass2==Long.class){
									value=x.longValue();
								}
								if (subjectClass2==Byte.class){
									value=x.byteValue();
								}
								if (subjectClass2==Double.class){
									value=x.doubleValue();
								}
								if (subjectClass2==Float.class){
									value=x.floatValue();
								}
								if (subjectClass2==Short.class){
									value=x.shortValue();
								}
								return value;
							}
							
						}
						if (String.class.isAssignableFrom(subjectClass2)){
							return value.toString();
						}
					}
				}
			}
		}
		return value;
	}

	public void dispose() {
		if (registerListeners) {
			unregisterObjectListener();
			unregisterRealmListener(true);
		}
		if (this.parent != null) {
			this.parent.removeBinding(this.getId());

		}
		this.hParent=null;
		super.dispose();

	}

	public void disposeExpression() {

	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Binding other = (Binding) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.object == null) {
			if (other.object != null) {
				return false;
			}
		} else if ((this.object != this) && !this.object.equals(other.object)) {
			return false;
		}
		return true;
	}

	public String externalizeString(String value) {
		if (this.property != null) {
			IExternalizer service = DefaultMetaKeys.getService(property,
					IExternalizer.class);
			if (service != null) {
				return service.externalizeMessage(value);
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> class1) {

		final T adapter = DefaultMetaKeys.getService(this, class1);
		return adapter;
	}

	public IClassResolver getClassResolver() {
		return getService(IClassResolver.class);
	}

	public ICommandExecutor getCommandExecutor() {
		if (commandExecutor != null) {
			return commandExecutor;
		}
		if (this.property != null) {
			return DefaultMetaKeys.getService(property, ICommandExecutor.class);
		}
		if (this.provider != null) {
			return DefaultMetaKeys.getService(MetaAccess.getMeta(provider),
					ICommandExecutor.class);
		}
		return null;// Platform.getDefaultExecutor();
	}

	public IConverter getConverter() {
		return converter;
	}

	public String getDescription() {
		return DefaultMetaKeys.getDescription(this); //$NON-NLS-1$
	}

	public IFunction getElementFactory() {
		if (this.factory != null) {
			return this.factory;
		}
		final IFactoryProvider adapter = this
				.getAdapter(IFactoryProvider.class);
		if (adapter != null) {
			return adapter.getElementFactory(this.object);
		}
		return null;
	}

	public String getId() {
		return this.id;
	}

	public String getMessage() {
		return this.getWhyBindingIsDisabled();
	}

	public String getName() {
		return DefaultMetaKeys.getCaption(this);
	}

	@SuppressWarnings("unchecked")
	public Iterable<IValidationContext> getNestedContexts() {
		return (Collection) childBindings.values();
	}

	public Object getObject() {
		return this.object;
	}

	public String getPath() {
		return this.path != null ? this.path : this.id;
	}

	public IProperty getProperty() {
		return this.property;
	}

	protected IProperty getProperty(Object object2, String id2) {
		if (this.provider == null) {
			this.provider = super.getPropertyProvider(object2);
		}
		if (this.provider == null) {

			return null;
		}
		IProperty property2 = (IProperty) this.provider.getProperty(object2, id2);
		if (property2==null){
			property2=CommonPropertyProvider.INSTANCE.getProperty(object2, id2);
		}
		return property2;		
	}

	@SuppressWarnings("unchecked")
	public IRealm<Object> getRealm() {
		if (this.realm == null) {
			if (this.cRealm != null) {
				return this.cRealm;
			}
			final IRealmProvider adapter = this
					.getAdapter(IRealmProvider.class);
			if (adapter != null) {
				final IRealm realm2 = adapter
						.getRealm(this, getObject(), value);
				if (realm2 != null) {
					registerRealmListener();
				}
				this.cRealm = realm2;
				return realm2;
			}
		}
		return this.realm;
	}

	public String getRole() {
		return DefaultMetaKeys.getStringValue(this, DefaultMetaKeys.ROLE_KEY);
	}

	public IBinding getRoot() {
		final Binding parent2 = this.getParent();
		if (parent2 == null) {
			return this;
		}
		return parent2.getRoot();
	}

	public Class<?> getSubjectClass() {
		if (this.subjectClass != null) {
			return this.subjectClass;
		}
		if (this.property != null) {
			return DefaultMetaKeys.getSubjectClass(property);
		}
		if (this.value != null) {
			if (value instanceof Collection<?>) {
				Collection<?> c = (Collection<?>) value;
				if (c.isEmpty()) {
					return Object.class;
				}
			}
			return this.value.getClass();
		}
		return Object.class;
	}
	public String getTheme() {
		return DefaultMetaKeys.getStringValue(this, DefaultMetaKeys.THEME_KEY);
	}

	public Boolean getUndo() {
		return UndoMetaUtils.undoAllowed(this);
	}

	public Object getUndoContext() {

		if (this.undoContext != null) {
			return this.undoContext;
		}
		if (this.parent != null) {
			return ((Binding) this.parent).getUndoContext();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected final IValidator getValidator() {
		if (this.property != null) {
			return DefaultMetaKeys.getService(property, IValidator.class);
		}
		return null;
	}

	public Object getValue() {
		if (this.isNullSet) {
			return null;
		}
		if ((this.value == null) && (this.getUnconvertedValue() == null)) {
			if (this.property != null) {
				Object vvl = this.property.getValue(this.object);
//				if (vvl instanceof IExpandableFunction){
//					IExpandableFunction f=(IExpandableFunction) vvl;
//					Class<?> subjectClass2 = DefaultMetaKeys.getSubjectClass(this.property);
//					vvl=f.getValue(this.object);
//				}
				if (converter != null) {
					Object from = converter.from(vvl);
					setValueSilent(from);
				} else {
					setValueSilent(vvl);
				}

			} else {
				if (id != null && this.id.length() == 0||this.path==null|| this.path.length()==0) {
					setValueSilent(object);
				}
			}
		}
		if (this.value instanceof Collection) {
			final Collection<?> m = (Collection<?>) this.value;
			if (m.size() == 1) {
				return m.iterator().next();
			}
		}

		return this.value != null ? this.value : this.getUnconvertedValue();
	}

	@SuppressWarnings("unchecked")
	public Collection<? extends Object> getValueAsCollection() {
		if (value instanceof Collection<?>) {
			return new ArrayList<Object>((Collection<? extends Object>) value);
		}
		return new ArrayList<Object>(Collections.singleton(value));
	}

	public String getWhyBindingIsDisabled() {
		if (this.exp instanceof IListenableExpression) {
			final IListenableExpression<?> l = (IListenableExpression<?>) this.exp;
			final String message = l.getMessage();
			if (message != null) {
				return this.externalizeString(message);
			}
		}
		return ""; //$NON-NLS-1$
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.property == null) ? 0 : this.property.hashCode());
		result = prime
				* result
				+ (((this.object == null) || (this.object == this)) ? 0
						: this.object.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	protected void init(Object newObject) {
		
		unregisterObjectListener();
		unregisterRealmListener(false);
		this.object = newObject;
		this.provider = null;
		final IProperty bs = (this.path==null||this.path.length()==0)?null: this.getProperty(this.object, this.path);
		if (bs == null && path != null && path.length() == 0 && id != null
				&& id.length() > 0) {
			return;
		}
		this.property = bs;
		
		registerObjectListener();

		final Object vl = this.value;
		if (property != null) {
			this.value = null;
			this.defaultMeta = property.getMeta();
			this.cachedService=null;
			
		}
		this.unconvertedValue = null;
		this.cRealm = null;
		IListenableExpression<Boolean> exp2 = this.exp;
		if (exp2 != null) {
			if (exp2.getValue() instanceof Boolean) {
				Boolean booleanValue = (Boolean) exp2.getValue();
				if (booleanValue != null) {
					this.notifyEnablement(booleanValue);
				} else {
					this.notifyEnablement(false);
				}
			} else if (exp2.getValue() != null)
				this.notifyEnablement(true);
			else
				this.notifyEnablement(false);
		}
		this.getValue();

		this.initNewValue(this.getProperty(), newObject);
		if (this.value != vl) {
			if (((this.value != null) && !compareVals(vl)) || (vl != null)) {
				for (final IValueListener<?> v : this.vlisteners) {
					((IValueListener) v).valueChanged(vl, this.value);
				}
			}
		}

		super.notifyChanges();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initNewValue(IProperty property, Object object) {
		this.validate(this.getValue());
		ITargetDependentReadonly service = DefaultMetaKeys.getService(this,
				ITargetDependentReadonly.class);
		if (service != null) {
			IListenableExpression buildReadonlyExpression = service
					.buildReadonlyExpression(this, this);
			if (buildReadonlyExpression != null) {
				setupExpression(buildReadonlyExpression);
			}
			else{
				setupExpression(null);
			}
		} else {
			setupExpression(null);
		}
	}

	protected void innerCommit(Object value) {
		ICommand cmd = null;
		if (this.property != null) {
			cmd = this.createCommand(value);
			UndoMetaUtils.markUndoable(cmd, isUndoSupported());
			ICommandExecutor service = cmd.getCommandExecutor();
			try {
				service.execute(cmd);				
			} catch (Exception e) {
				setupStatus(CodeAndMessage.errorMessage(e.getMessage()));
			}
		}
		fireCommit(cmd);
	}

	public boolean isAlwaysFire() {
		return alwaysFire;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	public boolean isDeepDirty() {
		Collection<String> knownChilds = this.getKnownChilds();
		for (String s : knownChilds) {
			Binding binding = getChild(s);
			if (binding.isDeepDirty()) {
				return true;
			}
		}
		if (this.property != null) {
			Object value2 = this.property.getValue(this.object);
			if (converter != null) {
				value2 = converter.from(value2);
			}
			if (this.value == null) {
				return value2 != null;
			}
			boolean equals = compareVals(value2);
			return !equals;
		}
		return false;
	}

	public boolean isDirty() {		
		if (this.property != null) {
			Object value2 = this.property.getValue(this.object);
			if (converter != null) {
				value2 = converter.from(value2);
			}
			if (this.value == null) {
				return value2 != null;
			}
			boolean compareVals = compareVals(value2);
			return !compareVals;
		} else {
			return isDeepDirty();
		}
	}

	public boolean isReadOnly() {
		if (DefaultMetaKeys.isReadonly(this)) {
			return true;
		}
		if (this.exp != null) {
			return ValueUtils.toBoolean(exp.getValue());
		}
		if (this.property == null) {
			if (this.parent != null) {
				return isReadonlyWithoutProperty();
			}
		} else if ((this.object == null)
				&& !DefaultMetaKeys.isStatic(this.property)) {
			return true;
		}
		return ((this.property != null) && PropertyAccess.isReadonly(property,
				object));
	}

	protected boolean isReadonlyWithoutProperty() {
		return rw;
	}

	public boolean isRegisterListeners() {
		return this.registerListeners;
	}

	public boolean isRequired() {
		return DefaultMetaKeys.isRequired(this);
	}

	public boolean isStatic() {
		return false;
	}

	protected boolean isUndoSupported() {
		return UndoMetaUtils.undoAllowed(this);
	}

	public boolean isUnique() {
		return DefaultMetaKeys.isUnique(this);
	}

	/* (non-Javadoc)
	 * @see com.onpositive.semantic.model.binding.IHasWorkingCopyManager#isWorkingCopiesEnabled()
	 */
	@Override
	public boolean isWorkingCopiesEnabled(){
		return wc;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		property = PropertyAccess.getProperty(object, path);
		provider = PropertyAccess.getPropertyProvider(object);
		realm = RealmAccess.getRealm(object);
	}

	public void refresh() {
		refresh(false);
	}

	public void refresh(boolean refreshChildren) {
		Object value = null;
		if (this.property != null) {
			value = this.property.getValue(this.object);
		} else {
			if (this.id.length() == 0) {
				value = this.object;
			} else if (path.length() == 0) {
				value = this.value;
			}
		}

		if (converter != null) {
			value = converter.from(value);
		}
		if (value != this.value) {
			this.validate(value);
			this.fireChanges(value, null, null);
			setValueSilent(value);
			this.isNullSet = value == null;
			for (final Binding b : this.childBindings.values()) {
				b.setObject(value);
			}
		}
		if (refreshChildren) {
			for (Binding b : childBindings.values()) {
				b.refresh(refreshChildren);
			}
		}
	}

	private void registerObjectListener() {
		if (object != null && registerListeners) {
			ObjectChangeManager.addWeakListener(object, externalChangeListener);
		}
	}

	private void registerRealmListener() {
		if (registerListeners) {
			if (realm instanceof INotifyableCollection) {
				ObjectChangeManager
						.registerRealm((INotifyableCollection<?>) realm);
			}
			if (cRealm instanceof INotifyableCollection) {
				ObjectChangeManager
						.registerRealm((INotifyableCollection<?>) cRealm);
			}
		}
	}

	public void setAdapter(Class<?> class1, Object object) {
		this.registerService((Class) class1, object);
	}

	public void setAlwaysFire(boolean alwaysFire) {
		this.alwaysFire = alwaysFire;
	}

	public void setCommandExecutor(ICommandExecutor executor) {
		this.commandExecutor = executor;
	}

	public void setConverter(IConverter converter) {
		this.converter = converter;
	}

	@HandlesAttributeDirectly("description")
	public void setDescription(String attribute) {
		putMeta(DefaultMetaKeys.DESCRIPTION_KEY, attribute);
	}

	@SuppressWarnings("unchecked")
	@HandlesAttributeDirectly("enablement")
	public void setEnablement(String attribute) {
		attribute = "!(" + attribute + ")";
		if (enablement != null && enablement.equals(attribute)) {
			return;
		}
		final IListenableExpression<?> parse = ExpressionAccess.parse(
				attribute, this);
		if (parse == null) {
			return;
		}
		this.setupExpression((IListenableExpression<Boolean>) parse);
		this.enablement = attribute;
	}

	public void setFactory(IFunction factory) {
		this.factory = factory;
		this.notifyChanges();
	}

	@HandlesAttributeDirectly("id")
	public void setId(String id) {
		String id2 = this.id;
		this.id = id;
		if (parent!=null&&!id2.equals(id)){
			parent.childBindings.remove(id2);
			parent.childBindings.put(id, this);
		}
	}

	@HandlesAttributeDirectly("caption")
	public void setName(String string) {
		putMeta(DefaultMetaKeys.CAPTION_KEY, string);
	}

	public void setObject(Object newObject) {
		if (this.object == newObject) {
			return;
		}
		this.init(newObject);
	}

	@HandlesParent
	public void setParent(AbstractBinding abstractBinding) {
		if (parent != this) {
			this.parent = abstractBinding;
		}
		
		abstractBinding.addChild(this);
	}

	@HandlesAttributeDirectly("path")
	public void setPath(String path) {
		this.path = path;
	}
	
	protected boolean rw;

	@HandlesAttributeDirectly("readonly")
	public void setReadOnly(boolean b) {
		if (DefaultMetaKeys.isReadonly(this) != b) {
			putMeta(DefaultMetaKeys.READ_ONLY_KEY, b);
			rw=b;
			this.notifyEnablement(!this.isReadOnly());
		}
	}

	@SuppressWarnings("unchecked")
	public void setRealm(final IRealm<?> realm) {
		if (realm == this.realm) {
			return;
		}
		if (this.realm != null) {
			unregisterRealmListener(true);
		}
		this.realm = (IRealm<Object>) realm;
		if (this.realm != null) {

			this.registerService(IRealmProvider.class, object2);
		} else {
			this.classInfo.remove(IRealmProvider.class);
			this.cachedService.remove(IRealmProvider.class);
		}
		IPropertyProvider service = DefaultMetaKeys.getService(
				MetaAccess.getMeta(realm), IPropertyProvider.class);
		if (service != null) {
			provider = service;
		}

		if ((this.provider == null)
				&& (this.realm instanceof IPropertyProvider)) {
			this.provider = (IPropertyProvider) this.realm;
		}
		if (realm != null) {
			if (this.provider != null) {
				registerRealmListener();
			}
		}
		this.notifyChanges();
	}

	@HandlesAttributeDirectly("registerListeners")
	public void setRegisterListeners(boolean registerListeners) {
		this.registerListeners = registerListeners;
		if (registerListeners) {
			registerRealmListener();
			registerObjectListener();

		} else {
			unregisterRealmListener(true);
			unregisterObjectListener();
		}
	}

	@HandlesAttributeDirectly("required")
	public void setRequired(Boolean required) {
		putMeta(DefaultMetaKeys.REQUIRED_KEY, required);

	}

	public void setRole(String role) {
		putMeta(DefaultMetaKeys.ROLE_KEY, role);
	}

	public void setSubjectClass(Class<?> subjectClass) {
		this.subjectClass = subjectClass;
	}

	public void setTheme(String theme) {
		putMeta(DefaultMetaKeys.THEME_KEY, theme);
	}

	@HandlesAttributeDirectly("enableUndo")
	public void setUndo(Boolean undo) {
		putMeta(UndoMetaUtils.UNDO_ALLOWED, undo);
	}

	@HandlesAttributeDirectly("undo-context")
	public void setUndoContext(Object undoContext) {
		this.undoContext = undoContext;
	}

	void setupExpression(IListenableExpression<Boolean> expz) {
		if (this.exp != null) {
			if (!((IListenableExpression<?>) this.exp instanceof IBinding)) {
				this.exp.disposeExpression();
			}
			this.exp.removeValueListener(this.vl);
		}
		if (expz != null) {
			expz.addValueListener(this.vl);
			this.exp = expz;
		}
	}

	public void setValueSilent(Object value) {
		this.value = value;
		this.parentMeta = MetaAccess.getMeta(value).getMeta();
	}

	/* (non-Javadoc)
	 * @see com.onpositive.semantic.model.binding.IHasWorkingCopyManager#setWorkingCopiesEnabled(boolean)
	 */
	@Override
	public void setWorkingCopiesEnabled(boolean m){
		this.wc=m;
	}

	public boolean shouldCommit() {
		return super.shouldCommit()
				&& ((this.property == null) || (!PropertyAccess.isReadonly(
						this.property, object) && ((this.object != null) || DefaultMetaKeys
						.isStatic(this.property))));
	}

	public String toString() {
		return this.object + "->" + this.property; //$NON-NLS-1$
	}

	private void unregisterObjectListener() {
		if (object != null) {
			ObjectChangeManager.removeWeakListener(object,
					externalChangeListener);
		}
	}

	private void unregisterRealmListener(boolean unregisterRealm) {
		if (unregisterRealm && realm instanceof INotifyableCollection) {
			ObjectChangeManager
					.unregisterRealm((INotifyableCollection<?>) realm);
		}
		if (cRealm instanceof INotifyableCollection) {
			ObjectChangeManager
					.unregisterRealm((INotifyableCollection<?>) cRealm);
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		// out.writeObject(out);
		// out.writeObject(value);
		// out.writeUTF(path);
		// out.writeUTF(id);
	}

}