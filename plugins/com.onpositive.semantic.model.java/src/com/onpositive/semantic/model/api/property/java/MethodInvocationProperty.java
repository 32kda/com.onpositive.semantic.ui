package com.onpositive.semantic.model.api.property.java;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.expressions.IExpandableFunction;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.factory.IFactoryProvider;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.property.IFunction;

public class MethodInvocationProperty extends AbstractReflectionProperty {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private final class MethodFactory  implements IExpandableFunction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Object obj;

		private MethodFactory(Object obj) {
			this.obj = obj;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.getOuterType().hashCode();
			result = prime * result + ((this.obj == null) ? 0 : this.obj.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			final MethodFactory other = (MethodFactory) obj;
			if (!this.getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (this.obj == null) {
				if (other.obj != null) {
					return false;
				}
			} else if (!this.obj.equals(other.obj)) {
				return false;
			}
			return true;
		}



		public Object getValue(Object context) {
			try {
				final Class<?>[] parameterTypes = MethodInvocationProperty.this.method.getParameterTypes();
				if (parameterTypes.length == 1) {
					return MethodInvocationProperty.this.method.invoke(this.obj, context);
				}
				if (parameterTypes.length == 0) {
					return MethodInvocationProperty.this.method.invoke(this.obj);
				}
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			throw new IllegalStateException(
					"Bad method " + MethodInvocationProperty.this.method + " used as a binding for event property"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private AbstractReflectionProperty getOuterType() {
			return MethodInvocationProperty.this;
		}

		
		public IMeta getMeta() {
			return MethodInvocationProperty.this.getMeta();
		}
	}

	private final Method method;

	public MethodInvocationProperty(Method method2) {
		super(method2.getName());
		this.method = method2;
		initElement(method2);		
		try{
		this.method.setAccessible(true);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	public ICommandExecutor getCommandExecutor() {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<Object> initSubjectClass() {
		return (Class) IFunction.class;
	}

	public Object getValue(final Object obj) {
		final IFunction factory = new MethodFactory(obj);
		return factory;
	}

	public boolean isReadOnly() {
		return false;
	}

	protected void doSet(Object target, Object object)
			throws IllegalAccessException {

	}

	
	protected ClassLoader getClassLoader() {
		return method.getDeclaringClass().getClassLoader();
	}


	
	protected int modifiers() {
		return method.getModifiers();
	}

	
	protected Type getGenericType() {
		return method.getGenericReturnType();
	}


	@Override
	public Class<?> getOwnerClass() {
		return method.getDeclaringClass();
	}
}
