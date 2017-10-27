package com.onpositive.semantic.model.ui.property.editors.structured.celleditor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CellEditorFactory {

	Class<? extends ICellEditorFactory> value();
}
