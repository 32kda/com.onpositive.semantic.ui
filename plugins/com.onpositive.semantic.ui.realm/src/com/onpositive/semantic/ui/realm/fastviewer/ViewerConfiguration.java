package com.onpositive.semantic.ui.realm.fastviewer;

public class ViewerConfiguration {

	public final FastTreeColumn columns[];
	public final Object viewerInput;
	public final boolean isTree;
	public final int[] columnOrder;
	public final String id;
	public final String sortColumnId;
	public final boolean sortDirection;
	
	public ViewerConfiguration(FastTreeColumn[] columns, Object viewerInput,boolean isTree,int[] columnOrder, String id,String soString,boolean sortDirection) {
		super();
		this.columns = columns;
		this.sortColumnId=soString;
		this.sortDirection=sortDirection;
		this.viewerInput = viewerInput;
		this.isTree=isTree;
		this.columnOrder=columnOrder;
		this.id=id;
	}
	
	public Object state;
}
