package com.onpositive.semantic.model.ui.generic;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.commons.xml.language.HandlesAttributeIndirectly;
import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.command.FixedTargetCommandFactory;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.IEditableExpression;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.ExpressionValueProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.realm.IDescribableToQuery;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingSetListener;
import com.onpositive.semantic.model.expressions.impl.ExpressionParserV2;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.richtext.GenericLabelProvider;
import com.onpositive.semantic.model.ui.richtext.IRichLabelProvider;
import com.onpositive.semantic.model.ui.richtext.RichLabelAccess;
import com.onpositive.semantic.model.ui.richtext.StyledString;
import com.onpositive.semantic.model.ui.roles.IImageDescriptorProvider;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;

public class Column extends GenericLabelProvider {

	private static final StyledString STYLED_STRING = new StyledString();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ColumnLayoutData layoutData = new ColumnLayoutData(1, true);

	private String caption;

	private String image;

	private String description;

	private boolean movable = true;

	private boolean resizable = true;

	protected String id;

	protected IProperty property;

	private boolean hasImage = true;

	private boolean isImageFromBase;

	private boolean isTextFromBase;

	boolean isEditable = true;

	protected IColumnContoller controller;

	protected boolean cacheElements;

	private final HashMap<Object, Object> cache = new HashMap<Object, Object>();

	@HandlesAttributeIndirectly({ "initialWidth", "resizeWeight" })
	public ColumnLayoutData getLayoutData() {
		return this.layoutData;
	}

	public void setLayoutData(ColumnLayoutData layoutData) {
		this.layoutData = layoutData;
	}

	public Column() {
	}

	public void setController(IColumnContoller createController) {
		createController.setText(this.getCaption());
		createController.setImage(this.getImage());
		createController.setMovable(this.movable);
		createController.setResizable(this.resizable);
		createController.setTooltipText(this.description);
		this.controller = createController;
		controller.setLayoutData(this.layoutData);
		controller.initEditing(this);
	}

	public Column(String name, String id2) {
		this.caption = name;
		this.description = name;
		setId(id2);
	}

	public boolean isCacheElements() {
		return this.cacheElements;
	}

	public void setCacheElements(boolean cacheElements) {
		this.cacheElements = cacheElements;
	}

	public void clearCache() {

		this.cache.clear();
	}

	private void clearFromCache(Object o) {
		if (o instanceof ITreeNode<?>) {
			final ITreeNode<?> node = (ITreeNode<?>) o;
			final ITreeNode<?>[] children = node.getChildren();
			if (children != null) {
				for (final ITreeNode<?> n : children) {
					this.clearFromCache(n);
				}
			}
		}
		this.cache.remove(o);
	}

	private Comparator comparator;

	private boolean hasText = true;

	@HandlesAttributeDirectly("hasText")
	public void setHasText(boolean parseBoolean) {
		this.hasText = parseBoolean;
	}

	public boolean hasText() {
		return this.hasText;
	}

	@SuppressWarnings("rawtypes")
	public Comparator getRealComparator() {
		if (this.comparator != null) {
			return (Comparator) this.comparator;
		}
		this.comparator = new ColumnComparator();
		return (Comparator) this.comparator;
	}

	@SuppressWarnings("unchecked")
	public void changed(Object extraData) {
		if (extraData instanceof ISetDelta<?>) {
			final ISetDelta<Object> cm = (ISetDelta<Object>) extraData;
			final Collection<?> changedElements = cm.getChangedElements();
			final Collection<?> rmElements = cm.getRemovedElements();
			final Collection<?> aElements = cm.getAddedElements();
			if (changedElements.size() + rmElements.size() + aElements.size() < this.cache
					.size() / 10) {
				for (final Object o : changedElements) {
					this.clearFromCache(o);
				}
				for (final Object o : rmElements) {
					this.clearFromCache(o);
				}
				for (final Object o : aElements) {
					this.clearFromCache(o);
				}
			} else {
				this.cache.clear();
			}
		} else {
			this.cache.clear();
		}
	}

	public Object getElement(Object item) {

		if (this.cacheElements) {
			final Object object = this.cache.get(item);
			if (object != null) {
				return object;
			}
			if (!this.cache.containsKey(item)) {
				final Object value = this.internalGet(item);

				this.cache.put(item, value);
				return value;
			}
			return null;
		}
		return this.internalGet(item);
	}

	private final class ColumnComparator implements Comparator<Object>,
			IDescribableToQuery {

		public int compare(Object e1, Object e2) {
			final int rc = this.rC(e1, e2);
			if (rc == 0) {
				final int d = System.identityHashCode(e1)
						- System.identityHashCode(e2);
				return d;
			}
			return rc;
		}

		@SuppressWarnings("unchecked")
		private int rC(Object e1, Object e2) {
			final Object it1 = getElement(e1);
			final Object it2 = getElement(e2);
			if ((it1 == null) && (it2 == null)) {
				if (e1 instanceof ITreeNode<?>) {
					e1 = ((ITreeNode<?>) e1).getElement();
				}
				if (e2 instanceof ITreeNode<?>) {
					e2 = ((ITreeNode<?>) e2).getElement();
				}
				if ((e1 instanceof Comparable) && (e2 instanceof Comparable)) {
					final Comparable<Object> c1 = (Comparable<Object>) e1;
					final Comparable<Object> c2 = (Comparable<Object>) e2;
					return c1.compareTo(c2);
				}
			}
			if ((it1 instanceof Comparable) && (it2 instanceof Comparable)) {
				final Comparable<Object> c1 = (Comparable<Object>) it1;
				final Comparable<Object> c2 = (Comparable<Object>) it2;
				try {
					return c1.compareTo(c2);
				} catch (final ClassCastException e) {
					// silently ignore
				}
			}
			final String t1 = LabelAccess.getLabel(Column.this, e1, it1);
			final String t2 = LabelAccess.getLabel(Column.this, e2, it2);
			return t1.compareTo(t2);
		}

		@Override
		public boolean adapt(Query query) {
			if (!ExpressionAccess.isExpression(id)) {
				query.setSorting(id);
				return true;
			}
			return false;
		}

	}

	public boolean isTextFromBase() {
		return this.isTextFromBase;
	}

	@HandlesAttributeDirectly("textFromBase")
	public void setTextFromBase(boolean isTextFromBase) {
		this.isTextFromBase = isTextFromBase;
	}

	public boolean isEditable() {
		return this.isEditable;
	}

	public String getId() {
		return this.id;
	}

	@HandlesAttributeDirectly("id")
	public void setId(String id) {
		this.id = id;
		this.property=null;
		recreateExpressionIfNeeded();
	}

	@HandlesAttributeDirectly("editable")
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public String getRole() {
		return this
				.getSingleValue(DefaultMetaKeys.ROLE_KEY, String.class, null);
	}

	@HandlesAttributeDirectly("role")
	public void setRole(String role) {
		this.putMeta(DefaultMetaKeys.ROLE_KEY, role);
	}

	@HandlesAttributeDirectly("resizable")
	public void setResizable(boolean resizable) {
		if (this.controller != null) {
			this.controller.setResizable(resizable);
		}
		this.resizable = resizable;
	}

	public String getImage() {
		
		return this.image;
	}

	@HandlesAttributeDirectly("icon")
	public void setImage(String image) {
		if (this.controller != null) {
			this.controller.setImage(image);
		}
		this.image = image;
	}

	public String getTheme() {
		return DefaultMetaKeys.getStringValue(this, DefaultMetaKeys.ROLE_KEY);
	}

	@HandlesAttributeDirectly("theme")
	public void setTheme(String theme) {
		putMeta(DefaultMetaKeys.THEME_KEY, theme);
	}

	public boolean isHasImage() {
		return this.hasImage;
	}

	@HandlesAttributeDirectly("caption")
	public void setCaption(String caption) {
		if (this.controller != null) {
			this.controller.setText(caption);
		}
		this.caption = caption;
	}

	public String getDescription() {
		if (this.description == null) {
			if (this.property != null) {
				return DefaultMetaKeys.getDescription(property);
			}
			return "";
		}
		return this.description;
	}

	@HandlesAttributeDirectly("description")
	public void setDescription(String description) {
		if (this.controller != null) {
			this.controller.setTooltipText(description);
		}
		this.description = description;
	}

	public boolean isMovable() {
		return this.movable;
	}

	@HandlesAttributeDirectly("movable")
	public void setMovable(boolean movable) {
		if (this.controller != null) {
			this.controller.setMovable(movable);
		}
		this.movable = movable;
	}

	public boolean isResizable() {
		return this.resizable;
	}

	@HandlesAttributeDirectly("hasImage")
	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}

	public String getCaption() {
		if (this.caption == null) {
			if (this.property != null) {
				return DefaultMetaKeys.getCaption(property);
			}
			return "";
		}
		return this.caption;
	}

	public IProperty getProperty() {
		return this.property;
	}

	public void setProperty(IProperty property) {
		this.property = property;
	}

	public boolean hasImage() {
		return this.hasImage;
	}

	public boolean isImageFromBase() {
		return this.isImageFromBase;
	}

	@HandlesAttributeDirectly("imageFromBase")
	public void setImageFromBase(boolean isImageFromBase) {
		this.isImageFromBase = isImageFromBase;
	}

	IImageDescriptorProvider imageProvider;

	ITextLabelProvider labelProvider;

	protected IListElement<?> ownerSelector;

	@HandlesAttributeDirectly("imageProvider")
	public void setImageProvider(IImageDescriptorProvider newInstance) {
		this.imageProvider = newInstance;
	}

	public IImageDescriptorProvider getImageProvider() {
		return imageProvider;
	}

	@HandlesAttributeDirectly("textLabelProvider")
	public void setLabelProvider(ITextLabelProvider newInstance) {
		this.labelProvider = newInstance;
	}

	protected Object internalGet(Object item) {
		ITreeNode<?> node = null;
		if (item instanceof ITreeNode<?>) {
			node = (ITreeNode<?>) item;
			item = node.getElement();
		}
		if (item instanceof IClusterizationPoint<?>) {
			return returnClustetizationValue(item, node);
		}
		if (ownerSelector!=null)
		{
			IBinding binding = ownerSelector.getBinding();
//			if (binding instanceof IHasWorkingCopyManager){
//				IHasWorkingCopyManager q=(IHasWorkingCopyManager) binding;
//				WorkingCopyManager workingCopyManager = q.getWorkingCopyManager();
//				if(workingCopyManager!=null){
//				item=workingCopyManager.getWorkingCopyOrOriginal(item);
//				}
//			}
		}
		if (this.property != null) {
			return extractValue(property, item);
		} else {
			if (this.id != null) {
				Object lookupAndGetValue = lookupAndGetValue(item);

				return lookupAndGetValue;
			}
		}

		return item;
	}

	private Object returnClustetizationValue(Object item, ITreeNode<?> node) {
		final IClusterizationPoint<?> point = (IClusterizationPoint<?>) item;
		Object primaryValue = point.getPrimaryValue();
		if (primaryValue != null) {
			Object lookupAndGetValue = lookupAndGetValue(primaryValue);
			if (lookupAndGetValue != this) {
				return lookupAndGetValue;
			}
		}
		final IPropertyProvider propertyProvider = point
				.getAdapter(IPropertyProvider.class);
		if (propertyProvider != null) {
			final IProperty prop = (IProperty) propertyProvider.getProperty(
					item, this.id);
			if (prop != null) {
				return extractValue(prop, node);
			}
		}
		return null;
	}

	// protected IListenableExpression<Object> expression;
	protected Binding root = new Binding("");

	IValueListener<Object> externalExprsssionListener = new IValueListener<Object>() {

		public void valueChanged(Object oldValue, Object newValue) {
			refreshParent();
		}
	};

	HashSet<IListenableExpression<?>> exps = new HashSet<IListenableExpression<?>>();
	static Binding nullBinding = new Binding(null);
	IExpressionEnvironment expressionBuilder = new IExpressionEnvironment() {

		private static final long serialVersionUID = 44284480788945542L;

		public IListenableExpression<?> getBinding(String path) {
			if (!path.startsWith("$")) {

				final IListenableExpression<Object> binding = root
						.getBinding(path);
				if (binding instanceof IBinding) {
					final IMeta m = ((IBinding) binding).getMeta();
					if (m instanceof BaseMeta) {
						BaseMeta mm = (BaseMeta) m;
						final FixedTargetCommandFactory object = new FixedTargetCommandFactory() {
							/**
							 * 
							 */
							private static final long serialVersionUID = 5590414762331486820L;

							protected Object getTarget() {
								return ((IBinding) binding).getObject();
							};

							public IHasCommandExecutor getExecutor() {
								IHasCommandExecutor mm = DefaultMetaKeys
										.getService(m.getDefaultMeta(),
												IHasCommandExecutor.class);
								if (mm == null) {
									mm = DefaultMetaKeys.getService(
											m.getParentMeta(),
											IHasCommandExecutor.class);
								}
								return mm;
							};

						};
						mm.registerService(ICommandFactory.class, object);
						// FIXME
						mm.registerService(IHasCommandExecutor.class,
								new IHasCommandExecutor() {

									/**
									 * 
									 */
									private static final long serialVersionUID = -8000017396449992073L;

									@Override
									public ICommandFactory getCommandFactory() {
										return object;
									}

									@Override
									public ICommandExecutor getCommandExecutor() {
										IHasCommandExecutor mm = DefaultMetaKeys.getService(
												m.getDefaultMeta(),
												IHasCommandExecutor.class);
										if (mm == null) {
											mm = DefaultMetaKeys.getService(
													m.getDefaultMeta(),
													IHasCommandExecutor.class);
										}
										return mm.getCommandExecutor();
									}
								});
					}
				}
				return binding;
			}
			if (ownerSelector == null)
				return nullBinding;

			IBinding binding2 = ownerSelector.getBinding();
			if (binding2 != null) {
				if (path.equals("$$")){
					return binding2.getRoot();
				}
				IListenableExpression<Object> binding = path.equals("$") ? binding2
						: binding2.getBinding(path.substring(2));

				binding.addValueListener(externalExprsssionListener);
				exps.add(binding);
				return binding;
			}
			return nullBinding;
		}

		public IClassResolver getClassResolver() {
			return getOwnerSelector().getBinding().getClassResolver();
		}
	};

	IBindingSetListener bingingSetListener = new IBindingSetListener() {

		public void bindingChanged(IBindable element, IBinding newBinding,
				IBinding oldBinding) {
			for (IListenableExpression<?> l : exps) {
				l.removeValueListener(externalExprsssionListener);
			}
			property=null;
			recreateExpressionIfNeeded();
			refreshParent();
		}
	};

	private Object lookupAndGetValue(Object primaryValue) {
		// if (expression != null) {
		// // root.setObject(primaryValue);
		// root.setValue(primaryValue, null);
		// return expression.getValue();
		// }
		if (primaryValue == IN_PROGRESS) {
			return "...";
		}
		IPropertyProvider propertyProvider = PropertyAccess
				.getPropertyProvider(primaryValue);
		if (propertyProvider != null) {
			final IProperty prop = (IProperty) propertyProvider.getProperty(
					primaryValue, this.id);
			if (prop != null) {
				return extractValue(prop, primaryValue);
			}
		}
		return this;
	}

	protected void recreateExpressionIfNeeded() {
		if (id != null) {

			if (ExpressionAccess.isExpression(id)) {
				ExpressionParserV2 parser = new ExpressionParserV2();
				IListenableExpression<?> parse = null;
				try {
					parse = parser.parse(id, expressionBuilder, null);
				} catch (Exception e) {

				}
				// if (this.expression != null)
				// this.expression.disposeExpression();
				//
				// this.expression = (IListenableExpression<Object>) parse;

				if (parse instanceof IEditableExpression<?>) {
					if (this.property != null) {
						ExpressionValueProperty vl = (ExpressionValueProperty) this.property;
						vl.setExpression(parse);

					} else {
						ExpressionValueProperty vl = new ExpressionValueProperty(
								root, parse);
						vl.setParentContext(this.root);
						this.property = vl;
					}
				}
			} 
		}
	}

	public static final Object IN_PROGRESS = new String("...");

	private Object extractValue(final IProperty prop, Object c) {
		Object value = prop.getValue(c);
		if (value instanceof Object[]) {
			Object[] vl = (Object[]) value;
			if (vl.length == 1) {
				return vl[0];
			}
			if (vl.length == 0) {
				return null;
			}
		}
		if (value instanceof Collection) {
			Collection cl = (Collection) value;
			if (cl.size() == 1) {
				return cl.iterator().next();
			}
			if (cl.isEmpty()) {
				return null;
			}
		}
		return value;
	}

	public int getWidth() {
		if (this.controller != null) {
			return this.controller.getWidth();
		}

		return layoutData.width;
	}

	public ITextLabelProvider getLabelProvider() {
		return labelProvider;
	}

	public IColumnContoller getController() {
		return controller;
	}

	public IListElement<?> getOwnerSelector() {
		return ownerSelector;
	}

	public void setValue(Object element, Object value) {
		IProperty prop = null;
		Object value2 = getElement(element);
		if (cacheElements){
			clearFromCache(element);
		}
		if (value2 != null && value2.equals(value)) {
			return;
		}
		// if (expression != null) {
		// if (expression instanceof IEditable){
		// IEditable ed=(IEditable) expression;
		// if (ed.isReadOnly()){
		// return ;
		// }
		// ed.setValue(value);
		// }
		// return;
		// }
		if (this.ownerSelector != null && this.ownerSelector.isAsTree()
				&& (element instanceof ITreeNode<?>)) {
			final ITreeNode<?> node = (ITreeNode<?>) element;
			final Object item = node.getElement();
			if (item instanceof IClusterizationPoint<?>) {
				final IClusterizationPoint<?> point = (IClusterizationPoint<?>) item;
				Object primaryValue = point.getPrimaryValue();
				if (primaryValue != null) {
					final IPropertyProvider propertyProvider = PropertyAccess
							.getPropertyProvider(primaryValue);
					if (propertyProvider != null) {
						prop = (IProperty) propertyProvider.getProperty(
								primaryValue, this.id);

					}
					if (prop != null) {
						internalSet(primaryValue, value, prop);
					}
					element = LabelAccess.getPresentationObject(element);
					if (ownerSelector instanceof IColumnValueListener) {
						((IColumnValueListener) this.ownerSelector)
								.editorValueApplied(element, value);
					}
					return;
				} else {
					final IPropertyProvider propertyProvider = point
							.getAdapter(IPropertyProvider.class);
					if (propertyProvider != null) {
						prop = (IProperty) propertyProvider.getProperty(item,
								this.id);

					}
				}
			} else {
				element = item;
				if (this.property != null) {
					prop = this.property;
				} else {
					final IPropertyProvider propertyProvider = PropertyAccess
							.getPropertyProvider(element);
					if (propertyProvider != null) {
						prop = (IProperty) propertyProvider.getProperty(
								element, this.id);
					}
				}
			}
		} else {
			element = LabelAccess.getPresentationObject(element);
			if (this.property != null) {
				prop = this.property;
			} else {
				final IPropertyProvider propertyProvider = PropertyAccess
						.getPropertyProvider(element);
				if (propertyProvider != null) {
					prop = (IProperty) propertyProvider.getProperty(element,
							this.id);
				}
			}
		}
		if (prop != null) {
			internalSet(element, value, prop);
		}
		if (this.ownerSelector != null
				&& ownerSelector instanceof IColumnValueListener) {
			((IColumnValueListener) this.ownerSelector).editorValueApplied(
					element, value);
		}
	}

	private void internalSet(Object element, Object value, IProperty prop) {
		Object undoContext = null;
		AbstractBinding pbinding=null;
		if (this.ownerSelector != null) {
			pbinding = (AbstractBinding) this.ownerSelector.getBinding();
//			if (pbinding instanceof IHasWorkingCopyManager) {
//				IHasWorkingCopyManager m = (IHasWorkingCopyManager) pbinding;
//				if (m.isWorkingCopiesEnabled()) {
//					Object orCreateWorkingCopy = m.getWorkingCopyManager()
//							.getOrCreateWorkingCopy(element);
//					element=orCreateWorkingCopy;
//					ICommand createSetValueCommand = PropertyAccess.createSetValueCommand(element,value,prop);
//					createSetValueCommand.getCommandExecutor().execute(createSetValueCommand);
//					m.getWorkingCopyManager().recordChanges(element, createSetValueCommand);					
//					return;
//				}
//				
//			}
			undoContext = pbinding.getUndoContext();
		}
		
		final Binding bnd = new Binding(element, prop, null);
		bnd.setUndoContext(undoContext);
		bnd.setAutoCommit(true);
		bnd.setValue(value, null);
		bnd.dispose();
		if (pbinding!=null){
			pbinding.onChildChanged();			
		}
	}

	public void setOwnerSelector(IListElement<?> ownerSelector) {
		if (this.ownerSelector != null) {
			this.ownerSelector.removeBindingSetListener(bingingSetListener);
		}
		this.ownerSelector = ownerSelector;
		if (this.property == null&&ownerSelector!=null) {
			final IRealm ps = ownerSelector.getRealm();
			if (ps != null) {
				IProperty property2 = PropertyAccess.getProperty(ps, this.id);
				if (property2 != null) {
					this.property = property2;
				}
				Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(ps);
				if (subjectClass!=null&&subjectClass!=Object.class){
					property2 = PropertyAccess.getProperty(subjectClass, this.id);
					if (property2 != null) {
						this.property = property2;
					}	
				}
			}
		}
		if (ownerSelector!=null){
		this.cacheElements =ownerSelector!=null&& !ownerSelector.isAsCheckBox();
		ownerSelector.addBindingSetListener(bingingSetListener);
		}
		recreateExpressionIfNeeded();

	}

	public boolean canActuallyEdit(Object element) {

		if (this.ownerSelector != null && this.ownerSelector.isAsTree()) {
			if (element instanceof ITreeNode<?>) {
				final ITreeNode<?> node = (ITreeNode<?>) element;
				final Object item = node.getElement();
				if (item instanceof IClusterizationPoint<?>) {
					final IClusterizationPoint<?> point = (IClusterizationPoint<?>) item;
					final IPropertyProvider propertyProvider = point
							.getAdapter(IPropertyProvider.class);
					if (propertyProvider != null) {
						Object primaryValue = point.getPrimaryValue();
						if (primaryValue != null) {
							if (!this.ownerSelector.isAllowCellEditing()) {
								return false;
							}
							if (property != null) {
								return !PropertyAccess.isReadonly(property,
										item);
							}
							return true;
						}
						final IProperty prop = (IProperty) propertyProvider
								.getProperty(item, this.id);
						if (prop != null) {
							return !PropertyAccess.isReadonly(prop, item);
						}
						return false;
					}
					return false;
				} else {
					element = item;
				}
			}
		}
		if (this.ownerSelector != null) {
			if (!this.ownerSelector.isAllowCellEditing()) {
				return false;
			}
		}
		if (this.property != null) {
			if (PropertyAccess.isReadonly(property, element)) {
				return false;
			}
		}
		// if (expression != null) {
		// if (expression instanceof IEditable){
		// IEditable ed=(IEditable) expression;
		// return !ed.isReadOnly();
		// }
		// return false;
		// }
		IProperty property = this.property != null ? this.property
				: PropertyAccess.getProperty(element, this.getId());
		if (property != null) {
			if (PropertyAccess.isReadonly(property, element)) {
				return false;
			}
			return true;
		}
		return false;
	}

	protected Object cellEditorFactory;

	public Object getCellEditorFactory() {
		return null;
	}

	@HandlesAttributeDirectly("cellEditorFactory")
	public void setCellEditorFactory(Object factory) {
		this.cellEditorFactory = factory;
	}

	protected Object renderer;

	public Object getRenderer() {
		return renderer;
	}

	public void setRenderer(Object renderer) {
		this.renderer = renderer;
		if (ownerSelector != null) {
			ownerSelector.redraw();
		}
	}

	private void refreshParent() {
		if (this.cacheElements) {
			cache.clear();
		}
		ownerSelector.refresh();
	}

	public IHasMeta meta() {
		if (property != null) {
			return property.getMeta();
		}
		return null;
	}

	public IMeta getMeta() {
		return this;
	}
	
	public ImageDescriptor getImage(Object item){
		Column column=this;
		
		ImageDescriptor im=null;
		if (column.hasImage()) {
			
			final Object element = column.getElement(item);
			if (!column.isImageFromBase()) {
				IImageDescriptorProvider imageProvider = column
						.getImageProvider();
				if (item instanceof ITreeNode<?>) {
					final ITreeNode node = (ITreeNode<?>) item;
					final INodeLabelProvider adapter = node
							.getAdapter(INodeLabelProvider.class);
					if (adapter != null) {
						final ImageDescriptor image = ImageManager.getInstance().getImageDescriptor(
								element, this.getRole(), this.getTheme());
						if (image != null) {
							im = image;
						} else {
							if (imageProvider != null) {
								im = imageProvider
										.getImageDescriptor(element);
							} else {
								im = super.getActualImage(element,ownerSelector,false);
							}
						}
					} else {
						if (imageProvider != null) {
							im = imageProvider
									.getImageDescriptor(element);
						} else {
							im = super.getActualImage(element,ownerSelector,false);							
						}
					}
				} else {
					if (imageProvider != null) {
						im = imageProvider
								.getImageDescriptor(element);
					} else {
						im = super.getActualImage(element,ownerSelector,false);

					}
				}
			} else {
				IImageDescriptorProvider imDescriptorProvider = column
						.getImageProvider();
				if (imDescriptorProvider != null) {
					im = imDescriptorProvider
							.getImageDescriptor(element);
				} else {
					im = super.getActualImage(item,ownerSelector,true);
				}
			}
		}
		return im;		
	}
	
	
	public StyledString getRichTextLabel(Object object) {
		final Column column = this;
		if (!column.hasText()) {
			return null;
		}

		ITextLabelProvider labelProvider = column.getLabelProvider();
		if (labelProvider != null) {
			if (labelProvider instanceof IRichLabelProvider) {
				IRichLabelProvider ta = (IRichLabelProvider) labelProvider;
				return ta.getRichTextLabel(object);
			}
			return new StyledString(labelProvider.getText(column.meta(),
					ownerSelector.getParentObject(),column.getElement(object)));
		}
		final Object element = column.getElement(object);
		if (DefaultMetaKeys.getValue(MetaAccess.getMeta(element),DefaultMetaKeys.IMAGE_IS_ENOUGH_IN_CELLS)){
			if (column.hasImage()&&!column.isImageFromBase()&&!column.isTextFromBase()){
				if (super.getActualImage(element,this.ownerSelector,false)!=null){
					return STYLED_STRING;
				}
			}
		}
		
		if (object instanceof ITreeNode<?>) {
			final ITreeNode<?> node = (ITreeNode<?>) object;
			final INodeLabelProvider adapter = node
					.getAdapter(INodeLabelProvider.class);
			if (adapter != null) {
				final StyledString richText = adapter.getRichText(node,
						element, getRole(), getTheme(), column);
				if (richText != null) {
					return richText;
				}
			}
		}
		if (column.isTextFromBase()) {
			final StyledString richTextLabel = super.getRichTextLabel(object,null);
			return richTextLabel;
		}
		final StyledString richTextLabel = RichLabelAccess.getLabel(column,
				object, element);
		return richTextLabel;
	}
	
	protected String getTheme(IListElement<?> ls) {
		if (getTheme()!=null){
			return getTheme();
		}
		return super.getTheme(ls);
	}

	protected String getRole(IListElement<?> ls) {
		if (getRole()!=null){
			return getRole();
		}
		
		return super.getRole(ls);
	}

	public Class<?> getType(Object object) {
		Object element = getElement(object);
		if (element!=null){
			return element.getClass();
		}
		if (property!=null){
			return DefaultMetaKeys.getSubjectClass(property);
		}
		return String.class;
	}
	
	@Override
	public String toString() {
		
		return getId()+"("+getCaption()+")";
	}
}
