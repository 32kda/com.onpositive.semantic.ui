package com.onpositive.semantic.model.ui.actions;

import java.beans.PropertyChangeListener;
import java.io.Serializable;


public interface IContributionItem extends Serializable{

	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);

	void setEnabled(boolean b);

	void setVisible(boolean visible);
	
	boolean isEnabled();
	
	boolean isVisible();
	
	String id();
	
	public int hashCode() ;
	
}
