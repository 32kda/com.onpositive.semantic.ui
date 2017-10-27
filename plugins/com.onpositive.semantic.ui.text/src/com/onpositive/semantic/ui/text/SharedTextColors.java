package com.onpositive.semantic.ui.text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/*
 * @see org.eclipse.jface.text.source.ISharedTextColors
 * @since 2.1
 */
public class SharedTextColors implements ISharedTextColors {

	/** The display table. */
	private Map<Display, Map> fDisplayTable;

	/** Creates an returns a shared color manager. */
	public SharedTextColors() {
		super();
	}

	/*
	 * @see ISharedTextColors#getColor(RGB)
	 */
	public Color getColor(RGB rgb) {
		if (rgb == null) {
			return null;
		}

		if (this.fDisplayTable == null) {
			this.fDisplayTable = new HashMap<Display, Map>(2);
		}

		final Display display = Display.getCurrent();

		Map<RGB, Color> colorTable = this.fDisplayTable.get(display);
		if (colorTable == null) {
			colorTable = new HashMap<RGB, Color>(10);
			this.fDisplayTable.put(display, colorTable);
			display.disposeExec(new Runnable() {
				public void run() {
					SharedTextColors.this.dispose(display);
				}
			});
		}

		Color color = colorTable.get(rgb);
		if (color == null) {
			color = new Color(display, rgb);
			colorTable.put(rgb, color);
		}

		return color;
	}

	/*
	 * @see ISharedTextColors#dispose()
	 */
	public void dispose() {
		if (this.fDisplayTable == null) {
			return;
		}

		final Iterator<Map> iter = this.fDisplayTable.values().iterator();
		while (iter.hasNext()) {
			this.dispose(iter.next());
		}
		this.fDisplayTable = null;
	}

	/**
	 * Disposes the colors for the given display.
	 * 
	 * @param display
	 *            the display for which to dispose the colors
	 * @since 3.3
	 */
	private void dispose(Display display) {
		if (this.fDisplayTable != null) {
			this.dispose(this.fDisplayTable.remove(display));
		}
	}

	/**
	 * Disposes the given color table.
	 * 
	 * @param colorTable
	 *            the color table that maps <code>RGB</code> to
	 *            <code>Color</code>
	 * @since 3.3
	 */
	private void dispose(Map colorTable) {
		if (colorTable == null) {
			return;
		}

		final Iterator iter = colorTable.values().iterator();
		while (iter.hasNext()) {
			((Color) iter.next()).dispose();
		}

		colorTable.clear();
	}

}
