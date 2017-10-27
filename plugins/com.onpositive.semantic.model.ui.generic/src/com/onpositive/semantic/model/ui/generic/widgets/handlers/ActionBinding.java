package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;

public abstract class ActionBinding extends Binding {

	public ActionBinding() {
		super(null, "doAction", null); //$NON-NLS-1$		
		this.setRegisterListeners(false);
	}

	public void setObject(Object newObject) {

	}

	public String getId() {
		return System.identityHashCode(this) + ""; //$NON-NLS-1$
	}

	public void setParent(AbstractBinding abstractBinding) {
		super.setParent(abstractBinding);
		super.setObject(this);
		//System.out.println(isReadOnly());
	}
	
	
	public void actionPerformed(Object object, Object extras) {
		setValue(null, null);
	}
	public void setValue(Object value, IBindingChangeListener<?> client){
		startCommit(this.getParent());
		try{
		doAction();
		}finally{
			finishCommit(this.getParent());
		}
	}
	protected boolean isReadonlyWithoutProperty() {
		return false;
	}
	
	public abstract void doAction();
}
