package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.changes.IValueListener;

public class ConstantExpression implements IListenableExpression<Object>,
		ISubsitutableExpression<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Object object;

	public ConstantExpression(Object object) {
		super();
		this.object = object;
	}

	@Override
	public void addValueListener(IValueListener<?> exp) {

	}

	@Override
	public Object getValue() {
		return this.object;
	}

	@Override
	public void removeValueListener(IValueListener<?> exp) {

	}

	public void dispose() {

	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public void disposeExpression() {

	}

	@Override
	public ISubsitutableExpression<Object> substituteAllExcept(
			IListenableExpression<?> ve) {
		return new ConstantExpression(object);
	}

	@Override
	public boolean isConstant() {
		return true;
	}

}
