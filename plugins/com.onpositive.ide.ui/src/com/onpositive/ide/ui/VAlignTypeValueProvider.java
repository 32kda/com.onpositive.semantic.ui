package com.onpositive.ide.ui;

import com.onpositive.commons.namespace.ide.ui.completion.AbstractTypeValueProvider;


public class VAlignTypeValueProvider extends AbstractTypeValueProvider
{

	
	protected String[] getValues()
	{
		return new String[] {"top", "bottom", "center", "fill"};
	}


}
