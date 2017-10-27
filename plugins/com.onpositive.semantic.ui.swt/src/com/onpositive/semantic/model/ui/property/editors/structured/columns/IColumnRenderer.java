package com.onpositive.semantic.model.ui.property.editors.structured.columns;

import org.eclipse.swt.widgets.Event;

import com.onpositive.viewer.extension.coloring.OwnerDrawSupport;

public interface IColumnRenderer {

	void drawColumn(OwnerDrawSupport support, Event event);
}
