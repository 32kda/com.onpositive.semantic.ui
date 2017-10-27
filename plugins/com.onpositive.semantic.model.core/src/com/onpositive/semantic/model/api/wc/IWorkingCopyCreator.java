package com.onpositive.semantic.model.api.wc;


public interface IWorkingCopyCreator {

	Object getWorkingCopy(Object source);
	
	public void applyWorkingCopy(Object source,Object target);
}
