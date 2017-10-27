package com.onpositive.semantic.model.ui.roles;

import java.util.ArrayList;
import java.util.Collection;

import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.core.runtime.IConfigurationElement;

public class SemanticRole extends GenericRegistryObject {

	ArrayList<SemanticRole> superRoles;

	public SemanticRole(IConfigurationElement element) {
		super(element);
	}

	public Collection<SemanticRole> getSuperRoles() {
		if (this.superRoles != null) {
			return this.superRoles;
		}
		this.superRoles = new ArrayList<SemanticRole>();
		final String s = this.getStringAttribute("parentRoles", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$
		if (s.length() > 0) {
			final String[] sr = s.split(","); //$NON-NLS-1$
			for (final String sq : sr) {
				final SemanticRole semanticRole = RoleManager.getInstance().get(
						sq.trim());
				if (semanticRole != null) {
					this.superRoles.add(semanticRole);
				}
			}
		}
		return this.superRoles;
	}
	

	public String getLabel() {
		return this.getStringAttribute("label", "{object.label}"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getDescription() {
		return this.getStringAttribute("description", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
}