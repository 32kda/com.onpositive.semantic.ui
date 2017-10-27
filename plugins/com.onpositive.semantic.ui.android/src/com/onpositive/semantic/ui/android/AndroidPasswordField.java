package com.onpositive.semantic.ui.android;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.widget.MultiAutoCompleteTextView;

public class AndroidPasswordField extends AndroidTextEditor {

	private static final long serialVersionUID = -9220295584612802176L;
	
	@Override
	protected void configureTextView(MultiAutoCompleteTextView textView) {
		super.configureTextView(textView);
		textView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		textView.setTransformationMethod(new PasswordTransformationMethod());
	}

}
