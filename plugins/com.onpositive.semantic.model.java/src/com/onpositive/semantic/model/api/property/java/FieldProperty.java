package com.onpositive.semantic.model.api.property.java;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider.ClassPropertyInfo;

public class FieldProperty extends AbstractReflectionProperty  {

	private static final long serialVersionUID = 1L;
	protected transient Field fld;

	public Field getField() {
		return this.fld;
	}

	public FieldProperty(ClassPropertyInfo classPropertyInfo, Field fld) {
		super(fld.getName());
		this.fld = fld;
		classPropertyInfo.properties.put(fld.getName(), this);
		this.init(fld);
		if(!Modifier.isStatic(fld.getModifiers())){
			metadata.putMeta(DefaultMetaKeys.PRIMARY_PROPERTY, true);
		}
	}
	@Override
	public Class<?> getOwnerClass() {
		return fld.getDeclaringClass();
	}

	private void init(Field fld) {
		try{
		fld.setAccessible(true);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		this.type = fld.getType();
		this.typeIsCollection = isCollection();
		this.typeisArray = this.type.isArray();
		initElement(fld);		
	}

	public boolean isReadOnly() {
		if (Modifier.isFinal(this.fld.getModifiers())) {
			if (typeIsCollection){
				return false;
			}
			return true;
		}
		return false;
	}

	public Object getValue(Object obj) {
		try {
			final Object object = this.fld.get(obj);
			return object;
		} catch (Exception e) {
			return null;
		}
	}

	

	protected void doSet(Object target, Object toAdd)
			throws IllegalAccessException {
		this.fld.set(target, toAdd);
	}	

	
	protected ClassLoader getClassLoader() {
		return fld.getDeclaringClass().getClassLoader();
	}

	
	protected Type getGenericType() {
		return fld.getGenericType();
	}

	
	protected int modifiers() {
		return fld.getModifiers();
	}


}
