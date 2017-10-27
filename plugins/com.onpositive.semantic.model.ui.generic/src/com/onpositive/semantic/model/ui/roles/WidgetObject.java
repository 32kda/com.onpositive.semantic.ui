package com.onpositive.semantic.model.ui.roles;

import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class WidgetObject extends RoleObject {

	public WidgetObject(IConfigurationElement element) {
		super(element);
	}

	public void show(Binding bnd, String role) {
		try {
			if (this.getStringAttribute("defaultCreator", null) != null) {
				final IWidgetCreator creator = this.getObjectAttribute(
						"defaultCreator", IWidgetCreator.class);
				creator.showWidget(bnd, this, role);
			} else {
				new DisplayableCreator().showWidget(bnd, this, role);
			}
		} catch (final CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object evaluate(Binding bnd){
		Object evaluateLocalPluginResource;
		try {
			evaluateLocalPluginResource = DOMEvaluator.getInstance()
					.evaluateLocalPluginResource(getBundle(), getResource(), bnd);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return evaluateLocalPluginResource;
	}

	public IUIElement<?> createWidget(Binding bnd) {
		try {
			if (this.getStringAttribute("defaultCreator", null) != null) {
				final IContextWidgetCreator creator = this.getObjectAttribute(
						"defaultCreator", IContextWidgetCreator.class);
				return creator.createWidget(bnd, this);
			} else {
				return new DisplayableCreator().createWidget(bnd, this);
			}
		} catch (final CoreException e) {
			throw new RuntimeException(e);
		}
	}

	public void show(Binding bnd, IWidgetCreator creator) {
		creator.showWidget(bnd, this, getRole());
	}

	public String getResource() {
		return this.getStringAttribute("resource", null);
	}
}
