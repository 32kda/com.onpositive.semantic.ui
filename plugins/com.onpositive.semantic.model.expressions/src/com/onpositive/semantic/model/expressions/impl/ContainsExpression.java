package com.onpositive.semantic.model.expressions.impl;

import java.util.Arrays;
import java.util.Collection;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.ISubsitutableExpression;


public class ContainsExpression extends AbstractListenableExpression<Boolean>
		implements IValueListener<Object> ,ISubsitutableExpression<Boolean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final IListenableExpression<?> owner;
	private final IListenableExpression<?> child;

	public ContainsExpression(IListenableExpression<?> listenableExpression,
			IListenableExpression<?> parseBinary) {
		super();
		this.child = parseBinary;
		this.owner = listenableExpression;
		parseBinary.addValueListener(this);
		listenableExpression.addValueListener(this);
		this.value = this.calculateValue();
	}

	
	public void dispose() {		
		child.disposeExpression();
		owner.disposeExpression();
		super.dispose();
	}

	protected Object calculateValue() {
		final Object bs1 = this.owner.getValue();
		if (bs1 == null) {
			return false;
		}
		final Object bs = this.child.getValue();
		if (bs1 instanceof Collection) {
			final Collection<?> b = (Collection<?>) bs1;
			return b.contains(bs);
		}
		if (bs1 instanceof Object[]) {
			final Object[] ba = (Object[]) bs1;
			return Arrays.asList(ba).contains(bs);
		}
		return (bs1 == bs) || bs1.equals(bs);
	}

	public void valueChanged(Object oldValue, Object newValue) {
		this.setNewValue(this.calculateValue());
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ISubsitutableExpression<Boolean> substituteAllExcept(
			IListenableExpression<?> ve) {
		if (child instanceof ISubsitutableExpression<?>){
			if (owner instanceof ISubsitutableExpression<?>){
				ISubsitutableExpression<?>z=(ISubsitutableExpression<?>) child;
				ISubsitutableExpression<?>z1=(ISubsitutableExpression<?>) owner;
				ISubsitutableExpression<?> s1 = z.substituteAllExcept(ve);
				ISubsitutableExpression<?> s2 = z1.substituteAllExcept(ve);
				if (s1!=null&&s2!=null){
					ContainsExpression containsExpression = new ContainsExpression(owner, child);
					if (containsExpression.isConstant()){
						return (ISubsitutableExpression)new ConstantExpression(containsExpression.getValue());
					}
					return containsExpression;
				}
			}
		}
		return null;
	}


	@Override
	public boolean isConstant() {
		if (child instanceof ISubsitutableExpression<?>){
			if (owner instanceof ISubsitutableExpression<?>){
				ISubsitutableExpression<?>z=(ISubsitutableExpression<?>) child;
				ISubsitutableExpression<?>z1=(ISubsitutableExpression<?>) owner;
				return z.isConstant()&&z1.isConstant();
			}
		}
		return false;
	}
}
