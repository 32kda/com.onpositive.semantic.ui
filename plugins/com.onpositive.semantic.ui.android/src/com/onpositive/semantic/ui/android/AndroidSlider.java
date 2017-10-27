package com.onpositive.semantic.ui.android;

import android.content.Context;
import android.view.View;
import android.widget.AbsSeekBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

public class AndroidSlider extends AndroidValueEditor {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 7806677325309910819L;
	private int min = 0;
	private int max = 100;

	public AndroidSlider() {
		getLayoutHints().setGrabHorizontal(true);
	}

	@Override
	protected void resetValue() {
		if (isCreated()) {
			AbsSeekBar bar = (AbsSeekBar) getControl();
			if (bar != null) {
				if (getBinding() != null) {
					Object value = binding.getValue();
					bar.setProgress(value != null ? (Integer) value - min : 0);
				}
			}
		}
	}

	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		SeekBar seekBar = new SeekBar(context);
		seekBar.setMax(max - min);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Do nothing for now
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Do nothing for now
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				commitToBinding(progress + min);

			}
		});
		return seekBar;
	}

	protected void setSeekBarRange() {
		((AbsSeekBar) widget).setMax(max - min);
	}

	public int getMin() {
		return min;
	}

	@HandlesAttributeDirectly("min")
	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	@HandlesAttributeDirectly("max")
	public void setMax(int max) {
		this.max = max;
	}
	
	@Override
	public boolean needsLabel() {
		return true;
	}
	


	protected void setSelection(Object value) {
		if (value instanceof Integer) {
			((AbsSeekBar) widget).setProgress(Math.min(max, Math.max(0, (Integer)value - min)));
		}
	}

	@Override
	protected Object getDefaultSelection() {
		return min;
	}

}
