package com.onpositive.semantic.model.ui.actions;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.ReadOnly;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.BindingStack;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionBinding;

public class Commit extends ActionBinding {
	

	
	@Caption("%ok")
	@ReadOnly("!$.@status.Error")//FIXME (root needed)
	public void doAction() {
		IBinding bnd = BindingStack.getCaller();
		while (bnd.getParent() != null) {
			bnd = bnd.getParent();
		}
		try{
		bnd.commit();
		}catch (Throwable e) {
			((AbstractBinding)bnd).setupStatus(CodeAndMessage.errorMessage(e.getMessage()));
		}
	}
}
