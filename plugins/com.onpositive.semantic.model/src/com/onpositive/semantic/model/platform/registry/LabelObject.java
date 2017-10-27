package com.onpositive.semantic.model.platform.registry;

import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.semantic.model.api.labels.ExpressionBasedLabelProvider;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;

public class LabelObject extends GenericRegistryObject {

	protected Object provider;

	public LabelObject(IConfigurationElement element) {
		super(element);
	}

	public String targetClass() {
		return getStringAttribute("targetClass", null);
	}

	public Object getProvider() {
		if (provider != null) {
			return provider;
		}

		String stringAttribute = getStringAttribute("labelProvider", null);
		if (stringAttribute != null) {
			try {
				provider = getObjectAttribute("labelProvider",
						ITextLabelProvider.class);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		stringAttribute = getStringAttribute("label", null);
		if (stringAttribute != null) {
			ExpressionBasedLabelProvider expressionBasedLabelProvider = new ExpressionBasedLabelProvider(stringAttribute,
					getStringAttribute("descriptionLabel", ""));
			provider=expressionBasedLabelProvider;
		}
		return provider;
	}
}
