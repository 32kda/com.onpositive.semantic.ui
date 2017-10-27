package com.onpositive.commons.namespace.ide.ui.core;

import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.meta.IHasMeta;

public class GroupTextLabelProvider implements ITextLabelProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public GroupTextLabelProvider() {
	}

	
	public String getDescription(Object object) {
		GenericRegistryObject genericRegistryObject = GroupRegistry.getRegistry().get(object.toString());
		if (genericRegistryObject==null){
			return "Undefined Group";
		}
		return genericRegistryObject
				.getDescription();
	}

	
	public String getText(IHasMeta meta, Object parent, Object object) {
		String string = object.toString().trim();
		if (string.length() == 0) {
			return "No Group";
		}
		GenericRegistryObject genericRegistryObject = GroupRegistry
				.getRegistry().get(string);
		if (genericRegistryObject != null) {
			String name = genericRegistryObject.getName();
			return name;
		}
		return string;
	}

}
