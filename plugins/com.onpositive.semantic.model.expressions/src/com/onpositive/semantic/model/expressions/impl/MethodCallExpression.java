package com.onpositive.semantic.model.expressions.impl;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.method.IMethodEvaluator;
import com.onpositive.semantic.model.api.method.MethodAccess;
import com.onpositive.semantic.model.expressions.operatorimplementations.BinaryOperator;

public class MethodCallExpression extends AbstractListenableExpression<Object> implements IValueListener<Object>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1368399718821118993L;
	protected String methodName;
	protected IListenableExpression<Object> baseExpression;
	protected final IListenableExpression<?> argumentExpression;
	protected IMethodEvaluator methodCallEvaluator;
	
	@SuppressWarnings("unchecked")
	public MethodCallExpression(IListenableExpression<?> baseExpression, String methodName, IListenableExpression<?> argumentExpression) {
		this.methodName = methodName;
		this.argumentExpression = argumentExpression;
		if (argumentExpression != null) {
			argumentExpression.addValueListener((IValueListener<?>) this);
		}
		baseExpression.addValueListener(this);
		this.baseExpression = (IListenableExpression<Object>) baseExpression;
		this.value = this.calculateValue();
	}
	
	protected Object calculateValue() {
		Object base = baseExpression.getValue();
		methodCallEvaluator = MethodAccess.getMethodEvaluator(base,methodName);
		if (argumentExpression != null) {
			return methodCallEvaluator.evaluateCall(base, getArgumentArray(argumentExpression));
		}else{
			return methodCallEvaluator.evaluateCall(base, new Object[0]);
		}
	}

	protected Object[] getArgumentArray(IListenableExpression<?> argumentExpression) {
		List<IListenableExpression<?>> unwrappedArgs = new ArrayList<IListenableExpression<?>>();
		if (argumentExpression instanceof BinaryExpression && ((BinaryExpression) argumentExpression).getKind() == BinaryOperator.COMMA) {
			unwrap(argumentExpression, unwrappedArgs);
		} else {
			unwrappedArgs.add(argumentExpression);
		}
		Object[] args = new Object[unwrappedArgs.size()];
		for (int i = 0; i < args.length; i++) {
			args[i] = unwrappedArgs.get(i).getValue();
		}
		return args;
	}

	private void unwrap(IListenableExpression<?> expression,
			List<IListenableExpression<?>> unwrapped) {
		IListenableExpression<?> operand1 = ((BinaryExpression) expression).getOperand1();
		if (operand1 instanceof BinaryExpression && ((BinaryExpression) operand1).getKind() == BinaryOperator.COMMA)
			unwrap(operand1,unwrapped);
		else 
			unwrapped.add(operand1);
		unwrapped.add(((BinaryExpression) expression).getOperand2());
	}

	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		setNewValue(calculateValue());
	}


}
