package com.onpositive.semantic.model.ui.property.editors.structured;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeColumn;


public class TreeColumnAdapter implements IColumnAdapter{
	
	TreeColumn column;

	public TreeColumnAdapter(TreeColumn column) {
		super();
		this.column = column;
	}

	public void addListener(int eventType, Listener listener) {
		this.column.addListener(eventType, listener);
	}

	public Object getAdaptee() {
		return this.column;
	}

	public void setMoveable(boolean moveable) {
		this.column.setMoveable(moveable);
	}
	
	public void removeListener(int eventType, Listener listener) {
		this.column.removeListener(eventType, listener);
	}
}
