package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;

public interface IPropertyEditorDescriptor {

	IPropertyEditor<?> getEditor();
}
