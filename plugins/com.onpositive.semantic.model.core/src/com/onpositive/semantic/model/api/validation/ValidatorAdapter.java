package com.onpositive.semantic.model.api.validation;

import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class ValidatorAdapter<T> implements IValidator<T> {

	@Override
	public CodeAndMessage isValid(IValidationContext context, T value) {
		return CodeAndMessage.OK_MESSAGE;
	}

//	public CodeAndMessage isValid(IValidationContext context,
//			Collection<T> object) {
//		for (final T o : object) {
//			final CodeAndMessage valid = this.isValid(context, o);
//			if (valid != null) {
//				if (valid.getCode() != CodeAndMessage.OK) {
//					return valid;
//				}
//			}
//		}
//		return CodeAndMessage.OK_MESSAGE;
//	}

}
