package com.onpositive.commons.xml.language;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import org.w3c.dom.Element;

public class AnnotationProxy<T> implements InvocationHandler {

	HashMap<String, Object> cached = new HashMap<String, Object>();

	private final T instance;

	public T getInstance() {
		return this.instance;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		final String name = method.getName();
		final Object object = this.cached.get(name);
		if (object != null) {
			return object;
		}
		return null;
	}

	void put(String key, Object value) {
		this.cached.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public AnnotationProxy(Class<T> prs, ClassLoader loader) {
		this.instance = (T) Proxy.newProxyInstance(loader, new Class[] { prs },
				this);
	}

	@SuppressWarnings("unchecked")
	public AnnotationProxy(Class<T> prs, ClassLoader loader, Element element)
	{
		this.instance = (T) Proxy.newProxyInstance( loader, new Class[] { prs }, this );
		final Method[] declaredMethods = prs.getDeclaredMethods();
		for (final Method method : declaredMethods)
		{
			final String name = method.getName();
			String value = element.getAttribute(name);
			final Class<?> returnType = method.getReturnType();
			if (value.length()==0&&name.equals("value")){
				if (returnType==Class.class){
					value=element.getAttribute("class");
				}
			}
			
			Object toPut = null;
			if (value.length() == 0) {
				toPut = method.getDefaultValue();
			} else {
				if (returnType == int.class) {
					toPut = Integer.parseInt(value);
				} else if (returnType == double.class) {
					toPut = Double.parseDouble(value);
				} else if (returnType == float.class) {
					toPut = Float.parseFloat(value);
				} else if (returnType == boolean.class) {
					toPut = Boolean.parseBoolean(value);
				} else if (returnType == String.class) {
					toPut = value;
				} else if (returnType == Class.class) {
					try {
						toPut = loader.loadClass(value);
					} catch (final ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
			}
			if (toPut != null) {
				this.put(name, toPut);
			}
		}
	}
}
