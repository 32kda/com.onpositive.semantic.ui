package com.onpositive.semantic.model.ui.roles;

import java.util.HashSet;

import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.semantic.model.ui.roles.AbstractRoleMap.RoleKey;

public class RoleObject extends GenericRegistryObject {

	public RoleObject(IConfigurationElement element) {
		super(element);
	}

	public String getRole() {
		return this.getStringAttribute("role", null); //$NON-NLS-1$
	}

	public String getTheme() {
		return this.getStringAttribute("theme", null); //$NON-NLS-1$
	}

	public String getTargetClass() {
		return this.getStringAttribute("targetClass", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public HashSet<String> getTargetType() {
		final String stringAttribute = this.getStringAttribute(
				"targetType", null); //$NON-NLS-1$
		if ((stringAttribute == null) || (stringAttribute.length() == 0)) {
			return null;
		}
		final String trim = stringAttribute.trim();
		if (trim.length() == 0) {
			return null;
		}
		final String[] split = trim.split(","); //$NON-NLS-1$
		final HashSet<String> result = new HashSet<String>();
		for (final String s : split) {
			result.add(s);
		}
		return result;
	}

	public RoleKey toKey() {
		final String role = this.getRole();
		return new RoleKey(this.getTargetClass(), role, this.getTheme(), this
				.getTargetType());
	}

}