package com.onpositive.semantic.model.expressions.impl;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.GetPropertyExpression;
import com.onpositive.semantic.model.api.expressions.ICanWriteToQuery;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.ISubsitutableExpression;
import com.onpositive.semantic.model.api.expressions.VariableExpression;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.expressions.operatorimplementations.UnaryOperator;

@SuppressWarnings("rawtypes")
public class ClosureExpression extends AbstractListenableExpression<Object> implements IValueListener{

	protected VariableExpression ve;
	protected IListenableExpression<Object>cm;
	
	public ClosureExpression(VariableExpression ve,
			IListenableExpression<Object> cm) {
		super();
		this.ve = ve;
		this.cm = cm;
		//ve.addValueListener((IValueListener)this);
		cm.addValueListener((IValueListener)this);
	}
	@Override
	public void dispose() {
		//ve.removeValueListener((IValueListener<?>)this);
		cm.removeValueListener((IValueListener<?>)this);
		super.dispose();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean computing;
	
	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		if (!computing){
		fireChanged();
		}
	}
	
	@Override
	public Object getValue() {
		return this;
	}
	public Object compute(Object element) {
		computing=true;
		try{
		ve.setValue(element);
		}finally{
			computing=false;
		}
		return cm.getValue();
	}
	@SuppressWarnings("unchecked")
	public ClosureExpression normalize() {
		if (cm instanceof ISubsitutableExpression){
			ISubsitutableExpression s=(ISubsitutableExpression) cm;
			ISubsitutableExpression substituteAllExcept = s.substituteAllExcept(ve);
			if (substituteAllExcept!=null){
				return new ClosureExpression(ve, substituteAllExcept);
			}
		}
		return null;
	}
	public boolean writeTo(Query query) {
		if (cm instanceof ICanWriteToQuery){
			ICanWriteToQuery q=(ICanWriteToQuery) cm;
			return q.modify(query);
		}
		return false;
	}
	
	public boolean writeSort(Query query) {
		if (cm instanceof GetPropertyExpression){
			ICanWriteToQuery q=(ICanWriteToQuery) cm;
			GetPropertyExpression m=(GetPropertyExpression) q;
			query.setSorting(m.getPropertyId());
			return true;
		}
		if (cm instanceof UnaryExpression){
			UnaryExpression ee=(UnaryExpression) cm;
			int kind = ee.getKind();
			if (kind==UnaryOperator.UMINUS){
				IListenableExpression<?> binding = ee.getBinding();
				if (binding instanceof GetPropertyExpression){
					GetPropertyExpression pm=(GetPropertyExpression) binding;
					query.setSorting(pm.getPropertyId());
					query.setAscendingSort(true);
				}
			}			
			return false;
		}
		return false;
	}

}
