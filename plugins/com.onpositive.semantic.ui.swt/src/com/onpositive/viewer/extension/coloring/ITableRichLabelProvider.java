package com.onpositive.viewer.extension.coloring;

import com.onpositive.semantic.model.api.roles.StyledString;

public interface ITableRichLabelProvider {

	StyledString getRichTextLabel(Object object, int index);
}
