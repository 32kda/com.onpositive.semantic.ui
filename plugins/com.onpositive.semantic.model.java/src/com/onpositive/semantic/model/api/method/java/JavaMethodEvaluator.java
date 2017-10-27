package com.onpositive.semantic.model.api.method.java;

import java.lang.reflect.Method;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.method.IMethodEvaluator;
import com.onpositive.semantic.model.api.property.java.BeanMetaProvider;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaContributor;


public class JavaMethodEvaluator implements IMethodEvaluator {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -5998085201209357911L;
	protected final String methodName;
	protected BaseMeta propertyMeta;

	public JavaMethodEvaluator(Object baseObject, String methodName) {
		this.methodName = methodName;
		initMeta(baseObject);
	}

	protected void initMeta(Object baseObject) { //TODO parameter-polymorphic methods handling
		if (baseObject == null)
			return;
		Class<?> baseClass = (Class<?>) ((baseObject instanceof Class) ? baseObject : baseObject.getClass());
		if (propertyMeta==null){
			propertyMeta = new BaseMeta(
					BeanMetaProvider.INSTANCE.getMeta(baseClass));
		}
		Method[] methods = baseClass.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				MetaContributor.contribute(propertyMeta, methods[i]);
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object evaluateCall(Object baseObject, Object[] params) {
		Class<?>[] paramTypes = new Class[params.length];
		for (int i = 0; i < paramTypes.length; i++) {
			if (params[i] == null)
				paramTypes[i] = Object.class;
			else
				paramTypes[i] = params[i].getClass();
		}
		try {
			Class<? extends Object> baseClass = baseObject.getClass();
			if (baseObject instanceof Class) {
				baseClass = (Class<? extends Object>) baseObject;
			}
			Method method = ReflectionUtil.getCompatibleMethod(
					baseClass, methodName, paramTypes);
			if (method==null){
				return null;
			}
			method.setAccessible(true);
			return method.invoke(baseObject, params);
		} catch (Throwable e) {
			return null;
		}
	}

	@Override
	public IMeta getMeta() {
		if (propertyMeta == null)
			return MetaAccess.getMeta(null).getMeta();
		return propertyMeta;
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
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
		JavaMethodEvaluator other = (JavaMethodEvaluator) obj;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}

}
