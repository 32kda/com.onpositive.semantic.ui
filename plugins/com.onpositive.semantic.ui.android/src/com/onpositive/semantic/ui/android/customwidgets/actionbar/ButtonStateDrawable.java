package com.onpositive.semantic.ui.android.customwidgets.actionbar;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Simple {@link StateListDrawable} wrapper for easy creating button bg
 * drawables
 * 
 * @author 32kda
 * 
 */
public class ButtonStateDrawable extends StateListDrawable {

	/**
	 * Constructor
	 * 
	 * @param unselectedDrawable
	 *            Unselected control bg drawable. Can't be <code>null</code>
	 * @param selectedDrawable
	 *            Selected control bg drawable. Can be <code>null</code>
	 * @param focusedDrawable
	 *            Focused control bg drawable. Can be <code>null</code>
	 */
	protected ButtonStateDrawable(Drawable unselectedDrawable,
			Drawable selectedDrawable, Drawable focusedDrawable) {
		this.addState(
				new int[] { -android.R.attr.state_pressed,
						-android.R.attr.state_selected,
						-android.R.attr.state_focused }, unselectedDrawable);
		if (selectedDrawable != null) {
			this.addState(new int[] { android.R.attr.state_pressed },
					selectedDrawable);
			this.addState(new int[] { android.R.attr.state_selected },
					selectedDrawable);
		}
		if (focusedDrawable != null) {
			this.addState(new int[] { android.R.attr.state_focused },
					focusedDrawable);
		}
	}

}
