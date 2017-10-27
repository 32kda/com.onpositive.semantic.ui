package com.onpositive.commons.xml.language;

import org.w3c.dom.Element;

public interface IElementHandler {

	Object handleElement(Element element, Object parentContext, Context context);
}
