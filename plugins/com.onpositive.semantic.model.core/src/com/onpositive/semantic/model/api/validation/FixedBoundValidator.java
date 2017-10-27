package com.onpositive.semantic.model.api.validation;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.LyfecycleUtils;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.RealmAccess;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class FixedBoundValidator extends ValidatorAdapter<Object> {

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		FixedBoundValidator other = (FixedBoundValidator) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

	protected String message="{this} is not allowed value";

	public FixedBoundValidator(String message) {
		super();
		if (message.length()>0){
			this.message = message;
		}
	}

	
	@Override
	public CodeAndMessage isValid(IValidationContext context, Object value) {
		IRealm<Object> realm = RealmAccess.getRealm(context,
				context.getObject(), value);
		try{
		if (ValueUtils.hasValue(value)&&(realm==null||!realm.contains(value))) {			
			return CodeAndMessage.errorMessage(ExpressionAccess
					.calculateAsString(message, context, context.getObject(),
							value));
		}
		}finally{
			LyfecycleUtils.disposeIfShortLyfecycle(realm);
		}
		return CodeAndMessage.OK_MESSAGE;
	}
}
