package com.onpositive.commons.elements;

import java.util.WeakHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;

public class TabFolderElement extends Container {

	private static final String SELECTED_NUM = "selectedNum"; //$NON-NLS-1$

	public TabFolderElement() {
		this.getLayoutHints().setGrabVertical(true);
	}

	public void create() {
		super.create();
		if (this.selectionIndex != -1) {
			this.getControl().setSelection(this.selectionIndex);
		}
	}

	private int selectionIndex;

	protected void setRedraw(boolean redraw) {

	}

	public boolean isGroup() {
		return true;
	}

	public void setLayout(Layout layout) {
		super.setLayout(layout);
	}

	public void internalLoadConfiguration(IAbstractConfiguration configuration) {
		this.selectionIndex = configuration.getIntAttribute(SELECTED_NUM);
	}

	public void internalStoreConfiguration(IAbstractConfiguration configuration) {
		final int selectionIndex = this.getControl().getSelectionIndex();
		configuration.setIntAttribute(SELECTED_NUM, selectionIndex);
	}

	protected TabFolder createControl(Composite conComposite) {
		final TabFolder tabFolder = new TabFolder(conComposite, this.calcStyle());
		
		return tabFolder;
	}

	WeakHashMap<AbstractUIElement<?>, TabItem> items = new WeakHashMap<AbstractUIElement<?>, TabItem>();

	public void adapt(AbstractUIElement<?> element) {
		if (this.isCreated()) {
			final TabItem item = new TabItem(this.getControl(), SWT.NONE);
			String caption = element.getCaption();
			if (caption!=null){
			item.setText(caption);
			}
			final Object control1 = element.getControl();
			item.setControl((Control) control1);
			this.items.put(element, item);
		}
	}

	protected void unudapt(AbstractUIElement<?> element) {
		if (this.isCreated()) {
			final TabItem ti = this.items.get(element); 
			if (ti != null) {
				ti.dispose();
				this.items.remove(element);
			}
		}
	}

	protected void update(AbstractUIElement<?> container) {
		final TabItem ti = this.items.get(container); 
		if (ti != null) {
			ti.setText(container.getText());
		}
	}

	public boolean handlesLabels() {
		return true;
	}


	public TabFolder getControl() {
		return (TabFolder) this.widget;
	}
}
