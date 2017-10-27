package com.onpositive.semantic.ui.android.composites;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.onpositive.semantic.model.ui.generic.widgets.IStackComposite;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.StackBehaviourDelegate;
import com.onpositive.semantic.ui.core.GenericLayoutHints;

public class AndroidStackComposite extends AndroidAbstractStackComposite implements IStackComposite{
	
	private static final long serialVersionUID = 1L;
	@Override
	public void adapt(BasicUIElement<View> element) {
		createChild(element);
		((StackBehaviourDelegate) delegate).adapt(element);
		if (isCreated()){
			ViewGroup m=(ViewGroup) getControl();
			int i = LayoutParams.WRAP_CONTENT;
			GenericLayoutHints layoutHints = element.getLayoutHints();
			int j = layoutHints.getGrabVertical()?LayoutParams.FILL_PARENT:LayoutParams.WRAP_CONTENT;
			android.widget.FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(i, j,layoutHints.getGrabHorizontal()?1:0);
			if (topControl == element)
				element.getControl().setVisibility(View.VISIBLE);
			else
				element.getControl().setVisibility(View.GONE);
			configureLayoutParams(layoutHints, p);
			m.addView(element.getControl(),p);			
		}
	}

	@Override
	protected void unadapt(BasicUIElement<View> element) {
		((StackBehaviourDelegate) delegate).unadapt(element);
		super.unadapt(element);
	}

}
