package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;


public class ComponentVisibilityController extends ComponentController{

	public ComponentVisibilityController(IUIElement<?> editor, String path) {
		super(editor, path);
	}

	
	protected void doOp(boolean bl) {
		editor.setDisplayable(bl);
	}

	
}
