package com.onpositive.businessdroids.mainmenu;

import android.graphics.drawable.Drawable;

import com.onpositive.businessdroids.ui.actions.ActionContribution;

public class StubAction extends ActionContribution {
	

	public StubAction(String text, Drawable icon) {
		super(text,icon);
	}

	@Override
	protected void run() {
		// Do nothing
	}

}
