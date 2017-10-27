package com.onpositive.commons.xml.language;

public class EnumConverter extends HandlingTypeConvertor.AbstractUnitConvertor{
	
	@SuppressWarnings("rawtypes")
	private Class targetClass;

	@SuppressWarnings("rawtypes")
	public EnumConverter(Class<? extends Enum> targetClass) {
		this.targetClass = targetClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object convertToTargetClass(Object obj)
			throws IllegalArgumentException {
		String name = (String) obj;
		try {
			return Enum.valueOf(targetClass,name);
		} catch (IllegalArgumentException e) {
			
		}
		try {
			return Enum.valueOf(targetClass,name.toLowerCase());
		} catch (IllegalArgumentException e) {
			
		}
		return Enum.valueOf(targetClass,name.toUpperCase());
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected String convertToString(Object obj) {
		if (obj instanceof Enum)
			return ((Enum) obj).name();
		throw new IllegalArgumentException("Argument must be an enum!");
	}

}
