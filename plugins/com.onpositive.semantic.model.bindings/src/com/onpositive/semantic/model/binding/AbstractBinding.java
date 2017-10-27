package com.onpositive.semantic.model.binding;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import com.onpositive.commons.xml.language.ChildSetter;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.ErrorExpressionAdapter;
import com.onpositive.semantic.model.api.expressions.GetPropertyLookup;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.ISubsitutableExpression;
import com.onpositive.semantic.model.api.labels.ILabelLookup;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.labels.NotFoundException;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IStatusChangeListener;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.UniqueValidator;

@SuppressWarnings("rawtypes")
public abstract class AbstractBinding extends BaseMeta implements IBinding,Serializable,ISubsitutableExpression<Object> {

	private final class BindingRealm extends
			AbstractListenableExpression<Object> implements IBindingChangeListener<Object>{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void changed() {
			fireChanged();
		}

		public void enablementChanged(boolean isEnabled) {
			
		}

		public Object getValue() {
			return getRealm();
		}

		public void valueChanged(ISetDelta<Object> valueElements) {
			fireChanged();
		}
	}

	static final String FIELD_IS_REQUIRED = Messages
			.getString("AbstractBinding.1"); //$NON-NLS-1$

	private static final String INCORRECT_VALUE_IN_FIELD_0 = Messages
			.getString("AbstractBinding.0"); //$NON-NLS-1$

	transient private static final NotEmptyValidator NOT_EMPTY_VALIDATOR = new NotEmptyValidator();
	transient private static final UniqueValidator UNIQUE_VALIDATOR = new UniqueValidator();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean addParentToScope = true;

	@DefaultSerialize
	protected HashMap<String, Binding> childBindings = new HashMap<String, Binding>();

	private CodeAndMessage childStatus = CodeAndMessage.OK_MESSAGE;

	// private ILabelLookup labelLookup; //It's never used

	private final transient IValueListener<Object> childValueListener = new IValueListener<Object>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void valueChanged(Object oldValue, Object newValue) {
			AbstractBinding.this.validate(AbstractBinding.this.value);
		}

	};

	private boolean commitErrors = true;

	@DefaultSerialize
	private HashSet<ICommitListener> commitListeners;

	CodeAndMessage convert;

	private boolean enableParsing = false;

	@DefaultSerialize
	protected HashMap<String, IListenableExpression<?>> expressions = new HashMap<String, IListenableExpression<?>>();

	@DefaultSerialize
	protected AbstractBinding hParent;

	private Boolean isAutoCommit;

	protected boolean isNullSet;

	@DefaultSerialize
	private final HashSet<IBindingChangeListener<?>> listeners = new HashSet<IBindingChangeListener<?>>();

	@DefaultSerialize
	AbstractBinding parent;

	transient protected IPropertyProvider provider;

	private IListenableExpression<Object> realmExpression;

	@DefaultSerialize
	private final HashSet<IStatusChangeListener> slisteners = new HashSet<IStatusChangeListener>();

	private CodeAndMessage status = CodeAndMessage.OK_MESSAGE;

	protected Object unconvertedValue;

	@DefaultSerialize
	protected HashSet<IValidator> validators = new HashSet<IValidator>();

	protected Object value;

	@DefaultSerialize
	protected HashSet<IValueListener<?>> vlisteners = new HashSet<IValueListener<?>>();

	public AbstractBinding() {
		this.validators.add(NOT_EMPTY_VALIDATOR);
		this.validators.add(UNIQUE_VALIDATOR);
		putMeta(IValidationContext.DEEP_VALIDATION, false);
	}

	// public IListenableExpression<?> getExpression(String path) {
	// AbstractBinding b = this;//TODO FIXME
	// final IListenableExpression<?> listenableExpression = this.expressions
	// .get(path);
	// if (listenableExpression != null) {
	// return listenableExpression;
	// }
	//		if (path.equals("parent")) { //$NON-NLS-1$
	// return this.getParent();
	// }
	//
	//		if (path.equals("error")) { //$NON-NLS-1$
	// return new ErrorExpressionAdapter(b.getRoot());
	// }
	// ;
	//		if (path.endsWith(".error")) { //$NON-NLS-1$
	// return new ErrorExpressionAdapter(this.getBinding(path.substring(0,
	//					path.length() - ".error".length()))); //$NON-NLS-1$
	// }
	// ;
	// if (path.startsWith("!")) {
	// return new NotExpression(getExpression(path.substring(1)));
	// }
	// while (b.parent != null) {
	// b = b.parent;
	// }
	// return getBinding(path);
	// }

	protected abstract Object actualGetValue() ;

	public void addBindingChangeListener(IBindingChangeListener<?> listener) {
		this.listeners.add(listener);
	}
	@ChildSetter(value = "bindingMember", needCasting = false)
	public void addChild(Binding binding) {
		setBinding(binding.id, binding);
	}
	public void addCommitCommand(CompositeCommand cmd) {
		for (final Binding bnd : this.childBindings.values()) {
			bnd.addCommitCommand(cmd);
		}
	}
	public void addCommitListener(ICommitListener l) {
		if (commitListeners == null) {
			commitListeners = new HashSet<ICommitListener>();
		}
		commitListeners.add(l);
	}

	public abstract void addForceCommitCommand(CompositeCommand cmd);
	public void addStatusChangeListener(IStatusChangeListener listener) {
		this.slisteners.add(listener);
	}
	public void addValidator(IValidator<?> validator) {
		this.validators.add(validator);
		validate(this.getValue());
	}
	public void addValueListener(IValueListener<?> listener) {
		this.vlisteners.add(listener);
	}

	@SuppressWarnings("unchecked")
	private Object adjustStringIfNeeded(Object value) throws NotFoundException {
		CodeAndMessage resultValidation = CodeAndMessage.OK_MESSAGE;
		// if (this instanceof AbstractBinding) {
		final AbstractBinding bs = this;
		bs.unconvertedValue = value;
		// }
		final IRealm<Object> realm = this.getRealm();
		if (value instanceof Collection) {
			final Collection c = (Collection) value;
			if (c.isEmpty()) {
				return c;
			}
			final Object next = c.iterator().next();
			if (next.getClass() == String.class) {
				final Class<?> subjectClass = this.getSubjectClass();
				if ((subjectClass != String.class)
						&& (subjectClass != Object.class)
						&& (subjectClass != null)) {

					final ArrayList ls = new ArrayList();
					final ITextLabelProvider adapter2 = this
							.getAdapter(ITextLabelProvider.class);
					final ILabelLookup lsa = this.getLabelLookup();
					l2: for (final Object o : c) {
						CodeAndMessage validation = CodeAndMessage.OK_MESSAGE;
						Object lookUpByLabel = null;
						try {
							lookUpByLabel = lsa != null ? lsa.lookUpByLabel(
									this, getObject(), o.toString()) : null;

						} catch (final NotFoundException e) {
							resultValidation = CodeAndMessage.errorMessage(e
									.getMessage());
							break;
						}
						if (lookUpByLabel == null) {
							if (realm != null) {
								for (final Object o1 : realm.getContents()) {
									final String text = adapter2 != null ? adapter2
											.getText(this, getObject(), o1)
											: o1.toString();
									if (text.equalsIgnoreCase(o.toString())) {
										ls.add(o1);
										validation = CodeAndMessage.OK_MESSAGE;
										continue l2;
									}
								}
							}
							resultValidation = errorStringConversion();
							value = null;
							break;
						} else {
							ls.add(lookUpByLabel);
						}
						if (value == null) {
							resultValidation = errorStringConversion();
						}
						if (validation.getCode() == CodeAndMessage.ERROR) {
							resultValidation = validation;
						}
					}
					value = ls;
				}
			}
		} else {
			if (value != null) {
				if (value.getClass() == String.class) {
					final ILabelLookup lsa = this.getLabelLookup();
					final Class<?> subjectClass = this.getSubjectClass();
					if (subjectClass != String.class
							&& subjectClass != Object.class) {
						boolean found = false;
						if (lsa != null) {

							Object lookUpByLabel = null;
							try {
								lookUpByLabel = lsa.lookUpByLabel(this,
										getObject(), (String) value);
							} catch (final NotFoundException e) {
								resultValidation = CodeAndMessage
										.errorMessage(e.getMessage());
							}
							if (lookUpByLabel != null) {
								value = lookUpByLabel;

								found = true;
							} else {
								if (!isRequired()) {
									value = null;
									found = true;
								}
							}
						}
						if (!found && resultValidation == null
								|| !resultValidation.isError()) {
							final ITextLabelProvider adapter2 = this
									.getAdapter(ITextLabelProvider.class);
							if (adapter2 != null) {
								if (realm != null) {
									for (final Object o : realm.getContents()) {
										final String text = adapter2.getText(
												this, getObject(), o);
										if (text.equalsIgnoreCase(value
												.toString())) {
											value = o;
											resultValidation = CodeAndMessage.OK_MESSAGE;
											found = true;
											break;
										}
									}
								}
							}
						}
						if (!found) {
							if (value.toString().length()!=0){
							resultValidation = CodeAndMessage
									.errorMessage(Messages
											.getString("AbstractBinding.4")); //$NON-NLS-1$
							}
						}
					}
				}
			}
		}

		if (resultValidation.getCode() == CodeAndMessage.ERROR) {
			convert = getStatus();
			this.setupStatus(resultValidation);
			throw new NotFoundException(resultValidation.getMessage());
		} else {
			if (convert != null) {
				this.setupStatus(convert);
				convert = null;
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public Binding binding(String path) {
		final IListenableExpression binding = getBinding(path);
		if (binding instanceof Binding) {
			return (Binding) binding;
		}

		final Binding bnd = new Binding(binding);
		binding.addValueListener(new IValueListener<Object>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void valueChanged(Object oldValue, Object newValue) {
				bnd.setObject(binding.getValue());
			}
		});
		bnd.setId(path);
		addChild(bnd);
		bnd.setReadOnly(true);
		return bnd;
	}

	public void commit() {
		try {
			startCommit(this);
			final CompositeCommand commit = new CompositeCommand();
			boolean undoSupported = isUndoSupported();
			if (undoSupported) {
				UndoMetaUtils.setUndoContext(commit, getUndoContext());
				UndoMetaUtils.markUndoable(commit, undoSupported);
			}
			this.addCommitCommand(commit);
			commit.getCommandExecutor().execute(commit);
			fireCommit(commit);
			validate(this.getValue());
		} finally {
			BindingStack.callEnded(this);
		}
	}

	protected abstract void commit(Object value);

	protected Binding createBinding(String path) {
		return new Binding(this, path, this.getId().length() > 0 ? this.value
				: this.getObject());
	}

	public void dispose() {
		for (final Binding b : new HashSet<Binding>(this.childBindings.values())) {
			b.dispose();
			this.removeChild(b);
		}
	}

	protected void doValidate(IBinding bnd) {
		CodeAndMessage maxStatus = CodeAndMessage.OK_MESSAGE;
		for (final IBinding m : this.childBindings.values()) {
			final CodeAndMessage ms = m.getStatus();
			if (ms.getCode() > maxStatus.getCode()) {
				maxStatus = ms;
			}
		}
		final CodeAndMessage status2 = bnd.getStatus();
		if (status2 != null && status2.getCode() > maxStatus.getCode()) {
			maxStatus = status2;
		}
		if (maxStatus != this.childStatus) {
			this.childStatus = maxStatus;
			this.notifyStatusChange();
		}
	}

	protected CodeAndMessage errorStringConversion() {
		return CodeAndMessage.errorMessage(MessageFormat.format(
				INCORRECT_VALUE_IN_FIELD_0, this.getName()));
	}

	protected void finishCommit(IBinding bnd) {
		BindingStack.callEnded(bnd);
	}

	@SuppressWarnings("unchecked")
	protected void fireChanges(Object value, Object oldValue,
			IBindingChangeListener<?> client) {
		Object oldObject = this.value;
		if (oldValue != null)
			oldObject = oldValue;
		if ((this.value == null) && (value != null)) {
			final HashDelta<Object> dlt = new HashDelta<Object>();
			if (value instanceof Collection) {
				final Collection c = (Collection) value;
				for (final Object o : c) {
					dlt.markAdded(o);
				}
			} else {
				dlt.markAdded(value);
			}
			setValueSilent(value);
			this.notifyListeners(client, dlt, oldObject, value);

		} else if ((value == null) && (this.value != null)) {
			final HashDelta<Object> dlt = new HashDelta<Object>();
			if (oldObject instanceof Collection) {
				final Collection m = (Collection) oldObject;
				for (final Object o : m) {
					dlt.markRemoved(o);
				}

			} else {
				dlt.markRemoved(this.value);
			}
			setValueSilent(value);
			// if (value==null){
			// isNullSet=true;
			// }
			// else{
			// isNullSet=false;
			// }
			this.notifyListeners(client, dlt, oldObject, value);
		} else {
			Collection l = null;
			if (this.value instanceof Collection) {
				l = (Collection) this.value;
			} else {
				l = Collections.singleton(this.value);
			}
			Collection l1 = null;
			if (value instanceof Collection) {
				l1 = (Collection) value;
			} else {
				l1 = Collections.singleton(value);
			}
			final HashDelta buildFrom = HashDelta.buildFrom(l, l1);
			setValueSilent(value);
			// if (value==null){
			// isNullSet=true;
			// }
			// else{
			// isNullSet=false;
			// }
			this.notifyListeners(client, buildFrom, oldObject, value);
		}
	}

	protected void fireCommit(ICommand cmd) {
		if (commitListeners != null) {
			for (ICommitListener l : commitListeners) {
				l.commitPerformed(cmd);
			}
		}
	}

	public void forceCommit() {
		try {
			startCommit(this);
			final CompositeCommand commit = new CompositeCommand();
			this.addForceCommitCommand(commit);
			this.getCommandExecutor().execute(commit);
			fireCommit(commit);
		} finally {
			BindingStack.callEnded(this);
		}
	}

	public IBinding getBinding() {
		return this;
	}

	@SuppressWarnings("unchecked")
	public IListenableExpression<Object> getBinding(String path) {
		final Binding binding = this.childBindings.get(path);
		if (binding != null) {
			return binding;
		}		
		final int indexOf = path.indexOf('.');
		if (indexOf != -1 && !enableParsing) {
			final String parent = path.substring(0, indexOf);
			final String child = path.substring(indexOf + 1);
			final IListenableExpression binding2 = this.getBinding(parent);
			if (binding2 instanceof IExpressionEnvironment) {
				IExpressionEnvironment env = (IExpressionEnvironment) binding2;
				return (IListenableExpression<Object>) env.getBinding(child);
				// return binding2.getBinding(child);
			}
			if (binding2 != null) {
				GetPropertyLookup gp = new GetPropertyLookup(binding2, null);
				
				return (IListenableExpression<Object>) gp.getBinding(child);
			}
		}
		if (path.equals("this") && !enableParsing) { //$NON-NLS-1$
			return (Binding) this;
		}
		if (path.equals("$value") && !enableParsing) { //$NON-NLS-1$
			return new IListenableExpression<Object>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void addValueListener(IValueListener<?> exp) {
					//AbstractBinding.this.addValueListener(exp);
				}

				@Override
				public void disposeExpression() {
					
				}

				@Override
				public String getMessage() {
					return AbstractBinding.this.getMessage();
				}

				@Override
				public Object getValue() {
					return actualGetValue();
				}

				@Override
				public void removeValueListener(IValueListener<?> exp) {
					//AbstractBinding.this.removeValueListener(exp);
				}
			};
		}
		if (path.equals("$") && !enableParsing) { //$NON-NLS-1$
			return this.getParent();
		}
		if (path.equals("$$") && !enableParsing) { //$NON-NLS-1$
			return this.getRoot();
		}
		if (path.length()>0&& path.charAt(0) == '@') {
			if (path.equals("@error") && !enableParsing) { //$NON-NLS-1$
				return (IListenableExpression) new ErrorExpressionAdapter(this);
			}
			if (path.equals("@dirty") && !enableParsing) { //$NON-NLS-1$
				return (IListenableExpression) new DirtyAdapter(this);
			}
			if (path.equals("@realm")){
				IRealm<Object> realm = getRealm();
				if (realm!=null){
					if (realmExpression!=null){
						return realmExpression;
					}
					AbstractListenableExpression<Object> abstractListenableExpression = new BindingRealm();
					
					addBindingChangeListener((IBindingChangeListener<?>) abstractListenableExpression);
					realmExpression=abstractListenableExpression;
					return abstractListenableExpression;
				}
			}
		}
		if (path.equals("root") && !enableParsing) { //$NON-NLS-1$
			Binding parent2 = (Binding) getRoot();
			if (parent2 != null) {
				return parent2.getBinding(Messages
						.getString("AbstractBinding.3")); //$NON-NLS-1$
			}
			return (Binding) this;
		}
		if (path.equals("parent") && !enableParsing) { //$NON-NLS-1$
			return (Binding) this.parent;
		}
		// TODO FIXME ERROR expression
		if (this.parent != null) {
			if (this.isAddParentToScope()) {
				final Binding lookup = this.parent.lookup(path);
				if (lookup != null) {
					return lookup;
				}
			}
		}
		final Binding bnd = createBinding(path);
		bnd.setCommitErrors(this.commitErrors);
		bnd.addValueListener(this.childValueListener);
		this.childBindings.put(path, bnd);
		bnd.hParent=this;
		this.addChild(bnd);

		return bnd;
	}

	public Binding getChild(String path) {
		final Binding binding = this.childBindings.get(path);
		if (binding != null) {
			return binding;
		}
		return null;
	}

	protected abstract ICommandExecutor getCommandExecutor();

	public Collection<String> getKnownChilds() {
		return this.childBindings.keySet();
	}

	public ILabelLookup getLabelLookup() {
		// if (this.labelLookup != null) {
		// return this.labelLookup;
		// }
		return this.getAdapter(ILabelLookup.class);
	}

	public Binding getParent() {
		return (Binding) this.parent;
	}

	protected IPropertyProvider getPropertyProvider(Object object) {
		if (this.provider != null) {
			return this.provider;
		}
		if (object == null) {
			if (this.parent != null) {
				return this.parent.getPropertyProvider(null);
			}
			return null;
		}
		return PropertyAccess.getPropertyProvider(object);
	}

	public final CodeAndMessage getStatus() {
		if (status != null && childStatus != null) {
			if (this.childStatus.getCode() > this.status.getCode()) {
				return this.childStatus;
			}
		}
		return this.status;
	}

	public Object getUnconvertedValue() {
		return this.unconvertedValue;
	}

	protected abstract IValidator<?> getValidator();

	public Object getValue() {
		return this.value;
	}

	public abstract String getWhyBindingIsDisabled();

	@SuppressWarnings("unchecked")
	protected void internalSet(Object value, IBindingChangeListener<?> client,
			boolean ls) {
		this.validate(value);
		Object oldValue = null;
		if (this.shouldCommit()) {
			if (this.value instanceof Collection)
				oldValue = new ArrayList((Collection<?>) this.value);
			this.commit(value);
		}
		this.isNullSet = value == null;
		this.fireChanges(value, oldValue, client);
		boolean cm = this.isReadOnly();
		if (ls != cm) {
			notifyEnablement(cm);
		}
		setValueSilent(value);

		for (final Binding b : new HashSet<Binding>(this.childBindings.values())) {
			b.setObject(value);
		}
		if (this.parent != null) {
			final Object pvalue = this.parent.getValue();
			final HashDelta<Object> dlt = new HashDelta<Object>();
			if (pvalue != null) {
				dlt.markChanged(pvalue);
			}
			this.parent.notifyListeners(client, dlt, null, pvalue);
			parent.onChildChanged();
		}
	}

	public boolean isAddParentToScope() {
		return this.addParentToScope;
	}

	public final boolean isAutoCommit() {
		if (isAutoCommit==null){
			if (parent!=null){
				return parent.isAutoCommit();
			}
			return true;
		}
		return this.isAutoCommit;
	}

	public boolean isCommitErrors() {
		return this.commitErrors;
	}

	public boolean isParsingEnabled() {
		return enableParsing;
	}
	
	protected boolean isUndoSupported() {
		return true;
	}

	private Binding lookup(String path) {
		final Binding binding = this.childBindings.get(path);
		if (binding != null) {
			return binding;
		}
		if (this.parent != null && parent != this) {
			return this.parent.lookup(path);
		}
		return null;
	}

	public Object lookupByLabel(String label) throws NotFoundException {
		return this.adjustStringIfNeeded(label);
	}

	@SuppressWarnings("unchecked")
	public Collection<Object> lookupByLabels(Collection<String> labels)
			throws NotFoundException {
		return (Collection<Object>) this.adjustStringIfNeeded(labels);
	}

	protected void notifyChanges() {
		for (final IBindingChangeListener<?> l : new HashSet<IBindingChangeListener<?>>(
				this.listeners)) {
			l.changed();
		}
		for (final Binding b : this.childBindings.values().toArray(new Binding[childBindings.size()])) {

			b.setObject(this.value);
		}
		this.validate(this.getValue());
	}

	protected void notifyEnablement(boolean enablement) {
		for (final IBindingChangeListener<?> l : this.listeners) {
			l.enablementChanged(enablement);
		}
	}

	@SuppressWarnings("unchecked")
	protected void notifyListeners(IBindingChangeListener<?> client,
			HashDelta<Object> dlt, Object oldValue, Object value2) {
		if (!dlt.isEmpty()) {
			for (final IBindingChangeListener<?> l : new HashSet<IBindingChangeListener<?>>(
					this.listeners)) {
				if (l != client) {
					try {
						l.valueChanged((ISetDelta) dlt);
					} catch (final Throwable e) {
						Platform.log(e);
					}
				}
			}
		}
		for (final IValueListener<?> v : new HashSet<IValueListener<?>>(
				this.vlisteners)) {
			((IValueListener) v).valueChanged(oldValue, value2);
		}
	}

	public void notifyPossibleChange() {
		final HashDelta<Object> hashDelta = new HashDelta<Object>();
		hashDelta.markChanged(this.value);
		this.notifyListeners(null, hashDelta, null, this.value);
	}

	protected void notifyStatusChange() {
		if (this.parent != null) {
			this.parent.doValidate(this);
		}
		for (final IStatusChangeListener s : new HashSet<IStatusChangeListener>(
				this.slisteners)) {
			s.statusChanged(this, this.getStatus());
		}
	}

	public void onChildChanged() {
		if (parent!=null){
			parent.onChildChanged();
		}
		else if (hParent!=null){
			hParent.onChildChanged();
		}
	}

	public void registerExpression(String path, IListenableExpression<?> ep) {
		this.expressions.put(path, ep);
	}

	protected void removeBinding(String id) {
		final Binding binding = this.childBindings.remove(id);

		this.removeChild(binding);
		if (binding != null) {
			binding.removeValueListener(this.childValueListener);
			binding.hParent=null;
		}
		
	}

	public void removeBindingChangeListener(IBindingChangeListener<?> listener) {
		this.listeners.remove(listener);
	}

	protected void removeChild(Binding binding) {

	}

	public void removeCommitListener(ICommitListener l) {
		if (commitListeners != null) {
			commitListeners.remove(l);
		}
	}

	public void removeExpression(String exp) {
		this.expressions.remove(exp);
	}

	public void removeStatusChangeListener(IStatusChangeListener listener) {
		this.slisteners.remove(listener);
	}

	public void removeValidator(IValidator<?> validator) {
		this.validators.remove(validator);
		validate(this.getValue());
	}

	public void removeValueListener(IValueListener<?> listener) {
		this.vlisteners.remove(listener);
	}

	@HandlesAttributeDirectly("addParentToScope")
	public void setAddParentToScope(boolean addParentToScope) {
		this.addParentToScope = addParentToScope;
	}

	@HandlesAttributeDirectly("autoCommit")
	public void setAutoCommit(boolean b) {
		this.isAutoCommit = b;
		for (final Binding ba : this.childBindings.values()) {
			ba.setAutoCommit(b);
		}
	}
	public void setBinding(String attribute, Binding binding) {
		final Binding binding2 = this.childBindings.get(attribute);
		if (binding.getParent() != this) {
			binding.setParent(this);
		}
		if ((binding2 != null) && (binding2 != binding)) {
			binding2.dispose();
		}
		this.childBindings.put(attribute, binding);
		binding.hParent=this;
	}

	@HandlesAttributeDirectly("commitOnErrors")
	public void setCommitErrors(boolean commitErrors) {
		this.commitErrors = commitErrors;
	}

	public final void setupStatus(CodeAndMessage newCode) {
		if (!this.status.equals(newCode)) {
			this.status = newCode;
			this.notifyStatusChange();
		}
	}

	public void setValue(Object value) {
		setValue(value, null);
	}

	public void setValue(Object value, IBindingChangeListener<?> client) {

		if (this.value == value) {
			return;
		}
		boolean ls = this.isReadOnly();
		if (value instanceof Collection<?>) {
			final Collection<?> c = (Collection<?>) value;
			if (c.size() == 1) {
				value = c.iterator().next();
			}
		}
		if (this.value == value) {
			return;
		}
		internalSet(value, client, ls);

	}

	protected abstract void setValueSilent(Object value2);

	public boolean shouldCommit() {
		if (status == null) {
			status = CodeAndMessage.OK_MESSAGE;
		}
		return ((this.status.getCode() != CodeAndMessage.ERROR) || this
				.isCommitErrors()) && this.isAutoCommit();
	}

	protected void startCommit(IBinding bnd) {
		BindingStack.callStarted(bnd);
	}

	@Override
	public ISubsitutableExpression<Object> substituteAllExcept(
			IListenableExpression<?> ve) {
		return new ConstantExpression(this.getValue());
	}

	public void switchParsingMode(boolean enableParsing) {
		this.enableParsing = enableParsing;
	}
	@SuppressWarnings("unchecked")
	protected void validate(Object value) {
		Object ov=this.value;
		try{
		this.value=value;
		final IValidator memberValidator = this.getValidator();
		CodeAndMessage newCode = CodeAndMessage.OK_MESSAGE;
		if (memberValidator != null) {

			if (value instanceof Collection) {
				if (this.allowsMultiValues()) {
					newCode = memberValidator.isValid(this, (Collection) value);
				} else {
					final Collection c = (Collection) value;
					if (c.isEmpty()) {
						newCode = memberValidator.isValid(this, null);
					} else {
						newCode = memberValidator.isValid(this, c.iterator()
								.next());
					}
				}
			} else {
				if (this.allowsMultiValues()) {
					newCode = memberValidator.isValid(this,
							Collections.singleton(value));
				} else {
					newCode = memberValidator.isValid(this, value);
				}
			}

		}
		for (final IValidator<Object> c : this.validators) {
			if (value instanceof Collection) {
				final CodeAndMessage valid = c
						.isValid(this, (Collection) value);
				if (valid.getCode() > newCode.getCode()) {
					newCode = valid;
				}
			} else {
				final CodeAndMessage valid = c.isValid(this, value);
				if (newCode != null && valid.getCode() > newCode.getCode()) {
					newCode = valid;
				}
			}
		}
		this.setupStatus(newCode);
		}finally{
		this.value=ov;	
		}
	}
}
