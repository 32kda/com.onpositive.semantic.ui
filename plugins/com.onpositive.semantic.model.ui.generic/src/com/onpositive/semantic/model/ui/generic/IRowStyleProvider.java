package com.onpositive.semantic.model.ui.generic;

import java.io.Serializable;


public interface IRowStyleProvider extends Serializable{

	public String getFont(Object element);
	
	public String getBackground(Object element);
}
