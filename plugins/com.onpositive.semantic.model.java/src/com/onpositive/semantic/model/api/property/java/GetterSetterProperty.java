package com.onpositive.semantic.model.api.property.java;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider.ClassPropertyInfo;

public class GetterSetterProperty extends AbstractReflectionProperty {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient final Method getter;
	private transient Method setter;

	private transient Method adder;
	private transient Method remover;

	@SuppressWarnings("rawtypes")
	protected void commit(Object target, Collection c) {
		if (this.commit) {
			try {
				this.doSet(target, c);
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	@Override
	protected boolean isPersistent() {
		return false;
	}

	public GetterSetterProperty(ClassPropertyInfo classPropertyInfo, Method method2, String mname) {
		super(mname);
		this.getter = method2;
		method2.setAccessible(true);
		classPropertyInfo.properties.put(mname, this);
		this.type = method2.getReturnType();
		this.typeIsCollection = isCollection();
		this.typeisArray = this.type.isArray();
		this.initElement(method2);
		
		initMetaInheritance(method2, mname);

		try {
			this.setter = method2.getDeclaringClass().getMethod(
					"set" + mname, this.getter.getReturnType()); //$NON-NLS-1$
			try{
			this.setter.setAccessible(true);
			}catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
			initElement(setter);
			if ((this.setter.getAnnotation(DoNotUseSetter.class) != null)
					) {
				this.commit = false;
			}
			else{
				this.commit=true;
			}
			// if (propertyMeta.getSingleValue(DefaultMetaKeys.READ_ONLY_KEY,
			// Boolean.class, null)){
			metadata.putMeta(DefaultMetaKeys.READ_ONLY_KEY, isReadOnly());
			// }
		} catch (final SecurityException e) {
		} catch (final NoSuchMethodException e) {
			if (!DefaultMetaKeys.isReadonly(metadata)) {
				if (isReadOnly()) {
					metadata.putMeta(DefaultMetaKeys.READ_ONLY_KEY, true);
				}
			}
		}
		final AdderRemover annotation = method2
				.getAnnotation(AdderRemover.class);
		if (annotation != null) {
			final Method[] declaredMethods = method2.getDeclaringClass()
					.getDeclaredMethods();
			final String adder2 = annotation.adder();
			final String remover = annotation.remover();
			for (final Method m : declaredMethods) {
				final String name2 = m.getName();
				if (name2.equals(adder2)) {
					this.adder = m;
				}
				if (name2.equals(remover)) {
					this.remover = m;
				}
			}
			metadata.putMeta(DefaultMetaKeys.USE_ADD_REMOVE__KEY, true);
		}
	
		method2.setAccessible(true);
		
	}

	

	protected void initMetaInheritance(Method method2, String mname) {
		Class<?>[] interfaces = method2.getDeclaringClass().getInterfaces();
		if (interfaces != null) {
			for (Class<?> c : interfaces) {
				ClassPropertyInfo properties = JavaPropertyProvider.instance
						.getProperties(c);
				if (properties != null) {
					IProperty property = properties.getProperty(mname);
					if (property != null) {
						metadata.setDefaultMeta(property.getMeta());
						break;
					}
				}
			}
		}
		Class<?> superclass = method2.getDeclaringClass().getSuperclass();
		if (superclass != null) {
			ClassPropertyInfo properties = JavaPropertyProvider.instance
					.getProperties(superclass);
			if (properties != null) {
				IProperty property = properties.getProperty(mname);
				if (property != null) {
					metadata.setDefaultMeta(property.getMeta());
				}
			}
		}
	}

	public Object getValue(Object obj) {
		try {
			return this.getter.invoke(obj);
		} catch (final Exception e) {
			e.printStackTrace();
			
			return null;
		}
	}

	public boolean isReadOnly() {
		if (this.setter == null) {
			return !typeIsCollection;
		}
		return false;
	}

	protected void add(Object target, Object toAdd, Object oldValue) {
		if (this.adder != null) {
			try {
				this.adder.invoke(target, toAdd);
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		} else {
			super.add(target, toAdd, oldValue);
		}
	}

	protected void remove(Object target, Object toAdd, Object oldValue) {
		if (this.remover != null) {
			try {
				this.remover.invoke(target, toAdd);
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		}
		super.remove(target, toAdd, oldValue);
	}

	@SuppressWarnings("rawtypes")
	protected void doSet(Object target, Object object)
			throws IllegalAccessException {
		try {
			if (this.setter != null) {
				this.setter.invoke(target, object);
			} else if ((this.adder != null) && (this.remover != null)) {
				final Collection value = (Collection) this.getValue(target);
				final Collection ti = (Collection) object;
				for (final Object o : value.toArray()) {
					this.remove(target, o, value);
				}
				for (final Object o : ti) {
					this.add(target, o, value);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ClassLoader getClassLoader() {
		return getter.getDeclaringClass().getClassLoader();
	}

	protected Type getGenericType() {
		return getter.getGenericReturnType();
	}

	protected int modifiers() {
		return getter.getModifiers();
	}

	@Override
	public Class<?> getOwnerClass() {
		return getter.getDeclaringClass();
	}
}
