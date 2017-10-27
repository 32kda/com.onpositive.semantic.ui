package com.onpositive.semantic.ui.xml;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.DialogSettings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.ui.dialogs.TitledDialog;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.binding.Binding;

public class DialogElementHandler implements IElementHandler {

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
				String attribute3 = element.getAttribute("image");
				String hh = element.getAttribute("helpContext");
				TitledDialog dlg = new TitledDialog(bnd, evaluate, attribute,
						attribute2, attribute3);
				if (hh.length() > 0) {
					dlg.setHelpContext(hh);
				}
				dlg.setResizable(Boolean.parseBoolean(element
						.getAttribute("resizable")));
				String attribute4 = element.getAttribute("dialogSettingsId");
				if (attribute4.length() > 0) {
					DialogSettings ss = new DialogSettings("");
					IPath stateLocation = com.onpositive.commons.Activator
							.getDefault().getStateLocation();
					File file = new File(stateLocation.toFile(), "dialogs");
					dlg.setDialogSettingsFile(file);
				} else {
					String id = context.getId();
					if (id != null) {
						IPath stateLocation = com.onpositive.commons.Activator
						.getDefault().getStateLocation();
						File file = new File(stateLocation.toFile(), id);
						dlg.setDialogSettingsFile(file);
					}
				}
				return dlg;
			}
		}
		return null;
	}
}
