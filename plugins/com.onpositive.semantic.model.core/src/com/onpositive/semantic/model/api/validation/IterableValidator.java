package com.onpositive.semantic.model.api.validation;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public final class IterableValidator extends ValidatorAdapter<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Object obj=null;
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
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
		IterableValidator other = (IterableValidator) obj;
		if (this.obj == null) {
			if (other.obj != null)
				return false;
		} else if (!this.obj.equals(other.obj))
			return false;
		return true;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CodeAndMessage isValid(IValidationContext context, Object value) {
		CodeAndMessage max = CodeAndMessage.OK_MESSAGE;
		if (DefaultMetaKeys.isMultivalue(context)){
			return CodeAndMessage.OK_MESSAGE;
		}
		if (value instanceof Object[]) {
			return doArray(context, (Object[]) value, max);
		}
		if (value instanceof Map) {
			return doMap(context, (Map) value, max);
			
		}
		return doIterable(context, (Iterable) value, max);
	}

	@SuppressWarnings("rawtypes")
	private CodeAndMessage doMap(IValidationContext context,
			Map<Object, Object> value, CodeAndMessage max) {
		Set<Entry<Object, Object>> entrySet = value.entrySet();
		for (Entry e : entrySet) {
			Object key = e.getKey();
			Object value2 = e.getValue();
			CodeAndMessage validate = ValidationAccess.validate(key);
			if (validate != null) {
				max = max.max(validate);
				if (max.isError()) {
					return max;
				}
			}
			validate = ValidationAccess.validate(value2);
			if (validate != null) {
				max = max.max(validate);
				if (max.isError()) {
					return max;
				}
			}
		}
		return max;
	}

	@SuppressWarnings("rawtypes")
	protected CodeAndMessage doIterable(IValidationContext context,
			Iterable value, CodeAndMessage max) {
		if (value!=null)
		for (final Object o : value) {
			CodeAndMessage validate = ValidationAccess.validate(o);
			if (validate != null) {
				max = max.max(validate);
				if (max.isError()) {
					return max;
				}
			}
		}
		return max;
	}

	protected CodeAndMessage doArray(IValidationContext context,
			Object[] value, CodeAndMessage max) {
		for (final Object o : value) {
			CodeAndMessage validate = ValidationAccess.validate(o);
			if (validate != null) {
				max = max.max(validate);
				if (max.isError()) {
					return max;
				}
			}
		}
		return max;
	}
}