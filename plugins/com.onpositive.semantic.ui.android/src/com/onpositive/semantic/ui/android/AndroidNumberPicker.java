package com.onpositive.semantic.ui.android;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;
import com.onpositive.semantic.ui.android.customwidgets.NumberPicker;

public class AndroidNumberPicker extends AndroidValueEditor{
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 6255169028896917798L;
	private int min = 0;
	private int max = 999;
	private int step = 1;
	
	@Override
	protected void resetValue() {
		if (isCreated()) {
			NumberPicker picker = (NumberPicker) getControl();
			if (picker != null) {
				if (getBinding() != null) {
					Object value = binding.getValue();
					picker.setValue(value != null ? (Integer) value : 0);
				}
			}
		}
	}

	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		NumberPicker picker = new NumberPicker(context, null);
		picker.setMinimum(min);
		picker.setMaximum(max);
		picker.setStep(step);
		picker.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				commitToBinding(Integer.parseInt(s.toString()));
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		return picker;
	}

	public int getMin() {
		return min;
	}

	@HandlesAttributeDirectly("min")
	public void setMin(int min) {
		this.min = min;
		if (isCreated()) {
			((NumberPicker) getControl()).setMinimum(min);
		}
	}

	public int getMax() {
		return max;
	}

	@HandlesAttributeDirectly("max")
	public void setMax(int max) {
		this.max = max;
		if (isCreated()) {
			((NumberPicker) getControl()).setMaximum(max);
		}
	}

	public int getStep() {
		return step;
	}

	@HandlesAttributeDirectly("step")
	public void setStep(int step) {
		this.step = step;
		if (isCreated()) {
			((NumberPicker) getControl()).setStep(step);
		}
	}

	@Override
	protected Object getDefaultSelection() {
		return min;
	}

	@Override
	protected void setSelection(Object value) {
		if (isCreated()) {
			((NumberPicker)getControl()).setValue((Integer) value);
		}
	}
	
	@Override
	public boolean needsLabel() {
		return true;
	}

}
