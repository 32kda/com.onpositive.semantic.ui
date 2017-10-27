package com.onpositive.semantic.ui.android.handlers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.view.View;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.ui.android.composites.AndroidDynamicEditor;
import com.onpositive.semantic.ui.android.composites.AndroidVerticalComposite;

public class OnNullHandler implements IElementHandler{

	public OnNullHandler() {
		
	}

	@SuppressWarnings("unchecked")
	public Object handleElement(Element element, Object parentContext,
			Context context) {
		AndroidDynamicEditor selector = (AndroidDynamicEditor) parentContext;
		AndroidVerticalComposite m = new AndroidVerticalComposite();
		final NodeList childNodes = element.getChildNodes();
		for (int a = 0; a < childNodes.getLength(); a++) {
			final Node item = childNodes.item(a);
			if (item instanceof Element)
			{
				m.add(
				(BasicUIElement<View>) DOMEvaluator.getInstance()
						.evaluate((Element) item, m, context));
			}
		}
		selector.setOnNull(m);
		
		return m;		
	}
	
	
}