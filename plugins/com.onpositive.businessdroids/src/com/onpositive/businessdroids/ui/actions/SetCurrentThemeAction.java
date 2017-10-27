package com.onpositive.businessdroids.ui.actions;

import android.graphics.drawable.Drawable;

import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.themes.ITheme;


public class SetCurrentThemeAction extends ActionContribution {

	protected final ITheme theme;
	protected final IViewer dataView;

	public SetCurrentThemeAction(String text, Drawable icon, ITheme theme,
			IViewer dataView) {
		super(text, icon);
		this.theme = theme;
		this.dataView = dataView;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void run() {
		this.dataView.setCurrentTheme(this.theme);
	}

}
