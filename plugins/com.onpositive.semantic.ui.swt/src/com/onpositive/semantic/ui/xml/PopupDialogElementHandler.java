package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.ui.dialogs.InputElementDialog;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.binding.Binding;

public class PopupDialogElementHandler implements IElementHandler {

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		final Binding bnd = (Binding) parentContext;
		NodeList childNodes = element.getChildNodes();
		for (int a = 0; a < childNodes.getLength(); a++) {
			Node item = childNodes.item(a);
			if (item instanceof Element) {
				Element el = (Element) item;
				AbstractUIElement<?> evaluate = (AbstractUIElement<?>) DOMEvaluator
						.getInstance().evaluate(el, bnd, context);
				String attribute = element.getAttribute("title");
				String attribute2 = element.getAttribute("message");
				
				InputElementDialog dlg=new InputElementDialog(bnd, evaluate,attribute,attribute2);
				return dlg;				
			}
		}
		return null;
	}
}
