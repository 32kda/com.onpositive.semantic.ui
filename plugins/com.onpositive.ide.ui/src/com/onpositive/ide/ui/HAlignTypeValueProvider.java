package com.onpositive.ide.ui;

import com.onpositive.commons.namespace.ide.ui.completion.AbstractTypeValueProvider;


public class HAlignTypeValueProvider extends AbstractTypeValueProvider
{
	
	
	protected String[] getValues()
	{
		return new String[] {"left", "right", "center", "fill"};
	}


}
