package com.onpositive.semantic.ui.layouts;


import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.core.runtime.Bundle;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.roles.RoleObject;
import com.onpositive.semantic.model.ui.generic.IPropertyEditorDescriptor;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;

public class VisualisationAspect extends RoleObject implements
		IVisualisationAspect {

	public VisualisationAspect(IConfigurationElement element) {
		super(element);
	}

	public IPropertyEditorDescriptor getEditor() {

		return new IPropertyEditorDescriptor() {

			Object object;

			public IPropertyEditor<?> getEditor() {
				if (this.object == null) {
					final String stringAttribute = VisualisationAspect.this.getStringAttribute(
							"definition", null); //$NON-NLS-1$
					if (stringAttribute == null) {
						return null;
					}
					final String name2 = VisualisationAspect.this.fElement.getContributorId();
					final Bundle bundle = Platform.getBundle(name2);
					try {
						this.object = DOMEvaluator.getInstance().evaluateLocalPluginResource(bundle,
										stringAttribute, null);
					} catch (final Exception e) {
						throw new RuntimeException(e);
					}
				}
				return (IPropertyEditor<?>) this.object;
			}

		};
	}

	public String getIcon() {
		return null;
	}

	public boolean isApplyable(Object object) {
		return true;
	}

}
