package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.property.DynamicProperty;
import com.onpositive.semantic.model.api.property.ExpressionValueProperty;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;

public class PropertyFilter extends AbstractFilter implements
		IValueListener<Object> {

	protected IProperty expression;

	public PropertyFilter(IProperty expression) {
		super();
		this.expression = expression;
	}

	public PropertyFilter(String id) {
		super();
		this.expression = new DynamicProperty(id);
	}

	public PropertyFilter(String expression, IClassResolver resolver) {
		super();
		this.expression = new ExpressionValueProperty(expression, resolver);
	}

	public PropertyFilter(String expression, IClassResolver resolver,
			IListenableExpression<?> pContext) {
		super();
		ExpressionValueProperty expressionValueProperty = new ExpressionValueProperty(
				expression, resolver);
		expressionValueProperty.setParentContext(pContext);
		this.expression = expressionValueProperty;
	}

	
	@Override
	public void addValueListener(IValueListener<?> listener) {
		boolean add = !hasListeners();
		super.addValueListener(listener);
		if (add) {
			PropertyAccess.addPropertyStructureListener(expression, this);
		}
	}

	
	@Override
	public void removeValueListener(IValueListener<?> listener) {
		super.removeValueListener(listener);
		if (!hasListeners()) {
			PropertyAccess.removePropertyStructureListener(expression, this);
		}
	}

	
	@Override
	public boolean accept(Object element) {
		Object value = expression.getValue(element);
		return ValueUtils.toBoolean(value);
	}

	
	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		fireChanged();
	}

}
