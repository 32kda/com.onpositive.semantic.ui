package com.onpositive.semantic.ui.android;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class AndroidSeparator extends AndroidUIElement {
	
	private static final int VERTICAL_PADDING = 5;
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -397245682489934987L;
	boolean vertical = false;
	
	public AndroidSeparator() {
		getLayoutHints().setGrabHorizontal(!vertical);
		getLayoutHints().setGrabVertical(vertical);
	}

	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		ImageView separatorView = new ImageView(context);
		separatorView.setImageResource(android.R.drawable.divider_horizontal_dark);
		separatorView.setPadding(5,VERTICAL_PADDING,5,2);
		separatorView.setScaleType(ScaleType.FIT_XY);
		return separatorView;
	}

	public boolean isVertical() {
		return vertical;
	}

	@HandlesAttributeDirectly("vertical")
	public void setVertical(boolean vertical) {
		this.vertical = vertical;
		getLayoutHints().setGrabHorizontal(!vertical);
		getLayoutHints().setGrabVertical(vertical);
	}

}
