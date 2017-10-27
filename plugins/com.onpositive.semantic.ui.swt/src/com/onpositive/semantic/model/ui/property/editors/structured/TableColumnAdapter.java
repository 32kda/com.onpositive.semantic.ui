package com.onpositive.semantic.model.ui.property.editors.structured;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;


public class TableColumnAdapter implements IColumnAdapter {
	
	TableColumn column;

	public TableColumnAdapter(TableColumn column) {
		super();
		this.column = column;
	}

	public void addListener(int eventType, Listener listener) {
		this.column.addListener(eventType, listener);
	}

	public Object getAdaptee() {
		return column;
	}

	public void setMoveable(boolean moveable) {
		this.column.setMoveable(moveable);
	}

	public void removeListener(int eventType, Listener listener) {
		this.column.removeListener(eventType, listener);
	}

}
