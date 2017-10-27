package com.onpositive.semantic.ui.android.composites;

import android.view.View;
import android.widget.ScrollView;

import com.onpositive.semantic.model.ui.generic.ICompositeElement;

public class AndroidScrollable extends AndroidComposite {
	
	private static final long serialVersionUID = 72396472737164247L;

	public AndroidScrollable() {
		getLayoutHints().setGrabVertical(true);
	}
	
	@Override
	protected View createControl(ICompositeElement<?, ?> parent) {
		return new ScrollView(getContext());
	}

}
