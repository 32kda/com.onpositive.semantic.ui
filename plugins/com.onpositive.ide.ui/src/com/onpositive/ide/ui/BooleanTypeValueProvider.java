package com.onpositive.ide.ui;

import com.onpositive.commons.namespace.ide.ui.completion.AbstractTypeValueProvider;
 

public class BooleanTypeValueProvider extends AbstractTypeValueProvider
{
	
	
	protected String[] getValues()
	{
		return new String[]{"true", "false"};
	}

}
