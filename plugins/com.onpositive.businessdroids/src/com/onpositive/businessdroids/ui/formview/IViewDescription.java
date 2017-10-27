package com.onpositive.businessdroids.ui.formview;

import android.view.View;

import com.onpositive.businessdroids.ui.actions.IContributionItem;

public interface IViewDescription {

	
	int getRequiredHeight();
	int getRequiredWidth();
	
	boolean canGrabVertical();
	boolean canGrabHorizontal();
	
	String getLabel();
	
	IContributionItem[] actions();
	
	IViewDescription[] getChildDescriptions();
	
	View getView();
	View getLabelView();
}
