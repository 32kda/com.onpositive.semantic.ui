package com.onpositive.semantic.ui.android.composites;

import android.R;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;

public class AndroidTabFolder extends AndroidComposite{

	/**
	 * Serial Versio UID
	 */
	private static final long serialVersionUID = -1444353589761464822L;

	public AndroidTabFolder() {
		getLayoutHints().setGrabVertical(true);
		getLayoutHints().setGrabHorizontal(true);
	}
	
	@Override
	protected View createControl(ICompositeElement<?,?> parent) {
		TabHost ts=new TabHost(getContext());		
		TabWidget child = new TabWidget(getContext());
		FrameLayout child2 = new FrameLayout(getContext());
		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		ts.addView(linearLayout,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		child2.setId(R.id.tabcontent);
		child.setId(R.id.tabs);
		linearLayout.addView(child);
		linearLayout.addView(child2);
		ts.setup();
		return ts;
	}
	
	@Override
	protected void adapt(BasicUIElement<View> element) {
		createChild(element);
		final View w = element.getControl();
		TabHost g = (TabHost) getControl();
		TabSpec newTabSpec = g.newTabSpec(System.identityHashCode(element)+"");
		
		newTabSpec.setContent(new TabContentFactory() {
			
			@Override
			public View createTabContent(String tag) {
				return w;
			}
		});
		
		newTabSpec.setIndicator(element.getCaption());
		g.addTab(newTabSpec);
		//g.addView(t, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	}
}
