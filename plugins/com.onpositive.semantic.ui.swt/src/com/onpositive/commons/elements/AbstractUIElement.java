package com.onpositive.commons.elements;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.ui.appearance.ColorFontAttributes;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.actions.ContributionManager;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IContributionManager;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIComposite;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.model.ui.property.editors.structured.ContributionItemConverter;
import com.onpositive.semantic.model.ui.viewer.IDrawsBorder;
import com.onpositive.semantic.ui.core.IConfigurable;

public abstract class AbstractUIElement<T extends Control> extends
		BasicUIElement<T> implements IConfigurable, IUIElement<T> {

	private transient final ArrayList<Control> allControls = new ArrayList<Control>();

	ColorFontAttributes attrs;

	private String caption = "";

	private boolean createPopupMenu = false;

	private boolean disposing;

	private DisposeListener dlistener;

	private final HashSet<EventListenerPair> elisteners = new HashSet<EventListenerPair>(
			0);

	private boolean hasLabel;

	private Hyperlink hyperLink;

	protected boolean inStateChange;

	private boolean isBordered = false;
	private Label label;
	Action labelAction;
	private Object layoutData;

	protected MenuManager manager = new MenuManager();

	

	boolean redraw = true;

	protected Integer style = null;

	protected String text;
	boolean textIsSet;

	private String tooltip;

	protected String visibility;

	class EventListenerPair implements Listener {
		int event;
		SWTEventListener<T> listener;

		public EventListenerPair(int event, SWTEventListener<T> listener) {
			super();
			this.event = event;
			this.listener = listener;
		}

		void apply() {
			AbstractUIElement.this.getEventControl().addListener(this.event,
					this);
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
			final EventListenerPair other = (EventListenerPair) obj;
			if (this.event != other.event) {
				return false;
			}
			if (this.listener == null) {
				if (other.listener != null) {
					return false;
				}
			} else if (!this.listener.equals(other.listener)) {
				return false;
			}
			return true;
		}

		public void handleEvent(Event event) {
			this.listener.handleEvent(AbstractUIElement.this, event);
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.event;
			result = prime * result
					+ ((this.listener == null) ? 0 : this.listener.hashCode());
			return result;
		}

		public void remove() {
			final Control widget = AbstractUIElement.this.getEventControl();
			if (widget != null) {
				if (!widget.isDisposed()) {
					widget.removeListener(this.event, this);
				}
			}
		}
	}

	static final class Property {
		Method getter;
		String name;
		Method setter;

		public Object get(Object obj) {
			if (this.getter != null) {
				try {
					return this.getter.invoke(obj);
				} catch (final IllegalArgumentException e) {
					throw new IllegalArgumentException(e);
				} catch (final IllegalAccessException e) {
					throw new IllegalArgumentException(e);
				} catch (final InvocationTargetException e) {
					throw new IllegalArgumentException(e);
				}
			}
			return null;
		}

		public boolean set(Object obj, Object value) {
			try {
				if (this.setter != null) {
					this.setter.invoke(obj, value);
					return true;
				}
				return false;
			} catch (final IllegalArgumentException e) {
				throw new IllegalArgumentException(
						"Error while setting property " + this.name, e); //$NON-NLS-1$
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(
						"Error while setting property " + this.name, e); //$NON-NLS-1$
			} catch (final InvocationTargetException e) {
				throw new IllegalArgumentException(
						"Error while setting property " + this.name, e.getCause()); //$NON-NLS-1$
			}
		}

	}

	static class PropertyKey {
		Class<?> cls;

		String name;

		public PropertyKey(Class<?> cls, String name) {
			super();
			this.cls = cls;
			this.name = name;
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
			final PropertyKey other = (PropertyKey) obj;
			if (this.cls == null) {
				if (other.cls != null) {
					return false;
				}
			} else if (!this.cls.equals(other.cls)) {
				return false;
			}
			if (this.name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!this.name.equals(other.name)) {
				return false;
			}

			return true;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((this.cls == null) ? 0 : this.cls.hashCode());
			result = prime * result
					+ ((this.name == null) ? 0 : this.name.hashCode());
			return result;
		}

		public Property toProperty() {
			final Property p = new Property();
			try {
				try {
					p.name = this.name;
					p.getter = this.cls.getMethod(
							"get" + this.name, new Class[] {}); //$NON-NLS-1$
				} catch (final NoSuchMethodException e) {
					p.getter = this.cls.getMethod(
							"is" + this.name, new Class[] {}); //$NON-NLS-1$
				}
				p.setter = this.cls.getMethod(
						"set" + this.name, new Class[] { p.getter //$NON-NLS-1$
								.getReturnType() });
			} catch (final SecurityException e) {

			} catch (final NoSuchMethodException e) {

			}
			return p;
		}

	}

	private static HashMap<PropertyKey, Property> properties = new HashMap<PropertyKey, Property>();

	protected static Object get(Object obj, String name) {
		final PropertyKey key = new PropertyKey(obj.getClass(), name);
		Property property = properties.get(key);
		if (property == null) {
			property = key.toProperty();
			properties.put(key, property);
		}
		return property.get(obj);
	}

	protected static boolean set(Object obj, String name, Object value) {
		final PropertyKey key = new PropertyKey(obj.getClass(), name);
		Property property = properties.get(key);
		if (property == null) {
			property = key.toProperty();
			properties.put(key, property);
		}
		return property.set(obj, value);
	}

	public AbstractUIElement() {
	}

	public AbstractUIElement(String caption) {
		this.caption = caption;
	}

	public AbstractUIElement(String caption, int style) {
		this.caption = caption;
		this.style = style;
	}

	public void addListener(int selection, SWTEventListener<T> eventListener) {
		final EventListenerPair e = new EventListenerPair(selection,
				eventListener);
		this.elisteners.add(e);
		if (this.isCreated()) {
			e.apply();
		}
	}

	private String adjustLabelText(String text) {
		String s = text;
		String trim = s.trim();
		if (!trim.endsWith(":") && s.trim().length() != 0) { //$NON-NLS-1$
			s = s + ":"; //$NON-NLS-1$
		}
		return s;
	}

	private void applyColorsAndFonts() {

	}

	protected void basicCreate() {
		final Composite contentParent = container().getContentParent();
		this.createLabel(container());//
		this.widget = this.createControl(contentParent);

		this.dlistener = new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				AbstractUIElement.this.dispose();
			}

		};

		this.widget.addDisposeListener(this.dlistener);
		if (this.text != null) {
			this.setText(this.text);
		}
		if ((this.caption != null) && (this.caption.length() > 0)) {
			this.setCaption(this.caption);
		}
		for (final EventListenerPair p : this.elisteners) {
			p.apply();
		}
		this.widget.addListener(SWT.Modify, new Listener() {

			public void handleEvent(Event event) {
				AbstractUIElement.this.text = AbstractUIElement.this.getText();
			}

		});
		Control m = getLayoutControl();
		this.registerPart(m);
	}

	public int calcStyle() {
		return this.style != null ? this.style : (this.isBordered ? SWT.BORDER
				: SWT.NONE);
	}

	@SuppressWarnings("unchecked")
	public void create() {
		if (container() == null) {
			throw new UnsupportedOperationException("Parent is not defined"); //$NON-NLS-1$
		}
		if (!container().isCreated()) {
			throw new UnsupportedOperationException("Parent is not created"); //$NON-NLS-1$
		}
		if (this.widget != null) {
			throw new UnsupportedOperationException(
					"Control is already created"); //$NON-NLS-1$
		}
		loadConfig();
		this.internalCreate();
		fireCreated();
		refreshAppearance();
		if (this.manager != null) {
			this.createMenu();
		}
		this.setToolTipText(this.tooltip);

	}

	protected void refreshAppearance() {
		if (isCreated()) {
			if (this.attrs!=null){
				attrs.dispose();
			}
			if ((this.font != null) || (this.background != null)
					|| (this.foreground != null)) {
				
				this.attrs = new ColorFontAttributes(this.font,
						this.background, this.foreground);
				this.attrs.apply(this.widget);
			}
			// if (!this.enbled) {
			for (final Control c : this.allControls) {
				c.setEnabled(isEnabled());
			}
			// }
			if (this.getBackgroundImage() != null) {
				String backgroundImage = this.getBackgroundImage();
				if ((backgroundImage != null) && (backgroundImage.length() > 0)) {
					if (this.isCreated()) {
						final Image image = SWTImageManager
								.getImage(backgroundImage);
						this.getControl().setBackgroundImage(image);
					}
				} else {
					if (this.isCreated()) {
						this.getControl().setBackgroundImage(null);
					}
				}
			}
		}
	}

	protected abstract T createControl(Composite conComposite);

	protected Control createLabel(Composite contentParent) {

		if (this.labelAction != null) {
			this.hyperLink = container().createControlHyperLink(contentParent);
			this.hyperLink.setToolTipText(this.labelAction.getToolTipText());
			// hyperLink.setText(labelAction.getText());
			this.hyperLink.setUnderlined(true);
			this.hyperLink.addHyperlinkListener(new IHyperlinkListener() {

				public void linkActivated(HyperlinkEvent e) {
					AbstractUIElement.this.labelAction.run();
				}

				public void linkEntered(HyperlinkEvent e) {
				}

				public void linkExited(HyperlinkEvent e) {
				}

			});
			// hyperLink.setText(s);
			final IPropertyChangeListener listener = new IPropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent event) {
					AbstractUIElement.this.hyperLink
							.setEnabled(AbstractUIElement.this.labelAction
									.isEnabled());
					AbstractUIElement.this.hyperLink
							.setToolTipText(AbstractUIElement.this.labelAction
									.getToolTipText());
				}

			};
			this.labelAction.addPropertyChangeListener(listener);
			this.hyperLink.setEnabled(this.labelAction.isEnabled());
			this.hyperLink.addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					AbstractUIElement.this.labelAction
							.removePropertyChangeListener(listener);
				}

			});
			this.hasLabel = true;
			return this.hyperLink;
		} else {
			final Label ml = container().createControlLabel(contentParent);
			this.label = ml;
			this.hasLabel = true;
			return ml;
		}
	}

	public void createLabel(Container cm) {
		if (!this.hasLabel && this.needsLabel()) {
			this.registerPart(this.createLabel(cm.getContentParent()));
			return;
		}
	}

	protected void createMenu() {
		final Control menuControl = this.getMenuControl();
		final Menu createContextMenu = this.manager
				.createContextMenu(menuControl);
		menuControl.setMenu(createContextMenu);
	}

	@SuppressWarnings("unchecked")
	public void dispose() {
		if (this.disposing) {
			return;
		}
		this.hasLabel = false;
		this.disposing = true;
		if (this.widget != null) {
			if (this.dlistener != null) {
				this.widget.removeDisposeListener(this.dlistener);
			}
			this.dlistener = null;
			saveConfig();
			for (final EventListenerPair p : this.elisteners) {
				p.remove();
			}
			if ((container() != null) && !container().isDisposing()) {
				for (final Control c : new ArrayList<Control>(this.allControls)) {
					c.dispose();
				}
			}

			this.allControls.clear();
			this.widget = null;
			this.disposing = false;
			fireDisposed();
			if (this.manager != null) {
				this.manager.dispose();
			}
			this.textIsSet = false;
		}
	}

	public IMenuManager getActualPopupMenuManager() {
		return manager;
	}

	public Control[] getAllControls() {
		final Control[] cs = new Control[this.allControls.size()];
		this.allControls.toArray(cs);
		return cs;
	}

	public String getCaption() {
		return this.caption;
	};

	public T getControl() {
		return this.widget;
	}

	public Object getDefaultLayoutData() {
		return null;
	}

	protected Control getEventControl() {
		return this.widget;
	}

	public final Control getFirst() {
		return this.allControls.get(0);
	}

	public String getFont() {
		return this.font;
	}

	@Override
	protected T createControl(ICompositeElement<?, ?> parent) {
		return createControl((Container)parent);
	}

	public Label getLabel() {
		return label;
	}

	public Action getLabelAction() {
		return this.labelAction;
	}

	protected Control getLabelControl() {
		if (this.hyperLink != null) {
			return this.hyperLink;
		} else {
			return this.label;
		}
	}

	public final Control getLast() {
		return this.allControls.get(this.allControls.size() - 1);
	}

	public Control getLayoutControl() {
		return widget;
	}

	public Object getLayoutData() {
		return this.layoutData != null ? this.layoutData : this
				.getDefaultLayoutData();
	}

	protected Control getMenuControl() {
		return this.widget;
	}

	public Container getParent() {
		return container();
	}

	public IContributionManager getPopupMenuManager() {
		return new ContributionManager() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void add(IContributionItem item) {
				manager.add(ContributionItemConverter.from(item));
			}

			@Override
			public void addAfter(String id, IContributionItem item) {
				super.addAfter(id, item);
				manager.appendToGroup(id, ContributionItemConverter.from(item));
			}

			public IContributionItem[] getItems() {
				org.eclipse.jface.action.IContributionItem[] items = manager
						.getItems();
				IContributionItem[] rr = new IContributionItem[items.length];
				for (int a = 0; a < items.length; a++) {
					rr[a] = ContributionItemConverter.to(items[a]);
				}
				return rr;
			}

			public void remove(IContributionItem action) {
				manager.remove(ContributionItemConverter.from(action));
			}
		};
	}

	public String getText() {
		if (this.isCreated()) {
			final Object object = get(this.widget, "Text"); //$NON-NLS-1$
			if (object != null) {
				return (String) object;
			}
		}
		return this.text;
	}

	public String getToolTipText() {
		return tooltip;
	}

	protected void initMenuManager() {

	}

	protected void installLayoutData() {
		if (isCreated()) {
			this.widget.setLayoutData(this.getLayoutData());
		}
	}

	protected void internalCreate() {
		basicCreate();

	}

	protected void internalSetCaption() {
		if (this.caption != null) {
			if (this.needsLabel()) {
				this.setLabelText(this.caption);
			} else {
				this.setText(this.caption);
			}

		}
	}

	protected void internalSetText(String txt) {

		if (txt == null) {
			txt = ""; //$NON-NLS-1$
		}
		set(this.widget, "Text", txt); //$NON-NLS-1$
	}

	public boolean isBordered() {
		return this.isBordered;
	}

	public boolean isCreatePopupMenu() {
		return this.createPopupMenu;
	}

	public boolean isDisposing() {
		return this.disposing
				|| ((container() != null) && container().isDisposing());
	}

	protected boolean isRedraw() {
		return this.redraw && this.isCreated()
				&& ((container() == null) || container().isRedraw());
	}

	protected boolean isReparentable() {
		final boolean reparentable = this.getControl().isReparentable();
		if (!reparentable) {
			return false;
		}
		for (final Control c : this.allControls) {
			if (!c.isReparentable()) {
				return false;
			}
		}
		return reparentable;
	}

	public boolean needsLabel() {
		return (this.caption != null) && (this.caption.length() > 0)
				&& !container().handlesLabels();
	}

	protected boolean parentDrawsBorder() {
		if (this.getParent().getService(IDrawsBorder.class) != null) {
			return true;
		}
		return false;
	}

	public void recreate() {
		if (this.widget == null) {
			return;
		}
		final boolean hasFocus = this.getEventControl().isFocusControl();
		final boolean redraw2 = this.getParent().isRedraw();
		if (redraw2) {
			this.getParent().setRedraw(false);
			this.widget.getParent().setLayoutDeferred(true);
		}
		List<AbstractUIElement<?>> children = container().getChildren();

		final int indexOf = children.indexOf(this);
		container().remove(this);

		if (indexOf >= 0) {
			if (children.size() > 1) {
				// this.parent.add(this);
				container().add(indexOf, this);
				if (redraw2) {
					container().getContentParent().layout(true, true);
					T control = this.getControl();
					this.widget.getParent().setLayoutDeferred(false);
					this.getParent().setRedraw(true);
				}
			} else {
				if (redraw2) {
					container().add(this);
					this.widget.getParent().setLayoutDeferred(false);
					this.getParent().setRedraw(true);
				}
			}
		} else {
			throw new RuntimeException();
		}
		if (hasFocus) {
			this.getEventControl().setFocus();
		}

	}

	public void redraw() {
		if (isCreated()) {
			getControl().redraw();
		}
	}

	protected void registerPart(final Control c) {
		c.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				AbstractUIElement.this.allControls.remove(c);
			}
		});
		this.allControls.add(c);
	}

	public void removeListener(int selection, SWTEventListener<T> eventListener) {
		final EventListenerPair e = new EventListenerPair(selection,
				eventListener);
		for (final EventListenerPair l : this.elisteners) {
			if (l.equals(e)) {
				l.remove();
				this.elisteners.remove(e);
				return;
			}
		}
	}

	protected void reparent() {
		if (this.isReparentable()) {
			final Composite contentParent = getContentParent();
			this.widget.setParent(contentParent);
			for (final Control c : this.allControls) {
				c.setParent(contentParent);
			}
		} else {
			this.dispose();
			this.create();
		}
	}

	protected Composite getContentParent() {
		return ((Container)container()).getContentParent();
	}

	@HandlesAttributeDirectly( "hasBorder" )
	public void setBordered(boolean isBordered) {
		if (this.isBordered != isBordered) {
			this.isBordered = isBordered;
			if (this.isCreated()) {
				this.recreate();
			}
		}
	}

	public void setCaption(String string) {
		this.caption = string;
		if (this.isCreated()) {
			this.internalSetCaption();
		}
	}

	public void setCreatePopupMenu(boolean createPopupMenu) {
		this.createPopupMenu = createPopupMenu;
		if (createPopupMenu) {
			if (this.manager == null) {
				this.manager = new MenuManager();
				this.manager.addMenuListener(new IMenuListener() {

					public void menuAboutToShow(IMenuManager manager) {
						manager.update(true);
					}

				});
				this.initMenuManager();
				if (this.isCreated()) {
					final Menu createContextMenu = this.manager
							.createContextMenu(this.widget);
					this.widget.setMenu(createContextMenu);
				}
			}
		} else {
			if (this.manager != null) {
				this.manager.dispose();
				this.manager = null;
			}
		}
	}

	public void setLabelAction(Action labelAction) {
		if (this.isCreated()) {
			throw new IllegalStateException();
		}
		this.labelAction = labelAction;
	}

	protected void setLabelText(String text) {
		text = this.adjustLabelText(text);
		if (this.hyperLink != null) {
			this.hyperLink.setText(text);
			if (this.widget.getParent() != null) {
				this.hyperLink.getParent().layout(
						new Control[] { this.hyperLink });
				this.hyperLink.getParent().redraw();
			}
		} else {
			if (this.label != null) {
				this.label.setText(text);
				if (this.widget.getParent() != null) {
					this.label.getParent().layout(new Control[] { this.label });
					this.label.getParent().redraw();
				}
			}

		}
		if (container().handlesLabels()) {
			throw new UnsupportedOperationException();
		}

	}

	protected Container container() {
		return (Container) this.parent;
	}

	public void setLayoutData(Object layoutData) {
		this.layoutData = layoutData;
		if (this.isCreated()) {
			this.getLayoutControl().setLayoutData(layoutData);
			this.widget.getShell().layout(true);
		}
	}

	protected void setRedraw(boolean redraw) {
		if ((container() != null)
				&& (container().getControl() instanceof TabFolder)) {
			return;
		}
		if (this.isRedraw() != redraw) {
			if (this.widget != null) {
				if (redraw) {
					this.widget.setRedraw(redraw);
				} else {

					this.widget.setRedraw(false);

				}
			}
			this.redraw = redraw;
		}
	}

	public void setText(String txt) {
		if (this.textIsSet && (this.text != null) && (txt != null)) {
			if (this.text.equals(txt)) {
				return;
			}
		}
		this.text = txt;

		if (this.isCreated()) {

			this.textIsSet = true;
			this.internalSetText(txt);
			if (this.widget.getParent() != null) {
				if (this.getParent().isCreated() && !inStateChange()) {
					try {
						this.getParent().getContentParent()
								.layout(new Control[] { this.widget });
						this.widget.getParent().redraw();
					} catch (Exception e) {

					}
				}
			}
		}
	}

	public void setToolTipText(String whyBindingIsDisabled) {
		if ((this.tooltip != null) && this.tooltip.equals(whyBindingIsDisabled)) {
			return;
		}
		this.tooltip = whyBindingIsDisabled;
		if (this.isCreated()) {
			this.getControl().setToolTipText(this.tooltip);
		}
	}

	protected void onRemovedFrom(Container container) {
		this.fireRemoved(container);
		this.fireHiearachyChange();
	}

	protected void onAddedTo(ICompositeElement<?, ?> container) {
		this.parent = container;		
		fireAdding(container);
		fireHiearachyChange();
	}

	@Override
	protected void fireHiearachyChange() {
		super.fireHiearachyChange();
	}

}