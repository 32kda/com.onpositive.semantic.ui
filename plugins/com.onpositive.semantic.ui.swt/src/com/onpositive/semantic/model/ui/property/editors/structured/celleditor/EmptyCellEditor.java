package com.onpositive.semantic.model.ui.property.editors.structured.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class EmptyCellEditor extends CellEditor {

	Canvas label;

	boolean inited;

	private Object value;

	public EmptyCellEditor(Composite parent) {
		super(parent);
	}

	
	protected void focusLost() {
		this.dispose();
	}

	
	public void dispose() {
		this.label.dispose();
		super.dispose();
	}

	public void activate() {

	}

	
	public LayoutData getLayoutData() {
		final LayoutData dl = new LayoutData();
		dl.grabHorizontal = false;
		dl.minimumWidth = 1;
		dl.horizontalAlignment = SWT.LEFT;
		return dl;
	}

	protected Control createControl(final Composite parent) {
		this.label = new Canvas(parent, SWT.NONE) {
		};
		return this.label;

	}

	protected void doSetFocus() {
		this.label.setFocus();
	}

	protected Object doGetValue() {
		return this.value;
	}

	protected void doSetValue(Object value) {
		this.value = value;
	}
}
