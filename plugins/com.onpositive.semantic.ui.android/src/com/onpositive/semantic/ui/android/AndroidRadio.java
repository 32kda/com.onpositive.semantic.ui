package com.onpositive.semantic.ui.android;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.RadioButton;


public class AndroidRadio extends AndroidSelector {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3186559407054468777L;

	public AndroidRadio() {
		super(true);
	}
	
	@Override
	protected CompoundButton createWidget(Context context) {
		return new RadioButton(context);
	}

}
