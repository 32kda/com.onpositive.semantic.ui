package com.onpositive.ide.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PDEExtensionRegistry;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeValidator;

@SuppressWarnings("restriction")
public class ExtensionTypeValidator implements ITypeValidator {

	public ExtensionTypeValidator() {

	}

	public String validate(IProject project, String value,
			DomainEditingModelObject element, String typeSpec) {
		if (typeSpec != null && typeSpec.indexOf('/') != -1) {
			String trim = typeSpec.trim();
			int p = trim.indexOf('/');
			String extension = trim.substring(0, p);
			String elem = trim.substring(p + 1);

			PDEExtensionRegistry extensionsRegistry = PDECore.getDefault()
					.getExtensionsRegistry();
			IExtension[] findExtensions = extensionsRegistry.findExtensions(
					extension, false);

			for (IExtension e : findExtensions) {
				IConfigurationElement[] configurationElements = e
						.getConfigurationElements();
				for (IConfigurationElement el : configurationElements) {
					String attribute = el.getAttribute("id");
					if (el.getName().equals(elem)) {
						if (attribute != null && attribute.equals(value)) {
							return null;

						}
					}
				}
			}
		}
		return "Unresolved extension:"+value;
	}

}
