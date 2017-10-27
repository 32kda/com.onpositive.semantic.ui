package com.onpositive.businessdroids.ui;

import com.onpositive.businessdroids.ui.dataview.ImageProviderService;
import com.onpositive.businessdroids.ui.dataview.renderers.ViewRendererService;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.view.View;


public interface IViewer {

	ITheme getCurrentTheme();
	
	Context getContext();
	
	View getView();

	void setCurrentTheme(ITheme theme);

	//FIXME
	ImageProviderService getImageProviderService();
	//FIXME
	ViewRendererService getViewRendererService();
}
