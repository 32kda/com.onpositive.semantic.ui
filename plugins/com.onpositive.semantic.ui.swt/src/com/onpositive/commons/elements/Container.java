package com.onpositive.commons.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.onpositive.commons.ui.appearance.IContainerLayoutManager;
import com.onpositive.commons.xml.language.ChildSetter;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.IContainerListener;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class Container extends AbstractUIElement<Composite> implements ICompositeElement<AbstractUIElement<?>,Composite> {

	public static final int GROUP = 1;

	protected final ArrayList<AbstractUIElement<?>> children = new ArrayList<AbstractUIElement<?>>();
	private final HashSet<IContainerListener> listeners = new HashSet<IContainerListener>();
	protected Layout layout;
	private boolean transaction;
	private boolean group;
	protected boolean restoreRedraw;
	private int maxLabelWidht = 0;

	public void setMaxLabelWidht(int maxLabelWidht) {
		this.maxLabelWidht = maxLabelWidht;
	}

	public int getMaxLabelWidht() {
		return maxLabelWidht;
	}

	public AbstractUIElement<?> getElement(String id) {
		final int size = this.children.size();
		for (int a = 0; a < size; a++) {
			final AbstractUIElement<?> abstractUIElement = this.children.get(a);
			final String id2 = abstractUIElement.getId();
			if ((id != null) && id2!=null&&id2.equals(id)) {
				return abstractUIElement;
			}
			if (abstractUIElement instanceof Container) {
				final Container c = (Container) abstractUIElement;
				final AbstractUIElement<?> element = c.getElement(id);
				if (element != null) {
					return element;
				}
			}
		}
		return null;
	}

	private IContainerLayoutManager layoutManager;

	public boolean isGroup() {
		return this.group;
	}

	public IContainerLayoutManager getLayoutManager() {
		return this.layoutManager;
	}

	public boolean needsLabel() {
		return false;
	}

	public void create() {

		super.create();
		if (this.layoutManager != null) {
			this.layoutManager.elementCreated(this);
		}
	}

	public void setLayoutManager(IContainerLayoutManager manager) {
		if (this.layoutManager != null) {
			this.layoutManager.uninstall(this);
		}
		this.layoutManager = manager;
		if (this.layoutManager != null) {
			this.layoutManager.install(this);
		}

	}

	private Rectangle margin;

	public Rectangle getMargin() {
		return this.margin;
	}

	public void setMargin(com.onpositive.semantic.ui.core.Rectangle margin) {
		if (margin!=null){
		this.margin = new Rectangle(margin.x, margin.y, margin.width, margin.height);
		if (this.isCreated()) {
			final Layout layoutData2 = this.getContentParent().getLayout();
			if (layoutData2 instanceof GridLayout) {
				final GridLayout ls = (GridLayout) layoutData2;
				ls.marginHeight = 0;
				ls.marginWidth = 0;
				ls.marginLeft = margin.x;
				ls.marginTop = margin.y;
				ls.marginRight = margin.width;
				ls.marginBottom = margin.height;
			}
		}
		}
	}

	public void addContainerListener(IContainerListener listener) {
		this.listeners.add(listener);
	}

	public void removeContainerListener(IContainerListener listener) {
		this.listeners.add(listener);
	}

	public Container() {
		setLayout(new FillLayout());
		getLayoutHints().setGrabHorizontal(true);
	}

	public Container(int style) {
		this.group = (style & GROUP) != 0;
		getLayoutHints().setGrabHorizontal(true);
	}

	public void add(AbstractUIElement<?> element)
	{
		if (this.isRedraw()) {
			this.setRedraw(false);
			this.restoreRedraw = true;
		}
		Container container = element.container();
		if (container != null) {
			container.remove(element, false);
			if (element.isCreated() && element.isDisplayable()) {
				container.getContentParent().layout(true, true);
			}
		}
		if (this.layoutManager != null && element.isDisplayable()) {
			this.layoutManager.elementAdding(this, element);
		}
		element.onAddedTo( this );
		if (element.isDisplayable()) {
			if (element.isCreated()) {
				element.reparent();
				this.accept(element);
				this.getContentParent().layout(true, true);
			} else {
				if (this.isCreated()) {
					element.create();
					element.installLayoutData();
					this.accept(element);
					this.getContentParent().layout(true, true);
				}
			}
		}
		this.children.add(element);
		fireHiearachyChange();
		element.fireHiearachyChange();
		if (!this.transaction) {
			this.postAdd(element);
		}
	}

	protected void fireHiearachyChange() {
		super.fireHiearachyChange();
		for (final AbstractUIElement<?> a : this.children) {
			a.fireHiearachyChange();
		}
	}

	protected void onDisplayable(AbstractUIElement<?> element,
			boolean displayable) {
		inStateChange=true;
		try{
		if (isCreated()) {
			Composite layoutRoot = this.getLayoutRoot();
			if (displayable) {
				AbstractUIElement<?> el = null;
				boolean f = false;
				for (AbstractUIElement<?> lm : children) {
					if (f && lm.isDisplayable()) {
						el = lm;
						break;
					}
					if (lm == element) {
						f = true;
					}
				}
				layoutRoot.setLayoutDeferred(true);
				layoutRoot.setRedraw(false);
				try {
					element.create();
					element.installLayoutData();
					
					this.accept(element);
					if (el != null) {
						Control allControls = el.getFirst();
						Control[] allControls2 = element.getAllControls();
						if (allControls2.length > 0) {
							for (int a = 0; a < allControls2.length; a++) {
								allControls2[a].moveAbove(allControls);
							}
						}
					}
					if (this.layoutManager != null && element.isDisplayable()) {
						this.layoutManager.elementAdded(this, element);
					}
				} finally {
					layoutRoot.setLayoutDeferred(false);
					layoutRoot.setRedraw(true);					
					layoutRoot.layout(true, true);
				}
			} else {
				internalRemove(element, true);
				this.layoutManager.elementRemoved(this, element);
				layoutRoot.layout(true, true);
			}		
		}
		}finally{
			inStateChange=false;
		}
	}

	private Composite getLayoutRoot() {
		Container c=this;
		while (c.getParent()!=null){
			c=c.getParent();
		}
		return c.getContentParent();
	}

	public void add(int position, AbstractUIElement<?> element) {
		this.transaction = true;
		this.add(element);
		if (position > -1) {

			final AbstractUIElement<?> commonUIElement = this.children
					.get(position);
			this.children.remove(element);
			this.children.add(position, element);
			if (this.isCreated() && element.isDisplayable()) {
				this.getContentParent().setLayoutDeferred(true);
				this.getContentParent().setRedraw(false);
				Control allControls = element.getLast();
				Control[] allControls2 = commonUIElement.getAllControls();
				if (allControls2.length > 0) {
					allControls2[0].moveBelow(allControls);
					for (int a = 1; a < allControls2.length; a++) {
						allControls2[a].moveBelow(allControls2[a - 1]);
					}
				}
				this.getContentParent().setLayoutDeferred(false);
				this.getContentParent().setRedraw(true);
				this.getContentParent().layout(true, true);
			}
		}
		this.transaction = false;
		this.postAdd(element);
	}

	public void addAbove(int position, AbstractUIElement<?> element) {
		this.transaction = true;
		this.add(element);
		if (position > -1) {
			final AbstractUIElement<?> commonUIElement = this.children
					.get(position);
			this.children.remove(element);
			this.children.add(position, element);
			if (isCreated() && element.isDisplayable()) {
				element.getControl().moveAbove(commonUIElement.getLast());
			}
			this.transaction = false;
			this.postAdd(element);
		}
	}

	private void postAdd(AbstractUIElement<?> element) {
		this.adapt(element);
		for (final IContainerListener l : this.listeners) {
			l.elementAdded((ICompositeElement)this, element);
		}
		if (this.layoutManager != null) {
			this.layoutManager.elementAdded(this, element);
		}
		if (this.restoreRedraw) {
			this.setRedraw(true);
		}
	}

	protected void accept(AbstractUIElement<?> element) {
		if (this.parent != null) {
			((Container)this.parent).accept(element);
		}
	}

	public void internalCreate() {
		super.internalCreate();
		for (final AbstractUIElement<?> e : this.children) {
			if (!e.isDisplayable()) {
				continue;
			}
			if (this.layoutManager != null) {
				this.layoutManager.childCreating(this, e);
			}
			e.create();
			this.adapt(e);
			this.accept(e);
		}
		for (final AbstractUIElement<?> e : this.children) {
			if (!e.isDisplayable()) {
				continue;
			}
			e.installLayoutData();
		}
		this.setRedraw(true);
	}

	@SuppressWarnings("unchecked")
	public List<AbstractUIElement<?>> getChildren() {
		return Collections.unmodifiableList((List)this.children);
	}

	public void adapt(AbstractUIElement<?> element) {
	}

	public void remove(AbstractUIElement element) {
		this.remove(element, true);

	}

	@SuppressWarnings("unchecked")
	public <R> R getService(Class<R> clazz) {
		if (clazz == IProvidesToolbarManager.class) {
			for (AbstractUIElement<?> s : children) {
				if (s instanceof IProvidesToolbarManager) {
					return (R) s;
				}
			}
		}
		return super.getService(clazz);
	}

	private void remove(AbstractUIElement<?> element, boolean unadapt) {
		this.children.remove(element);
		if (element.isDisplayable()) {
			internalRemove(element, unadapt);
		}
		for (final IContainerListener l : this.listeners) {
			l.elementRemoved((ICompositeElement)this, (IUIElement<?>)element);
		}
		element.onRemovedFrom(this);
	}

	protected void internalRemove(AbstractUIElement<?> element, boolean unadapt) {
		if (element.isCreated()) {
			element.getControl().setLayoutData(null);
		}
		if (unadapt) {
			this.unudapt(element);
		}
		if (this.layoutManager != null) {
			this.layoutManager.elementRemoved(this, element);
		}
	}

	public Layout getLayout() {
		return this.layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
		if (this.isCreated()) {
			(this.getContentParent()).setLayout(layout);
			for (final AbstractUIElement<?> e : this.children) {
				e.installLayoutData();
			}
		}

	}

	protected void unudapt(AbstractUIElement<?> element) {
		element.dispose();
	}

	public Composite getContentParent() {
		return this.getControl();
	}

	protected Composite createControl(Composite conComposite) {
		if (!this.group) {
			final Composite composite = new Composite(conComposite,
					this.calcStyle());
			composite.setLayout(this.layout);
			composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
			return composite;
		} else {
			final Composite composite = new Group(conComposite,
					this.calcStyle());
			composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
			composite.setLayout(this.layout);
			return composite;
		}
	}

	public boolean handlesLabels() {
		return false;
	}

	public void setText(String txt) {
		super.setText(txt);
		Container parent2=container();
		if ((parent2 != null) && parent2.isCreated()
				&& parent2.handlesLabels()) {
			parent2.update(this);
		}
	}

	protected void update(AbstractUIElement<?> container) {

	}

	protected Hyperlink createControlHyperLink(Composite contentParent) {
		if (this.parent != null) {
			return container().createControlHyperLink(contentParent);
		}
		return new Hyperlink(contentParent, SWT.LEFT);
	}

	protected Label createControlLabel(Composite contentParent) {
		if (this.parent != null) {
			Label ret = container().createControlLabel(contentParent);
			return ret;
		}

		Label ret = null;
		if (Platform.getOS()
				.equals(org.eclipse.core.runtime.Platform.OS_MACOSX)) {
			ret = new Label(contentParent, SWT.LEFT | SWT.NO_BACKGROUND);
		} else {
			ret = new Label(contentParent, SWT.LEFT);			
		}
		return ret;
	}

	public void dispose() {
		super.dispose();
		if (this.layoutManager != null) {
			this.layoutManager.elementDisposed(this);
		}
	}

	public Composite getLabelParent(AbstractUIElement<?> element) {
		if (this.layoutManager != null) {
			return this.layoutManager.getLabelParent(this, element);
		}
		return this.getContentParent();
	}

	public int indexOf(AbstractUIElement<?> element) {
		return this.children.indexOf(element);
	}

	public AbstractUIElement<?> getFirstChild() {
		if (this.children.size() == 0) {
			return null;
		}
		return this.children.get(0);
	}

	public AbstractUIElement<?> getLastChild(AbstractUIElement<?> element) {
		final int size = this.children.size();

		return this.children.get(size - 1);
	}

	public void onDisplayable(IUIElement<?> element) {
		onDisplayable((AbstractUIElement<?>) element,element.isDisplayable());
	}
}
