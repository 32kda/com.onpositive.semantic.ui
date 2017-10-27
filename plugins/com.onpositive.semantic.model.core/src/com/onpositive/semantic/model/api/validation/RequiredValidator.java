package com.onpositive.semantic.model.api.validation;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class RequiredValidator implements IValidator<Object>,IMultiValueAwareValidator {


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequiredValidator other = (RequiredValidator) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

	protected final String expression;
	protected final String message;

	public RequiredValidator(String expression, String message) {
		super();
		this.expression = expression;
		this.message = message;
	}

	
	@Override
	public CodeAndMessage isValid(IValidationContext context, Object value) {
		if (!ValueUtils.hasValue(value)) {
			if (expression != null && expression.length() != 0) {				
				Object calculate = ExpressionAccess.calculate(expression,
						context, context.getObject(), value);
				boolean boolean1 = ValueUtils.toBoolean(calculate);
				if (boolean1) {
					return getError(context, value);
				}
				return CodeAndMessage.OK_MESSAGE;
			}
			return getError(context, value);
		}
		return CodeAndMessage.OK_MESSAGE;
	}

	private CodeAndMessage getError(IValidationContext context, Object value) {
		String message=this.message;
		if (message==null||message.length()==0){
			message=DefaultMetaKeys.getCaption(context)+" is required";
			return CodeAndMessage.errorMessage(message);
		}
		return CodeAndMessage.errorMessage(ExpressionAccess
				.calculateAsString(message, context,
						context.getObject(), value));
	}


	@Override
	public CodeAndMessage isValid(IValidationContext context,
			Iterable<Object> object) {
		return isValid(context, (Object)object);
	}
}
