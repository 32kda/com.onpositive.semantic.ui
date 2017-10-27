package com.onpositive.semantic.model.ui.generic;


public interface IDisplayable {

	public int openWidget();
	
	public boolean isModal();
	
	void addDisposeCallback(Runnable r);
}
