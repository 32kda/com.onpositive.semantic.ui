package com.onpositive.semantic.model.ui.roles;


import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.semantic.model.api.decoration.IObjectDecorator;

public class ObjectDecoratorDescriptor extends RoleObject {

	public ObjectDecoratorDescriptor(IConfigurationElement element) {
		super(element);
	}

	public int compareTo(GenericRegistryObject o) {
		final ObjectDecoratorDescriptor ds = (ObjectDecoratorDescriptor) o;
		return this.getPriority() - ds.getPriority();
	}

	public IObjectDecorator getDecorator() {
		try {
			return (IObjectDecorator) this.getObject();
		} catch (final CoreException e) {
			throw new RuntimeException();
		}
	}

	public int getPriority() {
		return this.getIntegerAttribute("priority", 0); //$NON-NLS-1$
	}
}
