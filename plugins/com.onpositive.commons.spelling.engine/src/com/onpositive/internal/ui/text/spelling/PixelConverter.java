package com.onpositive.internal.ui.text.spelling;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.dialogs.Dialog;

public class PixelConverter {

	private final FontMetrics fFontMetrics;

	public PixelConverter(Control control) {
		this(control.getFont());
	}

	public PixelConverter(Font font) {
		final GC gc = new GC(font.getDevice());
		gc.setFont(font);
		this.fFontMetrics = gc.getFontMetrics();
		gc.dispose();
	}

	/*
	 * see
	 * org.eclipse.jface.dialogs.DialogPage#convertHeightInCharsToPixels(int)
	 */
	public int convertHeightInCharsToPixels(int chars) {
		return Dialog.convertHeightInCharsToPixels(this.fFontMetrics, chars);
	}

	/*
	 * see
	 * org.eclipse.jface.dialogs.DialogPage#convertHorizontalDLUsToPixels(int)
	 */
	public int convertHorizontalDLUsToPixels(int dlus) {
		return Dialog.convertHorizontalDLUsToPixels(this.fFontMetrics, dlus);
	}

	/*
	 * see org.eclipse.jface.dialogs.DialogPage#convertVerticalDLUsToPixels(int)
	 */
	public int convertVerticalDLUsToPixels(int dlus) {
		return Dialog.convertVerticalDLUsToPixels(this.fFontMetrics, dlus);
	}

	/*
	 * see org.eclipse.jface.dialogs.DialogPage#convertWidthInCharsToPixels(int)
	 */
	public int convertWidthInCharsToPixels(int chars) {
		return Dialog.convertWidthInCharsToPixels(this.fFontMetrics, chars);
	}

}
