package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.onpositive.commons.ui.dialogs.FormDialog;
import com.onpositive.commons.ui.dialogs.TitledDialog;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.api.roles.ImageManager;
import com.onpositive.semantic.model.api.roles.ImageObject;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.model.ui.property.editors.FormEditor;

public class FormDialogElementHandler implements IElementHandler {

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		final Binding bnd = (Binding) parentContext;
		NodeList childNodes = element.getChildNodes();
		for (int a = 0; a < childNodes.getLength(); a++) {
			Node item = childNodes.item(a);
			if (item instanceof Element) {
				Element el = (Element) item;
				IUIElement evaluate = (IUIElement) DOMEvaluator
						.getInstance().evaluate(el, bnd, context);
				String attribute = element.getAttribute("title");
				String attribute2 = element.getAttribute("description");
				String attribute3 = element.getAttribute("image");
				FormEditor editor=(FormEditor) evaluate;
				editor.setBinding(bnd);
				editor.setBindingInTitle(false);
				
				//editor.add(evaluate);
				editor.setCaption(attribute);
				editor.setToolTipText(attribute2);
				ImageObject imageObject = ImageManager.getInstance().get(attribute3);
				if(imageObject!=null){
					editor.setImageDescriptor(imageObject.getImageDescriptor());
				}
				String attribute4 = element.getAttribute("flags");
				int flags=FormDialog.EDIT;
				if (attribute4!=null){
					if (attribute4.equals("create")){
						flags=FormDialog.CREATE;
					}
					if (attribute4.equals("edit")){
						flags=FormDialog.VIEW;
					}
					if (attribute4.equals("view")){
						flags=FormDialog.VIEW;
					}
				}
				FormDialog dlg=new FormDialog(bnd, editor,attribute,attribute2);
				
				dlg.setMode(flags);
				return dlg;				
			}
		}
		return null;
	}
}
