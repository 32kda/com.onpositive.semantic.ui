package com.onpositive.semantic.ui.android.composites;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.ui.core.GenericLayoutHints;

public class AndroidHorizontalComposite extends AndroidComposite {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -8018885234632776141L;

	@Override
	protected void adapt(BasicUIElement<View> element) {
		createChild(element);
		boolean needsLabel = element.needsLabel();
		LinearLayout parentLayout = getContentLayout();
		GenericLayoutHints layoutHints = element.getLayoutHints();
		int i = LayoutParams.WRAP_CONTENT;
		int j = layoutHints.getGrabVertical()?LayoutParams.FILL_PARENT:LayoutParams.WRAP_CONTENT;
		View control = element.getControl();
		if (control instanceof TextView){
			TextView t=(TextView) control;
			t.setGravity(Gravity.FILL);
		}
		if (needsLabel) {
			TextView tv = new TextView(getContext());
			tv.setPadding(2, 0, 2, 0);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setText(element.getCaption() + ':');
			parentLayout.addView(tv, LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
			LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(i, j,layoutHints.getGrabHorizontal()?1:0);
			configureLayoutParams(layoutHints,p);
			parentLayout.addView(control,p);
//			parentLayout.setBaselineAlignedChildIndex(1);
		} else {			
			LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(i, j,layoutHints.getGrabHorizontal()?1:0);		
			configureLayoutParams(layoutHints,p);
			parentLayout.addView(control,p);			
//			parentLayout.setBaselineAlignedChildIndex(0);
		}
	}
	
	@Override
	protected View createControl(ICompositeElement<?, ?> parent) {
		LinearLayout layout = (LinearLayout) super.createControl(parent);
		layout.setBaselineAligned(false);
		return layout;
	}

	protected LinearLayout getContentLayout() {
		return (LinearLayout) getControl();
	}
	
}
