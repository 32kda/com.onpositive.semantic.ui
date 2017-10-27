package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.core.runtime.Platform;

public class UIElementFactoryProvider {

	static IUIElementFactory instance;
	
	static{
		IConfigurationElement[] configurationElementsFor = Platform.getExtensionRegistry().getConfigurationElementsFor("com.onpositive.semantic.model.uiengine");
		instance=(IUIElementFactory) configurationElementsFor[0].createExecutableExtension("clazz");
	}
	
	public static IUIElementFactory getInstance(){		
		return instance;		
	}
}
