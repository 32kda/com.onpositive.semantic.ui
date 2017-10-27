package com.onpositive.semantic.model.ui.roles;

import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.IConfigurationElement;

public class ContentProducerObject extends RoleObject {

	boolean isInited;
	IInformationalControlContentProducer producer;

	public ContentProducerObject(IConfigurationElement element) {
		super(element);
	}

	public IInformationalControlContentProducer getContentProducer() {
		try {
			if (!this.isInited) {
				final String stringAttribute = this.getStringAttribute(
						"contentProducer", null); //$NON-NLS-1$
				if (stringAttribute != null) {
					this.producer = this
							.getObjectAttribute(
									"contentProducer", IInformationalControlContentProducer.class); //$NON-NLS-1$
				}
				this.isInited = true;
			}
			return this.producer;
		} catch (final CoreException e) {
			throw new RuntimeException(e);
		}
	}

}