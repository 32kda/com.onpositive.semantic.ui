package com.onpositive.semantic.model.ui.property.editors.structured;
import org.eclipse.swt.widgets.Listener;



public interface IColumnAdapter {
	
	public Object getAdaptee();	
	
	public void addListener (int eventType, Listener listener);
	public void removeListener (int eventType, Listener listener);
	public void setMoveable (boolean moveable);

	
}
