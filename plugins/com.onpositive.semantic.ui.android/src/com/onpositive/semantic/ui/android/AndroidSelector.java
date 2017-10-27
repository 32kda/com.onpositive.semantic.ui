package com.onpositive.semantic.ui.android;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.IButton;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

public class AndroidSelector extends SimpleAndroidEditor implements IButton<View>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean radio;
	
	public AndroidSelector(boolean isRadio) {
		this.radio=isRadio;
	}
	
	@Override
	protected void resetValue() {
		if (isCreated()){
			Checkable x=(Checkable) getControl();
			if (x!=null){
			if (getBinding()!=null){
				Object value = binding.getValue();
				x.setChecked(value!=null?(Boolean) value:false);
			}
			}
		}
	}
	
	@Override
	public void setBinding(IBinding binding) {
		super.setBinding(binding);
	}
	
	@Override
	public void setCaption(String caption) {
		super.setCaption(caption);
		if (isCreated()){
			CompoundButton m= (CompoundButton) widget;
			m.setText(caption);
		}
	}

	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		CompoundButton checkBox = (CompoundButton) (createWidget(context));
		String caption = getCaption();
		if (caption!=null){
		checkBox.setText(caption);
		}
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (radio && isChecked) {
					for (IUIElement<?> m : getParent().getChildren()) {
						if (m instanceof AndroidSelector) {
							AndroidSelector c = (AndroidSelector) m;
							if (c.radio && c != AndroidSelector.this) {
								RadioButton b = (RadioButton) c.getControl();
								if (b.isChecked()) {
									b.setChecked(false);
								}
							}
						}
					}
				}
				commitToBinding(isChecked);
			}
		});
		return (View) checkBox;
	}

	protected CompoundButton createWidget(Context context) {
		if (radio){
			return new RadioButton(context);
		}
		return new CheckBox(context);
	}

	@Override
	public boolean getSelection() {
		if (widget!=null){
			return ((CompoundButton) widget).isChecked();
		}
		return false;
	}

	@Override
	public void setSelection(boolean selected) {
		if (isCreated()){
			((CompoundButton) widget).setChecked(selected);
		}
		
	}
}
