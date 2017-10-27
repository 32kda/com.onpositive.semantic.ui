package com.onpositive.semantic.ui.android.composites;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;

import com.onpositive.semantic.model.ui.generic.ICompositeElement;

public class AndroidVerticalGroup extends AndroidVerticalComposite {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 7757600422114387578L;
	private TextView captionTextView;

	@Override
	protected View createControl(ICompositeElement<?, ?> parent) {
		if (getCaption() != null && getCaption().length() > 0) {
			TableLayout layout = new TableLayout(getContext());
			TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			captionTextView = new TextView(getContext());
			captionTextView.setText(getCaption());
			captionTextView.setGravity(Gravity.CENTER);
			captionTextView.setTypeface(Typeface.create((String)null, Typeface.BOLD));
			captionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			captionTextView.setPadding(5,5,5,5);
			captionTextView.setBackgroundDrawable(new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{Color.BLACK, Color.DKGRAY, Color.BLACK}));
			layout.addView(captionTextView, layoutParams);
//			layout.setBaselineAlignedChildIndex(0);
			return layout;
		}
		return super.createControl(parent);
	}
	
	@Override
	public void setCaption(String caption) {
		super.setCaption(caption);
		if (isCreated()) {
			captionTextView.setText(caption);
		}
	}
	
}
