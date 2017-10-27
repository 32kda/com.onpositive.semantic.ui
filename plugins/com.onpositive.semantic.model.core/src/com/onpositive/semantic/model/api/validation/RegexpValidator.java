package com.onpositive.semantic.model.api.validation;

import java.util.regex.Pattern;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class RegexpValidator extends ValidatorAdapter<Object> {

	private final Pattern regexp;
	private final String message;

	public RegexpValidator(String message,String regexp) {
		this.regexp = Pattern.compile(regexp);
		this.message = message;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((regexp == null) ? 0 : regexp.hashCode());
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
		RegexpValidator other = (RegexpValidator) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (regexp == null) {
			if (other.regexp != null)
				return false;
		} else if (!regexp.equals(other.regexp))
			return false;
		return true;
	}

	@Override
	public CodeAndMessage isValid(IValidationContext context, Object object) {
		if (object == null) {
			object = ""; //$NON-NLS-1$
		}
		String string = null;
		if (!(object instanceof String)) {
			string = LabelAccess.getLabel(
					context,context.getObject(),object);
		} else {
			string = (String) object;
		}
		final boolean matches = this.regexp.matcher(string).matches();
		if (!matches) {

			return CodeAndMessage.errorMessage(ExpressionAccess
					.calculateAsString(message, context, context.getObject(),
							object));
		}
		return CodeAndMessage.OK_MESSAGE;
	}
}