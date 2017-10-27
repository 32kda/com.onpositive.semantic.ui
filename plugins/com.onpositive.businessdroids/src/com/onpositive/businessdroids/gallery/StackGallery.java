package com.onpositive.businessdroids.gallery;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;

public class StackGallery extends AdapterView {

	protected Adapter adapter;
	protected FrameLayout layout;
	protected View[] views;
	private int selection;
	private View selectedView; 

	public StackGallery(Context context) {
		super(context);
		initView();
	}

	public StackGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public StackGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	protected void initView() {
		layout = new FrameLayout(getContext());
		addView(layout);
	}

	@Override
	public Adapter getAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getSelectedView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		this.adapter = adapter;
		if (adapter != null) {
			views = new View[adapter.getCount()];
			if (adapter.getCount() > 0)
				setSelection(0);
		}
	}

	@Override
	public void setSelection(int selection) {
		if (selection < 0 || selection > views.length)
			throw new IndexOutOfBoundsException("Index out of bounds, should be 0<index<" + views.length + ", actually " + selection);
		this.selection = selection;
		if (views[selection] != null)
			selectView(views[selection]);
		else {
			View view = adapter.getView(selection,null,layout);
			view.setLayoutParams(new FrameLayout.LayoutParams(getWidth(),getHeight(),Gravity.TOP));
			views[selection] = view;
			selectView(view);
		}
	}

	private void selectView(View view) {
		if (view == null)
			throw new AssertionError("Argument cannot be null");
		((FrameLayout.LayoutParams)selectedView.getLayoutParams()).gravity = Gravity.BOTTOM;
		selectedView = view;
		((FrameLayout.LayoutParams)view.getLayoutParams()).gravity = Gravity.TOP;
	}

}
