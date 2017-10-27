package com.onpositive.semantic.model.api.property.java.annotations.meta;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.IExpandableFunction;
import com.onpositive.semantic.model.api.factory.AbstractFactory;
import com.onpositive.semantic.model.api.factory.IFactoryProvider;
import com.onpositive.semantic.model.api.property.IFunction;

public class ExpressionBasedFactoryProvider implements
		IFactoryProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String expression;
	private String caption;

	public ExpressionBasedFactoryProvider(String value, String caption) {
		this.expression=value;
		this.caption=caption;
	}

	@Override
	public IFunction getElementFactory(final Object model) {
		return new AbstractFactory(caption,caption) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object getValue(Object context) {
				Object calculate = ExpressionAccess.calculate(expression, model);
				if (calculate instanceof IExpandableFunction){
					if (calculate instanceof IExpandableFunction){
						IExpandableFunction v=(IExpandableFunction) calculate;
						calculate=v.getValue(context);
					}
				}
				return calculate;
			}
		};
	}

}
