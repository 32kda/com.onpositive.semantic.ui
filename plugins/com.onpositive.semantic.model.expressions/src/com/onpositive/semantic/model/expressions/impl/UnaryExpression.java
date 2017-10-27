package com.onpositive.semantic.model.expressions.impl;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.GetPropertyExpression;
import com.onpositive.semantic.model.api.expressions.ICanWriteToQuery;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.ISubsitutableExpression;
import com.onpositive.semantic.model.api.expressions.VariableExpression;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;
import com.onpositive.semantic.model.expressions.operatorimplementations.UnaryOperator;
import com.onpositive.semantic.model.expressions.operatorimplementations.UnaryOperatorEvaluator;


public class UnaryExpression extends AbstractListenableExpression<Object>
		implements IValueListener<Object>,ISubsitutableExpression<Object>,ICanWriteToQuery {
	
	static UnaryOperatorEvaluator operatorEvaluator = new UnaryOperatorEvaluator() ;

	//private final char kind; 
	private final int kind;
	private final IListenableExpression<?> binding;

	public String getMessage() {
//		final Boolean bs1 = (Boolean) this.binding.getValue();
//		if ((bs1 == null) || !bs1.booleanValue()) {
			return this.binding.getMessage();
//		}
	}

	public UnaryExpression(IListenableExpression<?> binding, int c) {
		super();
		this.binding = binding;
		this.kind = c;
		binding.addValueListener(this);
		this.value = this.calculateValue();
	}

	public void dispose() {
		this.binding.removeValueListener(this);
		binding.disposeExpression();
		super.dispose();
	}

	public void valueChanged(Object oldValue, Object newValue) {
		this.setNewValue(this.calculateValue());
	}

	protected Object calculateValue()
	{
		 return operatorEvaluator.getOperatorValue( kind, binding.getValue() ) ;
	}

	@Override
	public ISubsitutableExpression<Object> substituteAllExcept(
			IListenableExpression<?> ve) {
		if (binding instanceof ISubsitutableExpression){
			ISubsitutableExpression<Object>o=(ISubsitutableExpression<Object>) binding;
			ISubsitutableExpression<Object> substituteAllExcept = o.substituteAllExcept(ve);
			if (substituteAllExcept!=null){
				UnaryExpression unaryExpression = new UnaryExpression(substituteAllExcept,kind);
				if (unaryExpression.isConstant()){
					return new ConstantExpression(unaryExpression.getValue());
				}
				return unaryExpression;
			}
		}
		return null;
	}

	@Override
	public boolean isConstant() {
		if (binding instanceof ISubsitutableExpression){
			ISubsitutableExpression<Object>o=(ISubsitutableExpression<Object>) binding;
			return o.isConstant();
		}
		return false;
	}

	public int getKind() {
		return kind;
	}

	public IListenableExpression<?> getBinding() {
		return binding;
	}

	@Override
	public boolean modify(Query q) {
		if (kind==UnaryOperator.L_NOT){
			String propId = "";
			if (binding instanceof GetPropertyExpression) {
				GetPropertyExpression m = (GetPropertyExpression) binding;
				propId = m.getPropertyId();
				if (!(m.getParent() instanceof VariableExpression)) {
					return false;
				}
			} else {
				return false;
			}
			q.addFilter(new QueryFilter(propId, false, QueryFilter.FILTER_EQUALS));
			return true;
		}
		return false;
	}
}

