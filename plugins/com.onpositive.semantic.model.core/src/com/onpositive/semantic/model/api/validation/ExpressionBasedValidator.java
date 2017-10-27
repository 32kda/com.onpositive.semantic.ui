package com.onpositive.semantic.model.api.validation;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class ExpressionBasedValidator extends ValidatorAdapter<Object> {

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		ExpressionBasedValidator other = (ExpressionBasedValidator) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	String path;
	String message;

	public ExpressionBasedValidator(String path, String message) {
		super();
		this.path = path;
		this.message = message;
	}

	
	@Override
	public synchronized CodeAndMessage isValid(IValidationContext context,
			Object object) {
		Object value = ExpressionAccess.calculate(path, context,
				context.getObject(), object);
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (!b) {
				return CodeAndMessage.errorMessage(ExpressionAccess
						.calculateAsString(message, context,
								context.getObject(), object));
			}
			return CodeAndMessage.OK_MESSAGE;
		}
		return super.isValid(context, object);
	}
}