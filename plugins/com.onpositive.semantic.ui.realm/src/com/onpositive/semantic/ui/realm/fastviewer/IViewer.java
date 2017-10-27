package com.onpositive.semantic.ui.realm.fastviewer;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Control;


public interface IViewer extends ISelectionProvider{

	boolean isTree();

	void setInput(Object input);

	void refresh();

	Control getControl();
	
	void configure(ViewerConfiguration configuration,boolean persist);

	StructuredViewer getStructuredViewer();

	void removeColumn(FastTreeColumn column);
	
	void addColumn(FastTreeColumn column);

	int[] getColumnOrder();

	void collapseAll();

	Object getInput();

	Object getState();
}
