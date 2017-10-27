package com.onpositive.semantic.model.ui.property.editors.structured.celleditor;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CheckBoxCellEditor extends EmptyCellEditor implements
		IRichCellEditor {

	boolean value;
	private IRichCellEditorSupport support;
	private Object owner;

	public CheckBoxCellEditor(Composite parent) {
		super(parent);
	}

	
	protected Control createControl(Composite parent) {
		final Control createControl = super.createControl(parent);
		createControl.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {

			}

			public void mouseDown(MouseEvent e) {
				CheckBoxCellEditor.this.doChange();
			}

			public void mouseUp(MouseEvent e) {

			}

		});
		createControl.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 32) {
					CheckBoxCellEditor.this.doChange();
				}
				if ((e.keyCode == '\r') || (e.keyCode == '\n')) {
					CheckBoxCellEditor.this.deactivate();
					CheckBoxCellEditor.this.dispose();
				}
			}

			public void keyReleased(KeyEvent e) {

			}

		});
		return createControl;
	}

	
	protected Object doGetValue() {
		return this.value;
	}

	
	protected void doSetValue(Object value) {
		if (value == null) {
			return;
		}
		if ((value != null) && (value instanceof Boolean)) {
			this.value = (Boolean) value;
		} else {
			this.value = Boolean.parseBoolean(value.toString());
		}
	}

	public void initCellEditor(Object owner, IRichCellEditorSupport support) {
		this.support = support;
		this.owner = owner;
	}

	private void doChange() {
		this.value = !this.value;
		if (this.support != null) {
			this.support.setValueAndContinue(this.owner, this.value);
		}
	}

	public void mouseDownOnElement(Object data) {
		if (data == this.owner) {
			this.doChange();
		}
	}

	public boolean handlesDown() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean handlesEnd() {
		return false;
	}

	public boolean handlesHome() {
		return false;
	}

	public boolean handlesLeft() {
		return false;
	}

	public boolean handlesPageDown() {
		return false;
	}

	public boolean handlesPageUp() {
		return false;
	}

	public boolean handlesRight() {
		return false;
	}

	public boolean handlesUp() {
		return false;
	}

}
