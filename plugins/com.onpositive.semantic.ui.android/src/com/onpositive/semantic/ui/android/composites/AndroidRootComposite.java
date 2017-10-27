package com.onpositive.semantic.ui.android.composites;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.onpositive.commons.platform.configuration.AndroidPlatform;
import com.onpositive.core.runtime.IPlatform;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;

public class AndroidRootComposite extends AndroidComposite{

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -4149075401173549602L;

	public AndroidRootComposite(Context context) {
		this.context=context;
		IPlatform platform = Platform.getPlatform();
		if (platform instanceof AndroidPlatform) {
			((AndroidPlatform) platform).setContext(context);
		}
		widget=new LinearLayout(getContext());
		widget.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setBinding(new Binding(context));
	}
	
	@Override
	public BasicUIElement<View> getRoot() {
		return this;
	}	
}
