package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.semantic.model.ui.generic.IStructuredSelection;


public interface IActionInterceptor {
	
	public boolean isEnabled(IListElement<?>selector, IStructuredSelection selection);
	
	public boolean preAction(IListElement<?>selector, IStructuredSelection selection);
	
	public boolean postAction(IListElement<?>selector, IStructuredSelection selection);
}
