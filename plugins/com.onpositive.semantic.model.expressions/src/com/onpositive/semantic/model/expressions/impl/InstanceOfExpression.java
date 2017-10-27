package com.onpositive.semantic.model.expressions.impl;

import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.ISubsitutableExpression;


public class InstanceOfExpression extends AbstractSingleOpBooleanExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String className;
	protected Class<?> clazz;
	
	public InstanceOfExpression(IListenableExpression<?> parseBinary,
			String string) {
		super(parseBinary);
		this.className=string;
		valueChanged(null, parseBinary.getValue());
	}
	


	@Override
	protected boolean getValue(Object bs1) {
		if (bs1 == null) {
			return false;
		}
		if (clazz==null){
			try{
			clazz=bs1.getClass().getClassLoader().loadClass(className);
			}catch (ClassNotFoundException e) {
				setNewValue(false);
				return false;
			}
		}		
		if (clazz.isInstance(bs1)){
			setNewValue(true);	
			return true;
		}
		else{
			setNewValue(false);
			return false;
		}
	}



	
}
