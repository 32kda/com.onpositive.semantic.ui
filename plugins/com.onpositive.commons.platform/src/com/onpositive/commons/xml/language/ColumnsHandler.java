package com.onpositive.commons.xml.language;

import org.w3c.dom.Element;

public class ColumnsHandler extends GeneralElementHandler {

	public ColumnsHandler() {
		super(null,null);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Object handleElement(Element element, Object parentContext, Context context)
	{
		objectClass = parentContext.getClass() ;
		//we need this to determine the child setter which accepts column
		checkMapConstructed() ;
		evaluateChildren(element, parentContext, context);
		return null ;
	}

}
