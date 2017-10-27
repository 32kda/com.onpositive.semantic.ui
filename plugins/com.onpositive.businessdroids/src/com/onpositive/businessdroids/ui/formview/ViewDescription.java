package com.onpositive.businessdroids.ui.formview;

import java.util.ArrayList;

import android.view.View;

import com.onpositive.businessdroids.ui.actions.IContributionItem;

public class ViewDescription implements IViewDescription{

	protected ArrayList<IContributionItem>actions=new ArrayList<IContributionItem>();
	protected ArrayList<IViewDescription>views=new ArrayList<IViewDescription>();
	protected String label;
	
	protected int height;
	protected int width;
	protected boolean grabVertical;
	protected boolean grabHorizontal;
	protected View view;
	protected View labelView;
	
	@Override
	public int getRequiredHeight() {
		return height;
	}

	@Override
	public int getRequiredWidth() {
		return width;
	}

	@Override
	public boolean canGrabVertical() {
		return grabVertical;
	}

	@Override
	public boolean canGrabHorizontal() {
		return grabHorizontal;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public IContributionItem[] actions() {
		return actions.toArray(new IContributionItem[actions.size()]);
	}

	@Override
	public IViewDescription[] getChildDescriptions() {
		return views.toArray(new IViewDescription[views.size()]);
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public View getLabelView() {
		return labelView;
	}

}
