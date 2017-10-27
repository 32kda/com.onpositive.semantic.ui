package com.onpositive.semantic.model.expressions.impl;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.GetPropertyExpression;
import com.onpositive.semantic.model.api.expressions.ICanWriteToQuery;
import com.onpositive.semantic.model.api.expressions.IEditableExpression;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.ISubsitutableExpression;
import com.onpositive.semantic.model.api.expressions.VariableExpression;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;
import com.onpositive.semantic.model.expressions.operatorimplementations.BinaryOperator;
import com.onpositive.semantic.model.expressions.operatorimplementations.BinaryOperatorEvaluator;

public class BinaryExpression extends AbstractListenableExpression<Object>
		implements IValueListener<Object>, ISubsitutableExpression<Object>,
		ICanWriteToQuery,IEditableExpression<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static BinaryOperatorEvaluator operatorEvaluator = new BinaryOperatorEvaluator();

	// private final char kind;
	private int kind;
	private IListenableExpression<?> binding;
	private IListenableExpression<?> binding1;

	public String getMessage() {
		final Boolean bs1 = (Boolean) this.binding.getValue();
		if ((bs1 == null) || !bs1.booleanValue()) {
			return this.binding.getMessage();
		}
		return this.binding1.getMessage();
	}

	public BinaryExpression(IListenableExpression<?> binding,
			IListenableExpression<?> binding1, int c) {
		super();
		this.binding = binding;
		this.binding1 = binding1;
		this.kind = c;
		binding.addValueListener(this);
		binding1.addValueListener(this);
		this.value = this.calculateValue();
	}
	boolean disposed=false;

	public void dispose() {
		
		disposed=true;
		this.binding.removeValueListener(this);
		this.binding1.removeValueListener(this);
		binding.disposeExpression();
		binding1.disposeExpression();
		super.dispose();
	}

	public void valueChanged(Object oldValue, Object newValue) {
		this.setNewValue(this.calculateValue());
	}

	protected Object calculateValue() {
		return operatorEvaluator.getOperatorValue(kind, binding.getValue(),
				binding1.getValue());
		// try {
		// Object value2 = this.binding1.getValue();
		// Boolean bs1 = toBoolean(value2);
		// if (bs1 == null) {
		// bs1 = false;
		// }
		// Object value3 = this.binding.getValue();
		// Boolean bs = toBoolean(value3);
		// if (bs == null) {
		// bs = false;
		// }
		// if (this.kind == '&') {
		// return bs && bs1;
		// }
		// if (this.kind == '|') {
		// return bs || bs1;
		// }
		// } catch (final ClassCastException e) {
		// return false;
		// }
		// return null;
	}

	//
	// protected Boolean toBoolean(Object value2) {
	// if (value2 instanceof Boolean){
	// return (Boolean) value2;
	// }
	// if (value2 instanceof Collection){
	// Collection<?> m = (Collection<?>) value2;
	// if (m.isEmpty()){
	// return false;
	// }
	// }
	// return value2!=null;
	// }

	@Override
	public ISubsitutableExpression<Object> substituteAllExcept(
			IListenableExpression<?> ve) {
		if (binding instanceof ISubsitutableExpression<?>) {
			if (binding1 instanceof ISubsitutableExpression<?>) {
				ISubsitutableExpression<?> z = (ISubsitutableExpression<?>) binding;
				ISubsitutableExpression<?> z1 = (ISubsitutableExpression<?>) binding1;
				ISubsitutableExpression<?> s1 = z.substituteAllExcept(ve);
				ISubsitutableExpression<?> s2 = z1.substituteAllExcept(ve);
				if (s1 != null && s2 != null) {
					BinaryExpression binaryExpression = new BinaryExpression(
							s1, s2, kind);
					if (binaryExpression.isConstant()) {
						return new ConstantExpression(
								binaryExpression.getValue());
					}
					return binaryExpression;
				}
			}
		}
		return null;
	}

	@Override
	public boolean isConstant() {
		if (binding instanceof ISubsitutableExpression<?>) {
			if (binding1 instanceof ISubsitutableExpression<?>) {
				ISubsitutableExpression<?> z = (ISubsitutableExpression<?>) binding;
				ISubsitutableExpression<?> z1 = (ISubsitutableExpression<?>) binding1;
				return z.isConstant() && z1.isConstant();
			}
		}
		return false;
	}

	@Override
	public boolean modify(Query q) {
		if (kind == BinaryOperator.L_OR || kind == BinaryOperator.COMMA) {
			Query  zz=new Query(q.getKind());
			Query  zz1=new Query(q.getKind());
			if (binding instanceof ICanWriteToQuery) {
				ICanWriteToQuery m = (ICanWriteToQuery) binding;
				
				if (!m.modify(zz)) {
					return false;
				}
			} else {
				return false;
			}
			if (binding1 instanceof ICanWriteToQuery) {
				ICanWriteToQuery m = (ICanWriteToQuery) binding1;
				if (!m.modify(zz1)) {
					return false;
				}
			} else {
				return false;
			}
			q.addOr(zz, zz1);
			return true;
		}
		if (kind == BinaryOperator.L_AND) {
			if (binding instanceof ICanWriteToQuery) {
				ICanWriteToQuery m = (ICanWriteToQuery) binding;
				if (!m.modify(q)) {
					return false;
				}
			} else {
				return false;
			}
			if (binding1 instanceof ICanWriteToQuery) {
				ICanWriteToQuery m = (ICanWriteToQuery) binding1;
				if (!m.modify(q)) {
					return false;
				}
			} else {
				return false;
			}
			return true;
		}
		String kind = getFilterKind();
		if (binding1 instanceof ConstantExpression) {
			IListenableExpression<?> z = binding;
			binding = binding1;
			binding1 = z;
			swapKind();
		}
		if (binding instanceof ConstantExpression) {

			IListenableExpression<?> z = binding1;
			if (kind == null) {
				return false;
			}
			String propId = "";
			if (z instanceof GetPropertyExpression) {
				GetPropertyExpression m = (GetPropertyExpression) z;
				propId = m.getPropertyId();
				if (!(m.getParent() instanceof VariableExpression)) {
					return false;
				}
			} else {
				return false;
			}
			ConstantExpression c = (ConstantExpression) binding;
			QueryFilter fl = new QueryFilter(propId, c.getValue(), kind);
			q.addFilter(fl);
			return true;
		}
		// FIXME this code should be written in much more elegant way...
		return false;
	}

	private void swapKind() {
		switch (kind) {
		case BinaryOperator.LEQ:
			kind = BinaryOperator.GEQ;
		case BinaryOperator.GEQ:
			kind = BinaryOperator.LEQ;
		case BinaryOperator.LOWER:
			kind = BinaryOperator.GREATER;
		case BinaryOperator.GREATER:
			kind = BinaryOperator.LOWER;

		default:
			break;
		}
	}

	private String getFilterKind() {
		switch (kind) {
		case BinaryOperator.LEQ:
			return QueryFilter.FILTER_LE;
		case BinaryOperator.GEQ:
			return QueryFilter.FILTER_GE;
		case BinaryOperator.LOWER:
			return QueryFilter.FILTER_LT;
		case BinaryOperator.GREATER:
			return QueryFilter.FILTER_GT;
		case BinaryOperator.EQ:
			return QueryFilter.FILTER_EQUALS;
		case BinaryOperator.NEQ:
			return QueryFilter.FILTER_NOT_EQUALS;
		default:
			break;
		}
		return null;
	}

	@Override
	public IMeta getMeta() {
		if (kind==BinaryOperator.FILTER_BY||kind==BinaryOperator.ORDER_BY){
			if (binding instanceof IEditableExpression){
				IEditableExpression<?>r=(IEditableExpression<?>) binding;
				return r.getMeta();
			}
		}	
		return null;
	}

	@Override
	public void setValue(Object value) {
		if (kind==BinaryOperator.FILTER_BY||kind==BinaryOperator.ORDER_BY){
			if (binding instanceof IEditableExpression){
				IEditableExpression<?>r=(IEditableExpression<?>) binding;
				r.setValue(value);
			}
		}
	}

	@Override
	public boolean isReadOnly() {	
		if (kind==BinaryOperator.FILTER_BY||kind==BinaryOperator.ORDER_BY){
			if (binding instanceof IEditableExpression){
				IEditableExpression<?>r=(IEditableExpression<?>) binding;
				return r.isReadOnly();
			}
		}		
		return true;
	}

	public int getKind() {
		return kind;
	}
	
	public IListenableExpression<?> getOperand1() {
		return binding;
	}
	
	public IListenableExpression<?> getOperand2() {
		return binding1;
	}

}
