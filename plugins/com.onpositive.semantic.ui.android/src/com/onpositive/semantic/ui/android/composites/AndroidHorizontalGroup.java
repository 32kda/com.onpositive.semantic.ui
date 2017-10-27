package com.onpositive.semantic.ui.android.composites;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.onpositive.semantic.model.ui.generic.ICompositeElement;

public class AndroidHorizontalGroup extends AndroidHorizontalComposite {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 227653333305102941L;
	private LinearLayout contentLayout;
	private TextView captionTextView;
	
	@Override
	protected View createControl(ICompositeElement<?, ?> parent) {
		LinearLayout layout=new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		captionTextView = new TextView(getContext());
		captionTextView.setText(getCaption());
		captionTextView.setGravity(Gravity.CENTER);
		captionTextView.setTypeface(Typeface.create((String)null, Typeface.BOLD));
		captionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		captionTextView.setPadding(5,5,5,5);
		captionTextView.setBackgroundDrawable(new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{Color.BLACK, Color.DKGRAY, Color.BLACK}));
		layout.addView(captionTextView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		contentLayout = new LinearLayout(context);
		layout.addView(contentLayout, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, getLayoutHints().getGrabHorizontal()?LayoutParams.FILL_PARENT:LayoutParams.WRAP_CONTENT));
		layout.setBaselineAligned(false);
		return layout;
	}
	
	@Override
	protected LinearLayout getContentLayout() {
		return contentLayout;
	}
	
	@Override
	public void setCaption(String caption) {
		super.setCaption(caption);
		if (isCreated()) {
			captionTextView.setText(caption);
		}
	}

}
