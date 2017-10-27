package com.onpositive.semantic.model.ui.property;

import java.util.HashSet;

import org.eclipse.jface.action.IMenuManager;

import com.onpositive.semantic.model.api.property.IProperty;

public interface IPropertyMenuCustomizer {

	void customizeMenu(IMenuManager manager, IProperty property, Object[] objeect,
			HashSet<Object> currentValues);

}
