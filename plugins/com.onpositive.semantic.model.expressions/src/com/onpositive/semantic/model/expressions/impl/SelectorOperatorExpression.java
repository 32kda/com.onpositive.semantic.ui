package com.onpositive.semantic.model.expressions.impl;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.BasicLookup;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.GetPropertyExpression;
import com.onpositive.semantic.model.api.expressions.IEditableExpression;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.ISubsitutableExpression;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.api.property.IProperty;

public class SelectorOperatorExpression extends
		AbstractListenableExpression<Object> implements IValueListener<Object>,
		IEditableExpression<Object>, ISubsitutableExpression<Object> {
	public final static String SELECTOR_OPERATOR_STRING_LABEL = "->";

	IListenableExpression<?> classExpression;
	IListenableExpression<?> memberExpression;
	Object classExpressionValue;
	IExpressionEnvironment parentBinding;
	IEditableExpression<?> childBinding;
	String memberName;

	private static BaseMeta defaultReadOnlyMeta = new BaseMeta();
	{
		defaultReadOnlyMeta.putMeta(DefaultMetaKeys.READ_ONLY_KEY, true);
	}

	public void valueChanged(Object oldValue, Object newValue) {
		Object calculateValue = this.calculateValue();
		// System.out.println("Recalc:"+this.hashCode()+" "+calculateValue);
		this.setNewValue(calculateValue);
	}

	private Object calculateValue() {
		calculateChildBinding();
		return childBinding != null ? childBinding.getValue() : null;
	}

	public void setValue(Object value) {
		calculateChildBinding();
		if (childBinding != null)
			childBinding.setValue(value);
	}

	private void calculateChildBinding() {
		IProperty f = null;
		if (memberExpression != null) {
			Object memberExpressionValue = memberExpression.getValue();
			if (memberExpressionValue instanceof String)
				memberName = (String) memberExpressionValue;
			else {
				memberName = null;
				if (!(memberExpressionValue instanceof IProperty)) {

					return;
				}
				f = (IProperty) memberExpressionValue;
			}
		}

		Object newClassExpressionValue = classExpression.getValue();
		// System.out.println("A:"+newClassExpressionValue);
		if (f == null
				&& (newClassExpressionValue != classExpressionValue || true)) {
			classExpressionValue = newClassExpressionValue;
			parentBinding = new BasicLookup(null, classExpressionValue) {
				protected void changed() {
					fireChanged();
				};
			};
		}
		if (f != null) {
			if (childBinding != null) {
				childBinding.disposeExpression();
			}
			childBinding = new GetPropertyExpression(f, classExpression);
		}
		if (memberName != null) {
			childBinding = (IEditableExpression<?>) ((parentBinding == null || memberName == null) ? null
					: parentBinding.getBinding(memberName));
		}

	}

	public SelectorOperatorExpression(IListenableExpression<?> classExpression,
			IListenableExpression<?> memberExpression) {
		super();
		this.classExpression = classExpression;
		this.memberExpression = memberExpression;
		this.classExpression.addValueListener(this);
		this.memberExpression.addValueListener(this);
		this.memberName = null;
		this.value = this.calculateValue();
	}

	public SelectorOperatorExpression(IListenableExpression<?> classExpression,
			String memberName) {
		super();
		this.classExpression = classExpression;
		this.memberExpression = null;
		this.classExpression.addValueListener(this);
		this.memberName = memberName;
		this.value = this.calculateValue();
	}

	public void dispose() {
		this.classExpression.removeValueListener(this);
		this.classExpression.disposeExpression();

		if (this.memberExpression != null) {
			this.memberExpression.removeValueListener(this);
			this.memberExpression.disposeExpression();
		}
		super.dispose();
	}

	public boolean isReadOnly() {
		if (childBinding != null) {
			boolean isReadOnly = childBinding.isReadOnly();
			// return childBinding.isReadOnly();
			return isReadOnly;
		}
		return true;
	}

	public IListenableExpression<?> getBinding() {
		return childBinding;
	}

	public IMeta getMeta() {
		if (childBinding != null) {
			return childBinding.getMeta();
		}
		return defaultReadOnlyMeta;
	}

	@Override
	public ISubsitutableExpression<Object> substituteAllExcept(
			IListenableExpression<?> ve) {
		if (classExpression instanceof ISubsitutableExpression<?>) {
			if (memberExpression instanceof ISubsitutableExpression<?>) {
				ISubsitutableExpression<?> z = (ISubsitutableExpression<?>) classExpression;
				ISubsitutableExpression<?> z1 = (ISubsitutableExpression<?>) memberExpression;
				ISubsitutableExpression<?> s1 = z.substituteAllExcept(ve);
				ISubsitutableExpression<?> s2 = z1.substituteAllExcept(ve);
				if (s1 != null && s2 != null) {
					SelectorOperatorExpression binaryExpression = new SelectorOperatorExpression(
							s1, s2);
					if (binaryExpression.isConstant()) {
						return new ConstantExpression(
								binaryExpression.getValue());
					}
					if (s2.isConstant()) {
						Object value2 = s2.getValue();
						if (value2 instanceof IProperty) {
							GetPropertyExpression p = new GetPropertyExpression(
									(IProperty) value2, s1);
							return p;
						}
						if (value2 != null) {
							GetPropertyExpression p = new GetPropertyExpression(
									value2.toString(), s1);
							return p;
						}
						return new ConstantExpression(null);
					}
					return binaryExpression;
				}
			}
		}
		return null;
	}

	@Override
	public boolean isConstant() {
		if (classExpression instanceof ISubsitutableExpression<?>) {
			if (memberExpression instanceof ISubsitutableExpression<?>) {
				ISubsitutableExpression<?> z = (ISubsitutableExpression<?>) classExpression;
				ISubsitutableExpression<?> z1 = (ISubsitutableExpression<?>) memberExpression;
				return z.isConstant() && z1.isConstant();
			}
		}
		return false;
	}

}
