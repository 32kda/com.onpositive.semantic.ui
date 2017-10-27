package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;


public class SimpleUngroupingActionContribution extends ActionContribution {

	protected final StructuredDataView dataView;

	public SimpleUngroupingActionContribution(String text, Drawable icon,
			StructuredDataView dataView) {
		super(text, icon);
		this.dataView = dataView;
	}

	@Override
	public boolean isEnabled() {
		return this.dataView.isGrouped();
	}
	
	@Override
	public String getId() {
		return "Group remove";
	}

	@Override
	public void run() {		
		this.dataView.getTableModel().setCurrentGrouping(null);
	}

}
