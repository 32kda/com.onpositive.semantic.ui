package com.onpositive.semantic.ui.android;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.widgets.IMultitextElement;

public class AndroidMultilineTextEditor extends AndroidAbstractTextEditor implements IMultitextElement<View> {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -4417041863142391583L;
	private boolean wrapText;
	private boolean isMultiline = true;

	@Override
	public boolean isWrapText() {
		return wrapText;
	}

	@Override
	@HandlesAttributeDirectly("wrapText")
	public void setWrapText(boolean wrapText) {
		this.wrapText = wrapText;
	}

	@Override
	public boolean isMultiline() {
		return isMultiline;
	}
	
	@Override
	protected void configureTextView(MultiAutoCompleteTextView textView) {
		textView.setSingleLine(!isMultiline);
		if (isMultiline) {
			textView.setMinLines(2);
		}
		textView.setMinEms(10);
	}

	@Override
	@HandlesAttributeDirectly("multiline")
	public void setMultiline(boolean isMultiline) {
		this.isMultiline = isMultiline;
		if (isCreated()) {
			((AutoCompleteTextView) widget).setSingleLine(!isMultiline);
			if (isMultiline) {
				((TextView) widget).setMinLines(2);
			}
		}
	}
	
	@Override
	public boolean needsLabel() {
		return (getCaption() != null && getCaption().length() > 0);
	}

}
