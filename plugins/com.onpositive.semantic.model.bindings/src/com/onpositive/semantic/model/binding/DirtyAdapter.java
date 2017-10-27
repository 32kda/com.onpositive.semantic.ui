package com.onpositive.semantic.model.binding;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;

public class DirtyAdapter extends AbstractListenableExpression<Boolean> implements IBindingChangeListener<Object>,ICommitListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	IBinding dnd;
	

	public DirtyAdapter(IBinding abstractBinding) {
		this.dnd=abstractBinding;
		abstractBinding.addBindingChangeListener(this);
		abstractBinding.addCommitListener(this);
	}

	@Override
	public Boolean getValue() {
		return dnd.isDirty();
	}

	@Override
	public void disposeExpression() {
		dnd.removeBindingChangeListener(this);
		dnd.removeCommitListener(this);
	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public void valueChanged(ISetDelta<Object> valueElements) {
		fireChanged(getValue());
	}

	@Override
	public void enablementChanged(boolean isEnabled) {
		
	}

	@Override
	public void changed() {
		fireChanged(getValue());
	}

	@Override
	public void commitPerformed(ICommand command) {
		fireChanged(getValue());
	}

}
