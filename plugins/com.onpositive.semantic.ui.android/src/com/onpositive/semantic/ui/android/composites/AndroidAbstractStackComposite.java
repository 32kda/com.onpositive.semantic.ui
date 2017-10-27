package com.onpositive.semantic.ui.android.composites;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;

public abstract class AndroidAbstractStackComposite extends AndroidComposite {

	private static final long serialVersionUID = -3060361866154043255L;
	protected BasicUIElement<?> topControl;

	public AndroidAbstractStackComposite() {
		super();
	}

	@Override
	protected View createControl(ICompositeElement<?,?> parent) {
		FrameLayout layout=new FrameLayout(getContext());		
		layout.setForegroundGravity(Gravity.TOP);
		return layout;
	}

	public BasicUIElement<?> getTopControl() {
		return topControl;
	}
	
	protected void adapt(BasicUIElement<View> element) {
		createChild(element);
		if (isCreated()){
			((FrameLayout)getControl()).addView(element.getControl());
		}
	}

	public void setTopControl(BasicUIElement<?> control) {
		if (topControl != null && topControl.isCreated()) {
			((View)topControl.getControl()).setVisibility(View.GONE);
		}
		this.topControl = control;
		if (topControl != null && topControl.isCreated()) {
			((View)topControl.getControl()).setVisibility(View.VISIBLE);
		}
		getControl().requestLayout();
	}

}