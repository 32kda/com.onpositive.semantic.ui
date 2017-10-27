package com.onpositive.businessdroids.ui.dashboard;

import android.content.Context;

import com.onpositive.businessdroids.model.IArray;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.dataview.ImageProviderService;
import com.onpositive.businessdroids.ui.dataview.renderers.ViewRendererService;

public class DashBoard extends AbstractViewer {

	private IArray cp;
	private IViewLabelProvider lp;
	private DashBoardComp dashBoardComp;

	public DashBoard(Context context, IArray cp, IViewLabelProvider lp) {
		super(context);
		this.cp = cp;
		this.lp = lp;
		setOrientation(VERTICAL);
	}

	public DashBoard(Context context, IArray cp, ILabelProvider lp) {
		super(context);
		setOrientation(VERTICAL);
		this.cp = cp;
		this.lp = new DefaultViewLabelProvider(lp);
		setOrientation(VERTICAL);
	}

	@Override
	protected void initView() {
		if (getWidth() > 0) {
			inited = true;
			dashBoardComp = new DashBoardComp(this, this.getContext(), cp, lp);
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			addView(dashBoardComp, layoutParams);
			addActionBar(this);
		}
	}

	@Override
	public ImageProviderService getImageProviderService() {
		//FIXME
		throw new UnsupportedOperationException();
		//return null;
	}

	@Override
	public ViewRendererService getViewRendererService() {
		throw new UnsupportedOperationException();
	}

}
