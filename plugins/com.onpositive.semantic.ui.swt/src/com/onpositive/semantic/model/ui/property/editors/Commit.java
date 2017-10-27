package com.onpositive.semantic.model.ui.property.editors;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Enablement;
import com.onpositive.semantic.model.binding.BindingStack;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionBinding;

public class Commit extends ActionBinding {
	

	
	@Caption("%ok")
	@Enablement("!error")
	public void doAction() {
		IBinding bnd = BindingStack.getCaller();
		while (bnd.getParent() != null) {
			bnd = bnd.getParent();
		}
		bnd.commit();
	}
}
