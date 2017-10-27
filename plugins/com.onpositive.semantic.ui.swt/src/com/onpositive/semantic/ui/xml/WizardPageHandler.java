package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.ui.dialogs.BindedWizard;
import com.onpositive.commons.ui.dialogs.BindedWizardPage;
import com.onpositive.commons.ui.dialogs.IWizardPageListener;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IElementHandler;

public class WizardPageHandler implements IElementHandler {

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		NodeList childNodes = element.getChildNodes();
		BindedWizard bw = (BindedWizard) parentContext;
		for (int a = 0; a < childNodes.getLength(); a++) {
			Node item = childNodes.item(a);
			if (item instanceof Element) {
				Element el = (Element) item;
				AbstractUIElement<?> evaluate = (AbstractUIElement<?>) DOMEvaluator
						.getInstance().evaluate(el, bw.getBinding(), context);
				String attribute = element.getAttribute("title");
				String attribute2 = element.getAttribute("message");
				BindedWizardPage page = new BindedWizardPage(bw.getBinding(),
						evaluate, attribute, attribute2);
				attribute2 = element.getAttribute("listener");
				if (attribute2 != null && attribute2.length() > 0) {
					IWizardPageListener l = (IWizardPageListener) context
							.newInstance(attribute2);
					page.setWizardPageListener(l);
				}
				bw.addPage(page);
				break;
			}
		}
		return null;
	}

}
