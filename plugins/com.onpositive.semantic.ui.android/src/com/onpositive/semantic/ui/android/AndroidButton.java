package com.onpositive.semantic.ui.android;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.widgets.IButton;
import com.onpositive.semantic.model.ui.generic.widgets.impl.ButtonDelegate;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class AndroidButton extends SimpleAndroidEditor implements IButton<View>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AndroidButton() {
		getLayoutHints().setGrabHorizontal(false);
		getLayoutHints().setGrabVertical(true);
	}
	
	@Override
	protected void resetValue() {
		if (getControl()!=null){
		((Button)getControl()).setText(getCaption());
		}
		
	}
	
	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		final Button button = new Button(context);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((ButtonDelegate) delegate).handleChange(AndroidButton.this,button.isSelected());
//				if (binding!=null){
//					binding.actionPerformed(this, null);
//				}
			}
		});
		((ButtonDelegate) delegate).initValue();
		return button;
	}
	
	@HandlesAttributeDirectly("expressionToSet")
	public void setExpressionToSet(String expression){
		((ButtonDelegate)delegate).setExpressionToSet(expression);
	}

	@Override
	public boolean getSelection() {
		if (widget!=null){
			return ((Button)widget).isSelected();
		}
		return false;
	}

	@Override
	public void setSelection(boolean selected) {
		if (widget != null) {
			widget.setSelected(selected);
		}
		
	}

}
