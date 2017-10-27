package com.onpositive.businessdroids.mainmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.dataview.ImageProviderService;
import com.onpositive.businessdroids.ui.dataview.renderers.ViewRendererService;

public class MainMenuViewer extends AbstractViewer implements IRowHeightProvider{

	private static final int PORTRAIT_COLUMNS = 2;
	private static final int LANDSCAPE_COLUMNS = 3;
	private static final int PORTRAIT_ROWS = 3;
	protected static final int LANDSCAPE_ROWS = 2;
	private static final int VERTICAL_PADDING = 10;
	private GridView gridView;
	private MainMenuAdapter mainMenuAdapter;

	public MainMenuViewer(Context context) {
		super(context);
		setTitle("Encyclopedia");
		setOrientation(LinearLayout.VERTICAL);
	}

	@Override
	public ImageProviderService getImageProviderService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ViewRendererService getViewRendererService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initView() {
		if (getWidth() > 0) {
			inited = true;
			gridView = new GridView(getContext());
			gridView.setPadding(0,VERTICAL_PADDING,0,VERTICAL_PADDING);
			Display defaultDisplay = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
			if (defaultDisplay.getWidth() > defaultDisplay.getHeight()) 
				gridView.setNumColumns(LANDSCAPE_COLUMNS);
			else
				gridView.setNumColumns(PORTRAIT_COLUMNS);
			

			if (mainMenuAdapter != null)
				gridView.setAdapter(mainMenuAdapter);
			gridView.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//			LinearLayout layout = new LinearLayout(getContext());
//			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
//			layout.addView(gridView, layoutParams);
			gridView.setGravity(Gravity.CENTER);
			addActionBar(this);
			addView(gridView, layoutParams);
//			addView(textView);
		}
	}

	@Override
	public int getRowHeight() {
		if (getWidth() > getHeight()) {
			return (gridView.getHeight() - VERTICAL_PADDING * 2) / LANDSCAPE_ROWS;
		}
		return (gridView.getHeight() - VERTICAL_PADDING * 2) / PORTRAIT_ROWS;
	}

	public void setItems(ActionContribution[] items) {
		mainMenuAdapter = new MainMenuAdapter(getContext(), items);
		if (gridView != null) {
			gridView.setAdapter(mainMenuAdapter);
		}
	}

	public void setTitle(CharSequence title) {
		setTitle(title);
	}

}
