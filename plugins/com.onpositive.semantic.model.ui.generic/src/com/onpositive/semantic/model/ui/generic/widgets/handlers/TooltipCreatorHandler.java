package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.ui.generic.IMayHaveCustomTooltipCreator;
import com.onpositive.semantic.model.ui.roles.IInformationalControlContentProducer;

public class TooltipCreatorHandler implements IElementHandler {

	public TooltipCreatorHandler() {
	}

	public Object handleElement(Element element, Object parentContext,
			Context context) {		
		if (parentContext instanceof com.onpositive.semantic.model.ui.generic.IMayHaveCustomTooltipCreator<?>) {
			final IInformationalControlContentProducer newInstance = (IInformationalControlContentProducer) context.newInstance(element,"value");
			final IMayHaveCustomTooltipCreator<?> paDecorators = (IMayHaveCustomTooltipCreator<?>) parentContext;
			paDecorators
					.setTooltipInformationControlCreator(newInstance);
		}
		return parentContext;
	}

}
