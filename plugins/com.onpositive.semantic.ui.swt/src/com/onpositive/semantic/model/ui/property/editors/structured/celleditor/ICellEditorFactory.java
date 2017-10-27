package com.onpositive.semantic.model.ui.property.editors.structured.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.Viewer;

import com.onpositive.semantic.model.api.property.IProperty;

public interface ICellEditorFactory {

	public CellEditor createEditor(Object parentObject, Object object,
			final Viewer parent, IProperty property);
}