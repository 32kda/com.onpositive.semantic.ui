package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public abstract class ComponentController extends AbstractComponentController {

	public ComponentController(IUIElement<?> editor, String expressionPath) {
		super(expressionPath,editor);		
	}

	protected void setValue(Object newValue) {
		
		if (newValue instanceof Boolean){
			Boolean bl = (Boolean) newValue;
			doOp(bl);
		}
		else {
			if (newValue == null)
				doOp(false);
			else
				doOp(true);
		}
	}

	protected abstract void doOp(boolean bl);

}