package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.common.ui.roles.IInformationalControlContentProducer;
import com.onpositive.semantic.model.ui.property.editors.IMayHaveCustomTooltipCreator;

public class TooltipCreatorHandler implements IElementHandler {

	public TooltipCreatorHandler() {
	}

	public Object handleElement(Element element, Object parentContext,
			Context context) {		
		if (parentContext instanceof IMayHaveCustomTooltipCreator<?>) {
			final IInformationalControlContentProducer newInstance = (IInformationalControlContentProducer) context.newInstance(element,"value");
			final IMayHaveCustomTooltipCreator<?> paDecorators = (IMayHaveCustomTooltipCreator<?>) parentContext;
			paDecorators
					.setTooltipInformationControlCreator(newInstance);
		}
		return parentContext;
	}

}
