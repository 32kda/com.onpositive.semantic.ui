package com.onpositive.ide.ui;

import com.onpositive.commons.namespace.ide.ui.completion.AbstractTypeValueProvider;


public class ActionStyleTypeValueProvider extends
		AbstractTypeValueProvider 
{
	
	
	protected String[] getValues()
	{
		return new String[] {"push", "check", "radio", "drop-down"};
	}

}
