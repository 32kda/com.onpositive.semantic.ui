package com.onpositive.semantic.model.expressions.impl;

import java.util.Collection;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.ISubsitutableExpression;


public class HasValue extends AbstractListenableExpression<Boolean> implements
		IValueListener<Object> ,ISubsitutableExpression<Boolean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final IListenableExpression<?> binding;

	public HasValue(IListenableExpression<?> bs) {
		this.binding = bs;
		if (binding==null){
			return;
		}
		this.binding.addValueListener(this);
		this.valueChanged(null, bs.getValue());
	}

	public void dispose() {
		this.binding.removeValueListener(this);
		binding.disposeExpression();
		super.dispose();
	}

	public void valueChanged(Object oldValue, Object newValue) {
		if (newValue == null) {
			this.setNewValue(false);
			return;
		} else if (newValue instanceof Collection) {
			this.setNewValue(!((Collection<?>) newValue).isEmpty());
		} else {
			this.setNewValue(newValue.toString().length()>0);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISubsitutableExpression<Boolean> substituteAllExcept(
			IListenableExpression<?> ve) {
		if (binding instanceof ISubsitutableExpression){
			ISubsitutableExpression<Object>o=(ISubsitutableExpression<Object>) binding;
			ISubsitutableExpression<Object> substituteAllExcept = o.substituteAllExcept(ve);
			if (substituteAllExcept!=null){
				HasValue hasValue = new HasValue(substituteAllExcept);
				if (isConstant()){
					return (ISubsitutableExpression)new ConstantExpression(hasValue.getValue());
				}
				return hasValue;
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
}
