package com.onpositive.semantic.ui.android;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class AndroidCheckbox extends AndroidSelector{

	public AndroidCheckbox() {
		super(false);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean radio;
	
	protected CompoundButton createWidget(Context context) {
		return new CheckBox(context);
	}
}
