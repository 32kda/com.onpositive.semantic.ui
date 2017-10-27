package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.IHyperlinkListener;

public interface IHasHyperlinks<T> extends IUIElement<T>,IHasText{

	@HandlesAttributeDirectly("url")
	public void setUrl(String attribute);
	
	@HandlesAttributeDirectly("hyperLinkListener")
	void addHyperLinkListener(IHyperlinkListener listener);
	
	void removeHyperLinkListener(IHyperlinkListener listener);
}
