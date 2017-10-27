package com.onpositive.semantic.ui.xml;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Element;

import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;
import com.onpositive.semantic.model.ui.property.editors.FormEditor;

public class HeadControlHandler implements IElementHandler {

	public Object handleElement( Element element, Object parentContext, Context context ) {
		final CompositeEditor cm = new CompositeEditor();
		final FormEditor editor = (FormEditor) parentContext;
		editor.addElementListener(new ElementListenerAdapter() {

			public void bindingChanged(IUIElement element,
					IBinding newBinding, IBinding oldBinding) {
				cm.setBinding(editor.getBinding());
			}

			public void elementCreated(IUIElement element) {
				Form form = (Form) element.getControl();
				form.setToolBarVerticalAlignment(SWT.TOP);
				// form.getHead().setBackgroundMode(SWT.INHERIT_FORCE);
				RootElement el = new RootElement(form.getHead(),
						SWT.NO_BACKGROUND);
				FormToolkit service = (FormToolkit) element.getService(FormToolkit.class);
				// el.getContentParent().setBackground(null);

				el.getContentParent().setBackgroundMode(SWT.INHERIT_DEFAULT);
				// service.adapt(el.getControl());
				el.add(cm);
				cm.setBinding(editor.getBinding());
				form.setHeadClient(el.getControl());
			}

		});
		cm.setBinding(editor.getBinding());
		DOMEvaluator.evaluateChildren(element, cm, context);
		return cm;
	}
}
