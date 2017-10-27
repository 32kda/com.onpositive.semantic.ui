package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;

public class SortAction {

	public static final int UNSORTED = 0;
	public static final int ASCENDING = 1;
	public static final int DESCENDING = 2;

	protected int state = SortAction.UNSORTED;
	protected final TableModel model;
	protected final IColumn column;

	public SortAction(TableModel model, IColumn column) {
		this.model = model;
		this.column = column;
	}

	public int run() {
		if (this.state == SortAction.UNSORTED) {
			this.state = SortAction.ASCENDING;
		} else if (this.state == SortAction.ASCENDING) {
			this.state = SortAction.DESCENDING;
		} else if (this.state == SortAction.DESCENDING) {
			this.state = SortAction.ASCENDING;
		}
		if (model != null) {
			this.model.sort(this.column, (this.state == SortAction.ASCENDING));
		}
		return this.state;
	}

	public int getState() {
		return this.state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
