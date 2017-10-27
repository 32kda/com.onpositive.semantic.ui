package com.onpositive.semantic.ui.layouts;

import com.onpositive.semantic.model.ui.generic.IPropertyEditorDescriptor;

public interface IVisualisationAspect {

	String getName();

	String getDescription();

	String getIcon();

	IPropertyEditorDescriptor getEditor();

	boolean isApplyable(Object object);
}
