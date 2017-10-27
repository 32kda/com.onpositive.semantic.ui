package com.onpositive.semantic.model.ui.property.editors;

import org.eclipse.swt.widgets.Display;

import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionBinding;

public class CloseShell extends ActionBinding {

	public void doAction() {
		Display.getCurrent().getActiveShell().close();
	}
// for debug 
//	public void setParent(AbstractBinding abstractBinding) {
//		super.setParent(abstractBinding);
//	}

}
