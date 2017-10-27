package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.changes.IValueListener;


public class MesagingExpression extends AbstractListenableExpression<Object>
		implements IValueListener<Object>,ISubsitutableExpression<Object> {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return this.message;
	}

	protected final IListenableExpression<?> exp;
	private final String message;

	public MesagingExpression(IListenableExpression<?> exp, String message) {
		super();
		this.exp = exp;
		this.message = message;
		exp.addValueListener(this);
		this.setNewValue(exp.getValue());
	}

	@Override
	public void dispose() {
		this.exp.removeValueListener(this);
		exp.disposeExpression();
		super.dispose();
	}

	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		this.setNewValue(newValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISubsitutableExpression<Object> substituteAllExcept(
			IListenableExpression<?> ve) {
		if (exp instanceof ISubsitutableExpression){
			ISubsitutableExpression<?>m=(ISubsitutableExpression<?>) exp;			
			return (ISubsitutableExpression<Object>) m.substituteAllExcept(ve);
		}
		return null;
	}

	@Override
	public boolean isConstant() {
		if (exp instanceof ISubsitutableExpression){
			ISubsitutableExpression<?>m=(ISubsitutableExpression<?>) exp;
			return m.isConstant();
		}
		return false;
	}

}
