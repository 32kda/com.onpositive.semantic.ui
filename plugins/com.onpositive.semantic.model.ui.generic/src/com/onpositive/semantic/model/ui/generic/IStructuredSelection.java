package com.onpositive.semantic.model.ui.generic;

import java.util.List;

public interface IStructuredSelection {

	public List<? extends Object> toList();

	public boolean isEmpty();

	public Object getFirstElement();
	
	public int size();

}
