package com.onpositive.semantic.model.java.tests;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.java.tests.BasicPlatformExtensionTest.LL;

public class LLListener implements IValueListener<LL>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void valueChanged(LL oldValue, LL newValue) {
		newValue.x++;
	}

}
