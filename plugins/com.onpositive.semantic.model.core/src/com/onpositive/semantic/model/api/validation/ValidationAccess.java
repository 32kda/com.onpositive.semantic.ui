package com.onpositive.semantic.model.api.validation;

import java.util.IdentityHashMap;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public final class ValidationAccess {

	private ValidationAccess() {
	}

	static ThreadLocal<IdentityHashMap<Object, Object>> validation = new ThreadLocal<IdentityHashMap<Object, Object>>();

	public static CodeAndMessage validateSubjectClass(Object value,
			IHasMeta meta) {
		Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(meta);
		boolean multivalue = DefaultMetaKeys.isMultivalue(meta);
		if (multivalue) {
			Iterable<Object> collection = ValueUtils.toCollection(value);
			for (Object o : collection) {
				if (o != null && !subjectClass.isInstance(o)) {
					return CodeAndMessage.errorMessage(o
							+ " should be instance of " + subjectClass);
				}
			}
			return CodeAndMessage.OK_MESSAGE;
		}
		if (value != null && !subjectClass.isInstance(value)) {
			// if (Collection.class.isAssignableFrom(subjectClass)){
			// return CodeAndMessage.OK_MESSAGE;//TODO REVIEW IT
			// }
			return CodeAndMessage.errorMessage(value
					+ " should be instance of " + subjectClass);
		}
		return CodeAndMessage.OK_MESSAGE;
	}

	public static CodeAndMessage validate(IValidationContext ct) {
		IValidator<Object> value = DefaultMetaKeys.getService(ct,
				IValidator.class);

		CodeAndMessage valid = CodeAndMessage.OK_MESSAGE;
		if (value != null) {
			Object value3 = ct.getValue();
			Object value2 = value3;
			if (DefaultMetaKeys.isMultivalue(ct)) {
				Iterable<Object> collection = ValueUtils.toCollection(value2);

				if (collection != null) {
					if (value instanceof IMultiValueAwareValidator) {
						valid = ((IMultiValueAwareValidator) value).isValid(ct,
								collection);
						if (valid.isError()) {
							return valid;
						}
					}

					Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(ct);
					for (Object o : collection) {
						if (o != null && !subjectClass.isInstance(o)) {
							return CodeAndMessage.errorMessage(o
									+ " should be instance of " + subjectClass);
						}
						valid = value.isValid(ct, o);
						if (valid != null) {
							if (!valid.isError()) {
								// valid=validate(ct)
								IHasMeta meta = MetaAccess.getMeta(o);
								DefaultValidationContext ct2 = new DefaultValidationContext(o,
										ct.getObject(), ct);
								//if (!){
								valid = validateNested(
										ct2, valid,!DefaultMetaKeys.getValue(meta, IValidationContext.NEVER_VALIDATE));
								//}
								if (valid.isError()) {
									return valid;
								}
							} else {
								return valid;
							}
						}
					}
				}
			} else {
				Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(ct);
				if (value3 != null && !subjectClass.isInstance(value3)) {
					if (!(value3 instanceof IFunction)) {
						return CodeAndMessage.errorMessage(value3
								+ " should be instance of " + subjectClass);
					} else {
						subjectClass = IFunction.class;
					}
				}
				valid = value.isValid(ct, value2);
				if (valid != null) {
					if (!valid.isError()) {
						valid = validateNested(ct, valid,false);
					}
					return valid;
				}

			}
		} else {
			valid = validateNested(ct, valid,false);
		}
		return valid;
	}

	protected static CodeAndMessage validateNested(IValidationContext ct,
			CodeAndMessage valid,boolean ignoreNeverValidate) {
		if (!ignoreNeverValidate&&DefaultMetaKeys.getValue(ct,
				IValidationContext.NEVER_VALIDATE)) {
			return CodeAndMessage.OK_MESSAGE;
		}
		IdentityHashMap<Object, Object> map = getMap();
		Object object = ct.getObject();
		if (map.containsKey(object)) {
			return valid;
		}
		boolean b = ct.getParent() != null;
		try {

			if (b) {
				map.put(object, object);
			}
			for (IValidationContext m : ct.getNestedContexts()) {
				CodeAndMessage validate = validate(m);
				valid = valid.max(validate);
				if (valid.isError()) {
					return valid;
				}
			}
			return valid;
		} finally {
			if (b) {
				map.remove(object);
			}
		}

	}

	private static IdentityHashMap<Object, Object> getMap() {
		IdentityHashMap<Object, Object> identityHashMap = validation.get();
		if (identityHashMap == null) {
			identityHashMap = new IdentityHashMap<Object, Object>();
			validation.set(identityHashMap);
		}
		return identityHashMap;

	}

	public static CodeAndMessage validate(Object object) {
		IValidationContext validateContext = getValidateContext(object);
		if (DefaultMetaKeys.getValue(validateContext,
				IValidationContext.NEVER_VALIDATE)) {
			return CodeAndMessage.OK_MESSAGE;
		}
		return validate(validateContext);
	}

	public static IValidationContext getValidateContext(Object object) {
		if (object instanceof IValidationContext) {
			IValidationContext ctx = (IValidationContext) object;
			return ctx;
		}
		if (object instanceof IHasValidationContext) {
			IHasValidationContext ctx = (IHasValidationContext) object;
			return ctx.getValidationContext();
		}
		IHasMeta meta = MetaAccess.getMeta(object);
		if (meta != null) {
			IValidationContextProvider service = meta.getMeta().getService(
					IValidationContextProvider.class);
			if (service != null) {
				return service.getValidationContext(object);
			}
		}
		return new DefaultValidationContext(object);
	}
}
