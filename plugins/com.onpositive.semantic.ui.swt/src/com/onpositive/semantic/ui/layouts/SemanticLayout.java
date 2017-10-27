package com.onpositive.semantic.ui.layouts;


import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.semantic.model.api.roles.RoleObject;

public class SemanticLayout extends RoleObject implements ISemanticLayout {

	public SemanticLayout(IConfigurationElement element) {
		super(element);
	}

	public String getIcon() {
		return null;
	}

	public void installAspect(IVisualisationAspect aspect) {
		// TODO Auto-generated method stub

	}

	public void refreshAspect(VisualisationAspect e) {
		// TODO Auto-generated method stub

	}

	public void setActive(IVisualisationAspect aspect) {
		// TODO Auto-generated method stub

	}

	public void uninstallAspect(IVisualisationAspect aspect) {
		// TODO Auto-generated method stub

	}

}
