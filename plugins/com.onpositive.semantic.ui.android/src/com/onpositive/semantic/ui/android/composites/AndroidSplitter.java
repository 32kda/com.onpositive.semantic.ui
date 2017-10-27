package com.onpositive.semantic.ui.android.composites;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.ui.android.customwidgets.IcsLinearLayout;
import com.onpositive.semantic.ui.core.GenericLayoutHints;

public class AndroidSplitter extends AndroidComposite {

	protected static final int DIVIDER_SIZE = 12;

	private static final long serialVersionUID = 7369680961749021086L;
	
	private boolean horizontal;
	private int[] weights;

	public int[] getWeights() {
		return this.weights;
	}

	@HandlesAttributeDirectly("weights")
	public void setWeights(int[] weights) {
		this.weights = weights;
		if (this.isCreated()) {
			if (weights != null) {
				setWeightsToControl(weights);
			}
		}
	}
	
	@Override
	protected View createControl(ICompositeElement<?,?> parent) {
		IcsLinearLayout layout=new IcsLinearLayout(getContext());			
		GradientDrawable drawable = new GradientDrawable(horizontal?Orientation.LEFT_RIGHT:Orientation.TOP_BOTTOM,new int[]{Color.BLACK,Color.DKGRAY,Color.BLACK});
		layout.setDividerDrawable(drawable);
		layout.setOrientation(horizontal ?  LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
		if (horizontal) {
			layout.setDividerWidth(DIVIDER_SIZE);
		} else {
			layout.setDividerHeight(DIVIDER_SIZE);
		}
		layout.setShowDividers(IcsLinearLayout.SHOW_DIVIDER_MIDDLE);
		return layout;
	}
	
	protected float calcWeightSum() {
		float sum = (float) 0.0;
		for (int i = 0; i < weights.length; i++) {
			sum += weights[i];
		}
		if (sum == 0.0f)
			sum = 1;
		return sum;
	}

	@HandlesAttributeDirectly("horizontal")
	public void setHorizontal(boolean isHorizontal) {
		this.horizontal = isHorizontal;
		if (this.isCreated()) {
			((LinearLayout) this.getControl()).setOrientation(
					(this.horizontal ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL));
		}
	}

	protected void setWeightsToControl(int[] weights) {
		float total = calcWeightSum();
		int count = ((LinearLayout)getControl()).getChildCount();
		for (int i = 0; i < weights.length; i++) {
			if (i < count) {
				((LayoutParams)((ViewGroup) getControl()).getChildAt(i).getLayoutParams()).weight = (float) (weights[i] * 1.0 / total);
			}
		}
	}
	
	@Override
	protected LayoutParams createLayoutParams(GenericLayoutHints layoutHints) {
		int i = 0;
		if (!horizontal) {
			i = layoutHints.getGrabHorizontal()?LayoutParams.FILL_PARENT:LayoutParams.WRAP_CONTENT;
		}
		int j = 0;
		if (horizontal) {
			j = layoutHints.getGrabVertical()?LayoutParams.FILL_PARENT:LayoutParams.WRAP_CONTENT;
		}
		LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(i, j);
		return p;
	}
	
	@Override
	protected void configureLayoutParams(GenericLayoutHints layoutHints,
			MarginLayoutParams params) {
		super.configureLayoutParams(layoutHints, params);
		float total = calcWeightSum();
		int count = ((LinearLayout)getControl()).getChildCount();
		if (count < weights.length) {
			((LayoutParams)params).weight = (float) (weights[count] * 1.0 / total);
		}
	}
	
	@Override
	protected void configureTotalWeight(ViewGroup t) {
		if (t instanceof LinearLayout){
			((LinearLayout)t).setWeightSum(1.0f);
		}
	}
	
}
