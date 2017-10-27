package com.onpositive.semantic.ui.realm.fastviewer;

import org.eclipse.swt.widgets.Event;

public interface IColumnRenderer {

	public void renderColumn(RowItem item, Event event);

	public int  measureWidth(RowItem node);

	public String getText(Object el);

}
