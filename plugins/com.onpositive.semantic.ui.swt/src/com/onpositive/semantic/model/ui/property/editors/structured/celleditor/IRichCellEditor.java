package com.onpositive.semantic.model.ui.property.editors.structured.celleditor;

public interface IRichCellEditor {

	public void initCellEditor(Object owner, IRichCellEditorSupport support);

	public void mouseDownOnElement(Object data);

	public boolean handlesLeft();

	public boolean handlesRight();

	public boolean handlesHome();

	public boolean handlesEnd();

	public boolean handlesUp();

	public boolean handlesDown();

	public boolean handlesPageUp();

	public boolean handlesPageDown();
}
