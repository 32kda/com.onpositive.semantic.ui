package com.onpositive.semantic.ui.xml;

import com.onpositive.semantic.model.ui.property.editors.IViewerConfigurator;
import com.onpositive.semantic.model.ui.property.editors.IViewerTextElement;
import com.onpositive.semantic.ui.text.SpellCheckConfigurator;

public class SpellCheckXMLConfigurator implements IViewerConfigurator{

	public void configure(IViewerTextElement element) {
		SpellCheckConfigurator configurator = new SpellCheckConfigurator();
		configurator.configure(element.getSourceViewer());
	}

}
