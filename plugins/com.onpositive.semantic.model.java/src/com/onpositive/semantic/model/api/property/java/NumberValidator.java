package com.onpositive.semantic.model.api.property.java;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.NumberFormat;

import com.onpositive.semantic.model.api.labels.ILabelLookup;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.labels.NotFoundException;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.RealmAccess;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.validation.DefaultValidationContext;
import com.onpositive.semantic.model.api.validation.ICanBeStricter;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.ValidatorAdapter;

final class NumberValidator extends ValidatorAdapter<Object> implements
		ILabelLookup,ICanBeStricter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Class<?> clazz;
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((ms == null) ? 0 : ms.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NumberValidator other = (NumberValidator) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (ms == null) {
			if (other.ms != null)
				return false;
		} else if (!ms.equals(other.ms))
			return false;
		return true;
	}

	private Method ms;

	static final String SHOULD_BE_ONE_OF = "{0} should be {1} or {2} ";
	static final String MIN_VIOLATED = "{0} should have value between {1} and {2}";
	static final String MAX_VIOLATED = MIN_VIOLATED;
	static final String SHOULD_BE_NUMBER = "{0} should be number";
	static final String SHOULD_BE_INTEGER_NUMBER = "{0} should be integer number";

	public NumberValidator(Class<? extends Object> class1) {
		this.clazz = class1;
		try {
			ms = class1.getDeclaredMethod("valueOf", String.class);
		} catch (Exception e) {
			try {
				ms=Double.class.getDeclaredMethod("valueOf", String.class);
			} catch (Exception e1) {
				throw new IllegalArgumentException(e);
			}
			//throw new IllegalStateException();
		}
	}

	public CodeAndMessage isValid(IValidationContext context, Object object) {
		if (!ValueUtils.hasValue(object)) {
			if (!DefaultMetaKeys.isRequired(context)) {
				return CodeAndMessage.OK_MESSAGE;
			}
			return errorMessage(context);
		}
		if (clazz == Boolean.class) {
			if (!(object instanceof Boolean)) {
				if (context.getUnconvertedValue() instanceof String) {
					final String label = (String) context.getUnconvertedValue();
					if (label.trim().length() == 0) {
						if (!DefaultMetaKeys.isRequired(context)) {
							return CodeAndMessage.OK_MESSAGE;
						}
					}
					if ((label != null) && label.equals("true")) { //$NON-NLS-1$
						return CodeAndMessage.OK_MESSAGE;
					}
					if ((label != null) && label.equals("false")) { //$NON-NLS-1$
						return CodeAndMessage.OK_MESSAGE;
					}
					return genBoolError(context, label);
				}
				return genBoolError(context, object.toString());
				
			}
			return CodeAndMessage.OK_MESSAGE;
		}
		Number doubleValue = (Number) (object instanceof Number ? object : null);
		if (doubleValue == null) {
			final Object unconvertedValue = context.getUnconvertedValue();
			try {
				if (unconvertedValue != null) {
					doubleValue = (Number) ms.invoke(null,
							unconvertedValue.toString());
				}
				else{
					return CodeAndMessage.OK_MESSAGE;
				}
			} catch (final Exception e) {

				return errorMessage(context);
			}
		}
		Double value = DefaultMetaKeys.getValue(context,
				DefaultMetaKeys.RANGE_MIN__KEY, Double.class, null);
		Double mvalue = DefaultMetaKeys.getValue(context,
				DefaultMetaKeys.RANGE_MAX__KEY, Double.class, null);
		double d = doubleValue!=null?doubleValue.doubleValue():null;
		if (value!=null&&d < value) {
			return CodeAndMessage.errorMessage(MessageFormat.format(
					MIN_VIOLATED,
					DefaultMetaKeys.getCaption(context), value, mvalue));
		}
		if (mvalue!=null&&d > mvalue) {
			return CodeAndMessage.errorMessage(MessageFormat.format(
					MAX_VIOLATED,
					DefaultMetaKeys.getCaption(context), value, mvalue));

		}
		return super.isValid(context, object);
	}

	protected CodeAndMessage genBoolError(IValidationContext context,
			final String label) {
		final ITextLabelProvider adapter = DefaultMetaKeys.getService(context,
				ITextLabelProvider.class);

		final String text = adapter.getText(context, context.getObject(),
				Boolean.TRUE);
		if (text.equalsIgnoreCase(label)) {
			return CodeAndMessage.OK_MESSAGE;
		}
		final String text1 = adapter.getText(context, context.getObject(),
				Boolean.FALSE);
		if (text1.equalsIgnoreCase(label)) {
			return CodeAndMessage.OK_MESSAGE;
		}
		return CodeAndMessage
				.errorMessage(MessageFormat.format(SHOULD_BE_ONE_OF,
						DefaultMetaKeys.getCaption(context), text, text1));
	}

	protected CodeAndMessage errorMessage(IValidationContext context) {
		if (clazz == Boolean.class) {
			return genBoolError(context, "''");
		}
		if (clazz == Character.class) {
			return CodeAndMessage.errorMessage("Character is expected");
		}
		if ((clazz == Double.class) || (clazz == Float.class)) {
			return CodeAndMessage.errorMessage(MessageFormat.format(
					SHOULD_BE_NUMBER,
					context.getValue()));
		}
		return CodeAndMessage
				.errorMessage(MessageFormat
						.format(SHOULD_BE_INTEGER_NUMBER,
								context.getValue()));
	}

	
	public Object lookUpByLabel(IHasMeta model, Object parentObject,
			String label) throws NotFoundException {
		if (label.length() == 0) {
			return null;
		}
		try {
			Object invoke = ms.invoke(label);
			return invoke;
		} catch (Exception e) {

		}
		if (Number.class.isAssignableFrom(clazz)) {
			try {
				Number parse = NumberFormat.getInstance().parse(label);
				if (clazz == Byte.class) {
					return parse.byteValue();
				}
				if (clazz == Short.class) {
					return parse.shortValue();
				}
				if (clazz == Integer.class) {
					return parse.intValue();
				}
				if (clazz == Long.class) {
					return parse.longValue();
				}
				if (clazz == Float.class) {
					return parse.floatValue();
				}
				if (clazz == Double.class) {
					return parse.doubleValue();
				}
				if (clazz == Number.class && parse != null) {
					return parse;
				}
			} catch (Exception e) {
			}
		}
		if (clazz==Character.class&&label.length()==1){
			return label.charAt(0);
		}
		if (clazz==Boolean.class){
			IRealm<Boolean> realm = RealmAccess.getRealm(Boolean.class);
			for (Object o:realm){
				if (label.equalsIgnoreCase(LabelAccess.getLabel(model,o))){
					return o;
				}
			}
			if (!DefaultMetaKeys.isRequired(model)&&label.equals(LabelAccess.getLabel(model,null))){
				return null;
			}
		}
		throw new NotFoundException(errorMessage(new DefaultValidationContext(label,model)).getMessage());
	}

	
	public boolean isStricter(ICanBeStricter stricter) {
		if (stricter instanceof NumberValidator){
			NumberValidator m=(NumberValidator) stricter;
			if (m.clazz.isAssignableFrom(this.clazz)){
				return true;
			}
		}
		return false;
	}
}