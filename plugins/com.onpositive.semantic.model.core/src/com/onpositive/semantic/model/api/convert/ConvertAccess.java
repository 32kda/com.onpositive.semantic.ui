package com.onpositive.semantic.model.api.convert;

//TOFO FIXME
public class ConvertAccess {

	public static<T> T convert(Object object,Class<T>targetClass){
		if (object==null){
			if (targetClass.isPrimitive()){
				
			}
			return null;
		}
		if (targetClass.isInstance(object)){
			return targetClass.cast(object);
		}		
		return null;		
	}
}
