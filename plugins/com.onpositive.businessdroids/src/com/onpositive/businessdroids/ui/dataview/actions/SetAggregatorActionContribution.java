package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.aggregation.IModesProvider;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.actions.ActionContribution;

import android.graphics.drawable.Drawable;


public class SetAggregatorActionContribution extends ActionContribution {

	protected final IAggregator aggregator;
	protected final IColumn column;
	protected final AbstractViewer dataView;
	protected int mode = -1;

	public SetAggregatorActionContribution(String text, Drawable icon,
			IColumn column, IAggregator aggregator, AbstractViewer dataView) {
		super(text, icon);
		this.column = column;
		this.aggregator = aggregator;
		this.dataView = dataView;
	}

	public SetAggregatorActionContribution(String text, Drawable icon,
			IColumn column, IAggregator aggregator,
			AbstractViewer dataView, int mode) {
		super(text, icon);
		this.column = column;
		this.aggregator = aggregator;
		this.dataView = dataView;
		this.mode = mode;
	}

	@Override
	public boolean isEnabled() {
		IAggregator oldAggregator = this.column.getAggregator();
		if (oldAggregator == null) {
			return true;
		}
		if (oldAggregator.getTitle().equals(this.aggregator.getTitle())
				&& (oldAggregator instanceof IModesProvider)
				&& (this.mode > -1)) {
			return ((IModesProvider) oldAggregator).getMode() != this.mode;
		}
		return !oldAggregator.getTitle().equals(this.aggregator.getTitle());
	}

	@Override
	public void run() {
		if ((this.aggregator instanceof IModesProvider) && (this.mode > -1)) {
			((IModesProvider) this.aggregator).setMode(this.mode);
		}
		this.column.setAggregator(this.aggregator);
	}

}
