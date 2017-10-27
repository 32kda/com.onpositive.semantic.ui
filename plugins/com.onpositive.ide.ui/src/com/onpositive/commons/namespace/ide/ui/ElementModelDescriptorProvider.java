package com.onpositive.commons.namespace.ide.ui;

import java.io.IOException;
import java.net.URL;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.semantic.language.model.DocumentationContribution;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.model.ui.roles.IImageDescriptorProvider;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;

public class ElementModelDescriptorProvider implements
		IImageDescriptorProvider {

	static ImageDescriptor imageDescriptorFromPlugin =SWTImageManager.newSWTDescriptor(AbstractUIPlugin
			.imageDescriptorFromPlugin("com.onpositive.ide.ui",
					"/icons/element.gif"));

	public ElementModelDescriptorProvider() {

	}

	public ImageDescriptor getImageDescriptor(Object object) {
		ElementModel ma = (ElementModel) object;
		DocumentationContribution documentationContribution = ma
				.getDocumentationContribution();
		if (documentationContribution != null) {
			String icon = documentationContribution.getIcon();
			if (icon!=null&&icon.trim().length() > 0) {
				URL resource = documentationContribution.getResource(icon);
				if (resource != null) {
					return ImageManager.createFromURL(resource);
				}
				else{
					Activator.log(new IOException("Icon not found:"+icon));
				}
			}
		}
		return imageDescriptorFromPlugin;
	}
}
