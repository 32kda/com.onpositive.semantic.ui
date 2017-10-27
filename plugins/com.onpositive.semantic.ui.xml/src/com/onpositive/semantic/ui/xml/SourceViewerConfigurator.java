package com.onpositive.semantic.ui.xml;

import com.onpositive.commons.Activator;
import com.onpositive.commons.xml.language.AttributeHandler;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.ui.property.editors.IViewerConfigurator;
import com.onpositive.semantic.model.ui.property.editors.ViewerTextElement;

public final class SourceViewerConfigurator extends
		AttributeHandler<ViewerTextElement> {
	public SourceViewerConfigurator() {
		super(ViewerTextElement.class);
	}

	public void handle(ViewerTextElement element, Object context,
			String value, Context ctx) {
		if (value.length() > 0) {
			try {
				final Class<?> loadClass = ctx.getClassLoader()
						.loadClass(value);
				final IViewerConfigurator configurator = (IViewerConfigurator) loadClass
						.newInstance();
				element.setConfigurator(configurator);
			} catch (final Exception e) {
				Activator.log(e);
			}
		}
	}
}