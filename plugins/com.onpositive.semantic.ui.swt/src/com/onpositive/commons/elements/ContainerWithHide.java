package com.onpositive.commons.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class ContainerWithHide extends Container {

	private Composite visiblePart;
	private Composite invisiblePart;
	private boolean addToInvise;

	public Composite getContentParent() {
		return this.addToInvise ? this.invisiblePart : this.visiblePart;
	}

	protected Composite createControl(Composite conComposite) {
		final Composite body = new Composite(conComposite, SWT.NONE);
		this.visiblePart = new Composite(body, this.calcStyle());
		this.visiblePart.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.invisiblePart = new Composite(body, SWT.NONE);
		this.invisiblePart.setLayoutData(new GridData(0, 0));
		this.invisiblePart.setVisible(false);
		this.visiblePart.setLayout(this.layout);
		final GridLayout layout2 = new GridLayout(1, false);
		layout2.marginWidth = 0;
		layout2.marginHeight = 0;
		layout2.horizontalSpacing = 0;
		layout2.verticalSpacing = 0;
		body.setLayout(layout2);
		return body;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
		if (this.isCreated()) {
			(this.visiblePart).setLayout(layout);
		}
	}

	public void setInitialChildMode(boolean hidden) {
		this.addToInvise = hidden;
	}

	public void hide(AbstractUIElement<?> element) {
		final boolean oldValue = this.addToInvise;
		this.addToInvise = true;
		this.add(element);
		this.addToInvise = oldValue;
	}

	public void show(AbstractUIElement<?> element) {
		final boolean oldValue = this.addToInvise;
		this.addToInvise = true;
		this.add(element);
		this.addToInvise = oldValue;
	}
}
