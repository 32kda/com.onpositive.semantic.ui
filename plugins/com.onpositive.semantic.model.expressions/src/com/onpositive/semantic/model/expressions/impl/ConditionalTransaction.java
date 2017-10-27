package com.onpositive.semantic.model.expressions.impl;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.ICanWriteToQuery;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.ISubsitutableExpression;
import com.onpositive.semantic.model.api.query.Query;

public class ConditionalTransaction extends
		AbstractListenableExpression<Object> implements IValueListener<Object>,
		ISubsitutableExpression<Object>,ICanWriteToQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final IListenableExpression<?> condition;
	private final IListenableExpression<?> successCase;
	private final IListenableExpression<?> failCase;

	public ConditionalTransaction(IListenableExpression<?> condition,
			IListenableExpression<?> successCase,
			IListenableExpression<?> failCase) {
		super();
		this.condition = condition;
		this.successCase = successCase;
		this.failCase = failCase;
		condition.addValueListener(this);
		successCase.addValueListener(this);
		failCase.addValueListener(this);

		this.value = this.calculateValue();
	}

	public void dispose() {
		this.condition.removeValueListener(this);
		this.successCase.removeValueListener(this);
		this.failCase.removeValueListener(this);

		this.condition.disposeExpression();
		this.successCase.disposeExpression();
		this.failCase.disposeExpression();
		super.dispose();
	}

	public String getMessage() {

		boolean booleanConditionValue = getBooleanCondition();
		return booleanConditionValue ? successCase.getMessage() : failCase
				.getMessage();
	}

	protected Object calculateValue() {
		boolean booleanConditionValue = getBooleanCondition();
		return booleanConditionValue ? successCase.getValue() : failCase
				.getValue();
	}

	public void valueChanged(Object oldValue, Object newValue) {
		this.setNewValue(this.calculateValue());
	}

	protected boolean getBooleanCondition() {

		Object conditionValue = condition.getValue();
		if (conditionValue == null)
			return false;

		return (conditionValue instanceof Boolean) ? Boolean.class
				.cast(conditionValue) : true;
	}

	@Override
	public ISubsitutableExpression<Object> substituteAllExcept(
			IListenableExpression<?> ve) {
		if (condition instanceof ISubsitutableExpression<?>) {
			if (failCase instanceof ISubsitutableExpression<?>) {
				if (successCase instanceof ISubsitutableExpression<?>) {
					ISubsitutableExpression<?> z = (ISubsitutableExpression<?>) condition;
					ISubsitutableExpression<?> z1 = (ISubsitutableExpression<?>) failCase;
					ISubsitutableExpression<?> z2 = (ISubsitutableExpression<?>) successCase;
					ISubsitutableExpression<?> s1 = z.substituteAllExcept(ve);
					ISubsitutableExpression<?> s2 = z1.substituteAllExcept(ve);
					ISubsitutableExpression<?> s3 = z2.substituteAllExcept(ve);
					if (s1 != null && s2 != null && s3 != null) {
						ConditionalTransaction conditionalTransaction = new ConditionalTransaction(
								s1, s3, s2);
						if (conditionalTransaction.isConstant()) {
							return new ConstantExpression(
									conditionalTransaction.getValue());
						}
						return conditionalTransaction;
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean isConstant() {
		if (condition instanceof ISubsitutableExpression<?>) {
			if (failCase instanceof ISubsitutableExpression<?>) {
				if (successCase instanceof ISubsitutableExpression<?>) {
					ISubsitutableExpression<?> z = (ISubsitutableExpression<?>) condition;
					ISubsitutableExpression<?> z1 = (ISubsitutableExpression<?>) failCase;
					ISubsitutableExpression<?> z2 = (ISubsitutableExpression<?>) successCase;
					return z.isConstant() && z1.isConstant() && z2.isConstant();
				}
			}
		}
		return false;
	}

	@Override
	public boolean modify(Query q) {
		return false;
	}
}
