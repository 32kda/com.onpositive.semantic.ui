package com.onpositive.semantic.model.ui.generic.widgets.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.commons.xml.language.HandlesAttributeIndirectly;
import com.onpositive.commons.xml.language.HandlesParent;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.status.IStatusChangeListener;
import com.onpositive.semantic.model.api.undo.IUndoManager;
import com.onpositive.semantic.model.api.undo.support.UndoRedoSupport;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.binding.IBindingSetListener;
import com.onpositive.semantic.model.ui.actions.ContributionManager;
import com.onpositive.semantic.model.ui.actions.IContributionManager;
import com.onpositive.semantic.model.ui.generic.ComponentEnablementController;
import com.onpositive.semantic.model.ui.generic.ComponentVisibilityController;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.IElementListener;
import com.onpositive.semantic.model.ui.generic.MultythreadIterable;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.BindingExpressionController;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;
import com.onpositive.semantic.ui.core.GenericLayoutHints;
import com.onpositive.semantic.ui.core.IConfigurable;

public abstract class BasicUIElement<T> implements IUIElement<T>, IHasMeta,
		IBindable, IPropertyEditor<BasicUIElement<T>> {
	
	public Object getUndoContext() {
		return null;
	}
	
	
	private final class InternalBindingListener implements
			IBindingChangeListener<Object>, IStatusChangeListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -148003322656513249L;

		public void valueChanged(final ISetDelta<Object> valueElements) {
			BasicUIElement.this.shouldIngoreChanges = true;
			try {
				BasicUIElement.this.processValueChange(valueElements);

			} finally {
				BasicUIElement.this.shouldIngoreChanges = false;
			}
		}

		public void enablementChanged(final boolean isEnabled) {
			if (getEnablement() != null) {
				return;
			}
			if (isCreated()) {
				executeOnUiThread(new Runnable() {

					@Override
					public void run() {
						BasicUIElement.this.setEnabled(isEnabled);
					}
				});
			}
		}

		public void changed() {
			final IBinding binding2 = getBinding();
			BasicUIElement.this.binding = null;
			BasicUIElement.this.setBinding(binding2);
		}

		@Override
		public void statusChanged(IHasStatus bnd, CodeAndMessage cm) {
			onStatus(cm);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final BaseMeta meta = new BaseMeta();

	protected MultythreadIterable<IConfigurable> cs = new MultythreadIterable<IConfigurable>(
			IConfigurable.class);

	protected MultythreadIterable<IElementListener> ls = new MultythreadIterable<IElementListener>(
			IElementListener.class);

	protected IAbstractConfiguration config;

	private GenericLayoutHints layout = new GenericLayoutHints();

	protected final ContributionManager cManager = createContributionManager();

	protected T widget;

	private String description;

	public String getDescription() {
		return description;
	}

	public void onStatus(CodeAndMessage cm) {

	}

	@HandlesAttributeDirectly("description")
	public void setDescription(String description) {
		this.description = description;
	}

	public BasicUIElement() {
		meta.setParentMeta(MetaAccess.getMeta(this.getClass()).getMeta());
	}

	public final String getRole() {
		return (String) getData(ROLE_PROPERTY);
	}

	public final void setRole(String role) {
		setData(ROLE_PROPERTY, role);
	}

	protected String background;
	protected String background_image;
	protected String font;
	protected String foreground;

	private boolean displayable = true;

	private transient boolean inStateChange;

	private String icon;

	protected final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	protected ICompositeElement<?, ?> parent;

	// private HashMap<Class<?>, Object> services = new HashMap<Class<?>,
	// Object>();

	private MultythreadIterable<com.onpositive.semantic.model.ui.generic.widgets.impl.IEnablementListener> enablementListeners;

	private ComponentVisibilityController ev;

	private String visibility;

	private ComponentEnablementController ec;

	private String enablement;

	protected final IElementBehaviorDelegate delegate = createDelegate();

	ImageDescriptor desc;

	private final HashSet<IBindingSetListener> listeners = new HashSet<IBindingSetListener>();

	public final BindingExpressionController controller = new BindingExpressionController(
			this, this);

	protected IBinding binding;

	protected boolean enablementFromBinding = true;

	protected boolean shouldIngoreChanges;

	protected final IBindingChangeListener<Object> binding_chlistener = new InternalBindingListener();

	private boolean ignore;

	public final void setBackground(String background) {
		String background2 = this.background;
		if (background2 == null || !background.equals(background2)) {
			this.background = background;
			firePropertyChange("background", background2, background);
		}
	}

	public String getBackground() {
		return background;
	}

	public final void setForeground(String foreground) {
		String background2 = this.foreground;
		if (!foreground.equals(background2)) {
			this.font = foreground;
			firePropertyChange("foreground", background2, foreground);
			refreshAppearance();
		}
	}

	public final String getForeground() {
		return foreground;
	}

	public final void setBackgroundImage(String image) {
		String background2 = this.background_image;
		if (background_image == null || !background_image.equals(background2)) {
			this.background_image = image;
			firePropertyChange("background_image", background2,
					background_image);
			refreshAppearance();
		}
	}

	public final void setIcon(String image) {
		String background2 = this.icon;
		if (image != null && image.length() > 0) {
			ImageDescriptor imageDescriptor = ImageManager
					.getImageDescriptor(image);
			desc = imageDescriptor;
		}
		if (icon == null || !icon.equals(background2)) {
			this.icon = image;
			firePropertyChange("icon", background2, this.icon);
			refreshAppearance();
		}

	}

	@Override
	public String getIcon() {
		return icon;
	}

	public ImageDescriptor getIconImageDescriptor() {
		return desc;
	}

	protected void refreshAppearance() {

	}

	public String getBackgroundImage() {
		return background_image;
	}

	public final void setFont(String font) {
		String background2 = this.font;
		if (!font.equals(background2)) {
			this.font = font;
			firePropertyChange("font", background2, font);
			refreshAppearance();
		}
	}

	public String getFont() {
		return font;
	}

	@SuppressWarnings({ "rawtypes" })
	protected final void fireAdding(ICompositeElement target) {
		for (final IElementListener l : ls.getArray()) {
			l.elementAdded(target, (BasicUIElement) this);
		}
	}

	protected final void saveConfig() {
		if (this.config != null) {
			this.storeConfiguration(this.config);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	protected void fireHiearachyChange() {
		for (final IElementListener l : ls.getArray()) {
			l.hierarchyChanged((BasicUIElement) this);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	protected final void fireRemoved(ICompositeElement target) {
		for (final IElementListener l : ls.getArray()) {
			l.elementRemoved(target, (BasicUIElement) this);
		}
	}

	protected final void fireDisposed() {
		for (final IElementListener l : ls.getArray()) {
			l.elementDisposed(this);
		}
	}

	protected void fireBindingChanged(IBinding newValue, IBinding oldValue) {
		for (final IElementListener la : ls.getArray()) {
			la.bindingChanged(this, newValue, oldValue);
		}
		for (final IBindingSetListener la : listeners
				.toArray(new IBindingSetListener[listeners.size()])) {
			la.bindingChanged(this, newValue, oldValue);
		}
	}

	protected final void fireCreated() {
		for (final IElementListener la : ls.getArray()) {
			la.elementCreated(this);
		}
	}

	public final String getTheme() {
		return (String) getData(THEME_PROPERTY);
	}

	public T getControl() {
		return widget;
	}

	public final boolean isDisplayable() {
		return displayable;
	}

	public void addConfigurationPart(IConfigurable iConfigurable) {
		cs.add(iConfigurable);
	}

	public void removeConfigurationPart(IConfigurable iConfigurable) {
		cs.remove(iConfigurable);
	}

	public final void addElementListener(IElementListener disposeBindingListener) {
		ls.add(disposeBindingListener);
	}

	public final void removeElementListener(
			IElementListener disposeBindingListener) {
		ls.remove(disposeBindingListener);
	}

	public String getToolTipText() {
		return (String) getData(TOOLTIP_PROPERTY);
	}

	public final GenericLayoutHints getLayoutHints() {
		return layout;
	}

	public final void setLayoutHints(GenericLayoutHints layoutHints) {
		GenericLayoutHints m = layout;
		this.layout = layoutHints;
		firePropertyChange(LAYOUT_HINTS_PROPERTY, m, layoutHints);
	}

	public void setEnabled(boolean val) {
		if (!isEnabled() == val) {
			setData(ENABLED_PROPERTY, val);
			refreshAppearance();
			if (this.enablementListeners != null) {
				for (final IEnablementListener l : enablementListeners
						.getArray()) {
					l.enablementChanged(this, val);
				}
			}
		}
	}

	public final void setDisplayable(boolean displayable) {
		if (this.displayable != displayable) {
			inStateChange = true;
			try {
				// FIXME
				this.displayable = displayable;
				if (parent != null) {
					parent.onDisplayable(this);
				}
			} finally {
				inStateChange = false;
			}
		}
	}

	public boolean isEnabled() {
		return getData(ENABLED_PROPERTY, true);
	}

	private boolean getData(String enabledProperty, boolean b) {
		Boolean bl = (Boolean) getData(enabledProperty);
		return bl != null ? bl : b;
	}

	// private HashMap<Class<?>, Object> services = new HashMap<Class<?>,
	// Object>();

	protected void onCreate(ICompositeElement<?, ?> parent) {
		
		if (!isCreated()) {
	
		startCreate();		
		
		widget = createControl(parent);
		endCreate();
		if (widget==null){
			throw new IllegalStateException();
		}
			
		fireCreated();
		}
	}


	protected void endCreate() {
		if (delegate != null) {
			delegate.onCreateEnd(this);
		}
		this.shouldIngoreChanges = false;
	}

	protected void startCreate() {
		this.shouldIngoreChanges = true;
		if (delegate != null) {
			delegate.onCreateStart(this);
		}
	}

	protected IElementBehaviorDelegate createDelegate() {
		return DelegateFactory.createDelegate(this);
	}

	protected void onDispose() {
		widget = null;
		if (delegate != null) {
			delegate.onDispose(this);
		}
		this.parent = null;
	}

	protected abstract T createControl(ICompositeElement<?, ?> parent);

	public final String getId() {
		return (String) getData(IUIElement.ID_PROPERTY);
	}

	public void setId(String id) {
		setData(ID_PROPERTY, id);
	}

	public void setCaption(String caption) {
		setData(CAPTION_PROPERTY, caption);
	}

	public String getCaption() {
		return (String) getData(CAPTION_PROPERTY);
	}

	public ICompositeElement<?, ?> getParent() {
		return parent;
	}

	//
	// @HandlesParent
	// public void setParent( ICompositeElement<?,?> parent ) {
	// this.parent = parent ;
	// }

	@SuppressWarnings("unchecked")
	public BasicUIElement<T> getRoot() {
		if (parent != null) {
			return ((BasicUIElement<T>) (parent)).getRoot();
		}
		return this;
	}

	public final boolean isCreated() {
		BasicUIElement<T> root = getRoot();
		return root != null && widget != null;
	}

	public String getText() {
		return (String) getData(CAPTION_PROPERTY);
	}

	public final void addEnablementListener(IEnablementListener listener) {
		if (this.enablementListeners == null) {
			this.enablementListeners = new MultythreadIterable<IEnablementListener>(
					IEnablementListener.class);
		}
		this.enablementListeners.add(listener);
	}

	public final void removeEnablementListener(IEnablementListener listener) {
		if (this.enablementListeners != null) {
			this.enablementListeners.remove(listener);
		}
	}

	public void setText(String attribute) {
		setData(CAPTION_PROPERTY, attribute);
	}

	public <R> R getService(Class<R> clazz) {
		if (clazz.isInstance(this)) {
			if (clazz == IPropertyEditor.class) {
				if (getBinding() == null && parent != null) {
					final R service = this.parent.getService(clazz);
					return service;
				}
			}
			if (clazz == IBindable.class) {
				if (getBinding() == null && parent != null) {
					final R service = this.parent.getService(clazz);
					return service;
				}
			}
			return (R) clazz.cast(this);
		}
		if (this.meta != null) {
			final Object object = this.meta.getService(clazz);
			if (object != null) {
				return clazz.cast(object);
			}
		}
		if (this.parent == null) {
			return null;
		}
		final R service = this.parent.getService(clazz);
		return service;
	}

	public <R> void addService(Class<R> service, R serviceInstance) {
		meta.registerService(service, serviceInstance);
		firePropertyChange(SERVICES_PROPERTY, null, serviceInstance);
	}

	public void setToolTipText(String whyBindingIsDisabled) {
		setData(whyBindingIsDisabled, TOOLTIP_PROPERTY);
	}

	public void setCreatePopupMenu(boolean b) {
		setData(HAS_POPUP, b);
	}

	public void setTheme(String theme) {
		setData(THEME_PROPERTY, theme);
	}

	public IMeta getMeta() {
		return meta;
	}

	protected boolean bindingInTitle = true;

	public boolean isBindingInTitle() {
		return this.bindingInTitle;
	}

	@HandlesAttributeDirectly("showValueInTitle")
	public void setBindingInTitle(boolean bindingInTitle) {
		this.bindingInTitle = bindingInTitle;
	}

	@HandlesParent
	public void setBinding(IBinding binding) {
		this.shouldIngoreChanges = true;

		final IBinding old = this.binding;
		if (old == binding) {
			return;
		}
		this.unhookBinding(old);
		if (this.binding != null) {
			this.binding.removeBindingChangeListener(this.binding_chlistener);
			this.binding
					.removeStatusChangeListener((IStatusChangeListener) this.binding_chlistener);
		}
		this.binding = binding;
		if (binding != null) {
			((IWritableMeta) getMeta()).setDefaultMeta(binding.getMeta());
		}
		if (bindingInTitle
				&& ((this.getCaption() == null) || (this.getCaption().length() == 0))) {
			if (binding != null && binding.getName() != null
					&& binding.getName().length() > 0) {
				this.setCaption(binding.getName());
			}
		}
		processEnablement(binding);
		if (binding != null) {
			this.setRole(binding.getRole());
			if (binding.getUndoContext() == null) {
				IUndoManager undoRedoChangeManager = UndoRedoSupport
						.getUndoRedoChangeManager();
				if (undoRedoChangeManager != null) {
					binding.setUndoContext(createUndoContext(binding));
				}
			}
		}
		BasicUIElement.this.internalSetBinding(binding);
		if (binding != null) {
			binding.addBindingChangeListener(this.binding_chlistener);
			this.binding
					.addStatusChangeListener((IStatusChangeListener) this.binding_chlistener);
		}
		beforeBindingSetComplete();
		this.fireBindingChanged(binding, old);
		this.shouldIngoreChanges = false;
	}

	protected void beforeBindingSetComplete() {

	}

	protected Object createUndoContext(IBinding binding) {
		return null;
	}

	protected void internalSetBinding(IBinding binding2) {
		if (delegate != null) {
			delegate.internalSetBinding(binding2);
		}
	}

	public void setValue(Object value) {
		if (delegate != null) {
			delegate.setValue(value);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	protected void unhookBinding(IBinding old) {
		if (this.binding != null) {
			this.binding.removeBindingChangeListener(this.binding_chlistener);
			this.binding
					.removeStatusChangeListener((IStatusChangeListener) this.binding_chlistener);
		}
	}

	public final Object getData(String string) {
		return meta.getSingleValue(string, Object.class, null);
	}

	public final void setData(String key, Object value) {
		Object object = getData(key);
		if (object != null) {
			if (!object.equals(value)) {
				meta.putMeta(key, value);
				firePropertyChange(key, object, value);
			}
		} else {
			if (value != null) {
				meta.putMeta(key, value);
				firePropertyChange(key, object, value);
			}
		}
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public final void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	public final void removePropertyChangeListener(
			PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	public final void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		support.removePropertyChangeListener(propertyName, listener);
	}

	public IContributionManager getPopupMenuManager() {
		return cManager;
	}

	public boolean needsLabel() {
		return false;
	}

	public final void loadConfiguration(IAbstractConfiguration configuration) {
		internalLoadConfiguration(configuration);
		for (final IConfigurable f : this.cs.getArray()) {
			f.loadConfiguration(configuration);
		}
	}

	public final void storeConfiguration(IAbstractConfiguration configuration) {
		internalStoreConfiguration(configuration);
		for (final IConfigurable f : this.cs.getArray()) {
			f.storeConfiguration(configuration);
		}
	}

	protected void internalStoreConfiguration(
			IAbstractConfiguration configuration) {

	}

	protected void internalLoadConfiguration(
			IAbstractConfiguration configuration) {

	}

	public final IAbstractConfiguration getConfiguration() {
		return this.config;
	}

	public final void setConfiguration(IAbstractConfiguration configuration) {
		if (this.config != null) {
			this.storeConfiguration(this.config);
		}
		this.config = configuration;
	}

	protected final void loadConfig() {
		if (this.config != null) {
			this.loadConfiguration(this.config);
		}
	}

	public final void setVisibility(String visibility) {
		if (ev != null) {
			ev.dispose();
		}
		ComponentVisibilityController el = new ComponentVisibilityController(
				this, visibility);
		ev = el;
		addElementListener(el);
		el.hierarchyChanged(this);
		this.visibility = visibility;
	}

	public String getVisibility() {
		return visibility;
	}

	public final void setEnablement(String enablement) {
		if (ec != null) {
			ec.dispose();
		}
		ComponentEnablementController el = new ComponentEnablementController(
				this, enablement);
		ec = el;
		addElementListener(el);
		el.hierarchyChanged(this);
		this.enablement = enablement;
	}

	public final String getEnablement() {
		return enablement;
	}

	public boolean inStateChange() {
		return inStateChange
				|| (parent != null ? parent.inStateChange() : false);
	}

	public void addBindingSetListener(IBindingSetListener ls) {
		this.listeners.add(ls);
	}

	public void removeBindingSetListener(IBindingSetListener ls) {
		this.listeners.remove(ls);
	}

	@HandlesAttributeIndirectly("bindTo")
	public BindingExpressionController getBindingExpressionController() {
		return controller;
	}

	public IHasMeta meta() {
		if (binding != null) {
			return binding;
		}
		return null;
	}

	public IBinding getBinding() {
		return this.binding;
	}

	public Object getParentObject() {
		if (binding != null) {
			return binding.getObject();
		}
		return null;
	}

	protected void commitToBinding(Object newValue) {
		if (this.binding != null) {
			this.binding.setValue(newValue, this.binding_chlistener);
		}
	}

	public boolean isEnablementFromBinding() {
		return enablementFromBinding;
	}

	public void setEnablementFromBinding(boolean enablementFromBinding) {
		this.enablementFromBinding = enablementFromBinding;
	}

	protected boolean isEnabled(IBinding binding) {
		return binding == null || !binding.isReadOnly();
	}

	protected void processEnablement(IBinding binding) {
		if (enablementFromBinding) {
			this.setEnabled(this.isEnabled(binding));
		}
	}

	protected void processValueChange(ISetDelta<?> valueElements) {
		if (delegate != null) {
			delegate.processValueChange(valueElements);
		} else
			throw new UnsupportedOperationException();
	}

	public boolean shouldIgnoreChanges() {
		return this.shouldIngoreChanges || this.ignore;
	}

	protected void dispose() {
		if (!inStateChange) {
			fireDisposed();
		} else {
			fireVisibility();
		}
		unhookBinding(binding);
	}

	protected final void fireVisibility() {
		for (final IElementListener l : ls.getArray()) {
			l.elementVisibilityChanged(this);
		}
	}

	protected void setIgnoreChanges(boolean ignore) {
		this.ignore = ignore;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IUIElement getUIElement() {
		return this;
	}
	
	protected ContributionManager createContributionManager() {
		return new ContributionManager();
	}
}
