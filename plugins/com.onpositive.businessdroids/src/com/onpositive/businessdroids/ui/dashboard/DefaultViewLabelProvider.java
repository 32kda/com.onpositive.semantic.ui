package com.onpositive.businessdroids.ui.dashboard;

import android.content.Context;
import android.view.View;

public class DefaultViewLabelProvider implements IViewLabelProvider{

	final ILabelProvider provider;
	
	public DefaultViewLabelProvider(ILabelProvider provider) {
		super();
		this.provider = provider;
	}

	public View viewFor(Context ctx, Object element) {
		return new Icon(ctx, provider.getText(element), provider.getBitmap(element), element);
	}	

}
