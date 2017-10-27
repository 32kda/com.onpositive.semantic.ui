package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;



public class ComponentEnablementController extends
		ComponentController {

	public ComponentEnablementController(IUIElement<?> modelElement, String path)
	{
		super( modelElement, path );
		if (modelElement instanceof IPropertyEditor){
			((IPropertyEditor<?>) modelElement).setEnablementFromBinding(false);
		}		
	}

	protected void doOp(boolean val) {
		editor.setEnabled(val);
	}

	
}
