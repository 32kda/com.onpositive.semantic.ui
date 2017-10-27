package com.onpositive.semantic.ui.snippets;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.commons.ui.appearance.HorizontalLayouter;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;

public class Snippet022LabelPropogation extends AbstractSnippet {

	
	protected AbstractUIElement<?> createContent() {
		final Container cn = new Container();
		cn.setLayoutManager(new OneElementOnLineLayouter());
		final OneLineTextElement<String> element = new OneLineTextElement<String>();
		element.setCaption("First Caption:"); //$NON-NLS-1$
		final Container child = new Container();
		child.setLayoutManager(new HorizontalLayouter());
		child.add(UIElementFactory.createText("Second:"));; //$NON-NLS-1$
		child.add(UIElementFactory.createText("Third:"));; //$NON-NLS-1$
		cn.add(element);
		cn.add(child);
		return cn;
	}

	
	protected String getDescription() {
		return "Label may be propogated automatically to parent control it helps sometimes"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Label Propogation"; //$NON-NLS-1$
	}

}
