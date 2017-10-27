package com.onpositive.semantic.model.binding;

import java.util.Collection;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.ValidatorAdapter;

public class NotEmptyValidator extends ValidatorAdapter<Object> {

	public CodeAndMessage isValid(IValidationContext context, Object object) {
		if (DefaultMetaKeys.isRequired(context)) {
			if ((context.getObject() == null)
					&& !DefaultMetaKeys.isStatic(context)) {
				return super.isValid(context, object);
			}
			if (object == null) {

				return CodeAndMessage.errorMessage(getMessage(context),
						DefaultMetaKeys.getCaption(context));
			}
			if (object instanceof String) {
				if (object.toString().trim().length() == 0) {

					return CodeAndMessage.errorMessage(getMessage(context),
							DefaultMetaKeys.getCaption(context));
				}
			}
			if (object instanceof Collection<?>) {
				if (((Collection<?>) object).isEmpty()) {

					return CodeAndMessage.errorMessage(getMessage(context),
							DefaultMetaKeys.getCaption(context));
				}
			}
		}
		return super.isValid(context, object);
	}

	private String getMessage(IValidationContext context) {
		String metaDescription = DefaultMetaKeys.getMetaDescription(context,
				DefaultMetaKeys.REQUIRED_KEY);
		if (metaDescription != null) {
			return metaDescription;
		}
		return AbstractBinding.FIELD_IS_REQUIRED;
	}

}