package com.onpositive.semantic.ui.android.composites;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.ui.android.customwidgets.AccorditionView;
import com.onpositive.semantic.ui.core.GenericLayoutHints;

public class AndroidAccordition extends AndroidComposite {

	private static final long serialVersionUID = 1L;
	
	public AndroidAccordition() {
		getLayoutHints().setGrabHorizontal(true);
	}
	
	@Override
	protected View createControl(ICompositeElement<?, ?> parent) {
		AccorditionView accorditionView = new AccorditionView(getContext());
		return accorditionView;
	}
	
	@Override
	protected void adapt(BasicUIElement<View> element) {
		createChild(element);
		if (isCreated()){
			ViewGroup m=(ViewGroup) getControl();
			GenericLayoutHints layoutHints = element.getLayoutHints();
			LinearLayout.LayoutParams p = createLayoutParams(layoutHints);
			configureLayoutParams(layoutHints, p);
			String caption = element.getCaption();
			if (caption == null)
				caption = "Element " + m.getChildCount();
//			m.addView(caption, element.getControl(),p);			
			((AccorditionView)m).addView(caption, element.getControl());
		}
	}
	
	@Override
	protected void addChildren() {
		super.addChildren();
		((AccorditionView)getControl()).createSections();
	}
	
	@Override
	protected LinearLayout.LayoutParams createLayoutParams(
			GenericLayoutHints layoutHints) {
		int i = layoutHints.getGrabHorizontal() ? LayoutParams.FILL_PARENT
				: LayoutParams.WRAP_CONTENT;
		int j = layoutHints.getGrabVertical() ? LayoutParams.FILL_PARENT
				: LayoutParams.WRAP_CONTENT;
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(i, j,
				layoutHints.getGrabHorizontal() ? 1 : 0);
		return p;
	}
	
}
