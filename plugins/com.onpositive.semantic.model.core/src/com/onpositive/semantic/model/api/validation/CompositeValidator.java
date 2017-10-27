package com.onpositive.semantic.model.api.validation;

import java.util.LinkedHashSet;

import com.onpositive.semantic.model.api.status.CodeAndMessage;

@SuppressWarnings("rawtypes")
public class CompositeValidator extends ValidatorAdapter implements
		IMultiValueAwareValidator {

	private final LinkedHashSet<IValidator> validators = new LinkedHashSet<IValidator>();

	public void addValidator(IValidator validator) {
		this.validators.add(validator);
	}

	public void removeValidator(IValidator validator) {
		this.validators.remove(validator);
	}

	@Override
	@SuppressWarnings("unchecked")
	public CodeAndMessage isValid(IValidationContext context, Object object) {
		CodeAndMessage msg = CodeAndMessage.OK_MESSAGE;
		for (final IValidator e : this.validators) {
			final CodeAndMessage ma = e.isValid(context, object);
			if (ma.getCode() > msg.getCode()) {
				msg = ma;
			}
		}
		return msg;
	}

	public boolean isEmpty() {
		return this.validators.isEmpty();
	}

	@Override
	public CodeAndMessage isValid(IValidationContext context,
			Iterable<Object> object) {
		CodeAndMessage msg = CodeAndMessage.OK_MESSAGE;
		for (final IValidator e : this.validators) {
			if (e instanceof IMultiValueAwareValidator) {
				final CodeAndMessage ma =((IMultiValueAwareValidator) e).isValid(context, object);
				if (ma.getCode() > msg.getCode()) {
					msg = ma;
					if (msg.isError()){
						return msg;
					}
				}
			}
		}
		return msg;
	}
}
