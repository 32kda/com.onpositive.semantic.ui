package com.onpositive.datamodel.model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.onpositive.datamodel.core.IEntry;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.property.ICommandFactory;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.realm.IType;

public class ProxyProvider {

	public static IEntry getEntry(Object o) {
		if (o instanceof Proxy) {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(o);
			if (invocationHandler instanceof EntryHandler) {
				EntryHandler h = (EntryHandler) invocationHandler;
				return h.entry;
			}
		}
		return null;
	}

	static ThreadLocal<ExecutableCommand> currentCommand = new ThreadLocal<ExecutableCommand>();

	public static ExecutableCommand getCurrent() {
		return currentCommand.get();
	}

	public static void startTransaction(ExecutableCommand command) {
		// if (currentCommand.get() != null) {
		// throw new IllegalStateException();
		// }
		currentCommand.set(command);
	}

	public static void commitTransaction() {
		currentCommand.get().execute();
		currentCommand.set(null);
	}

	public static void cancelTransaction() {
		currentCommand.set(null);
	}

	private static final class EntryHandler implements InvocationHandler {
		private final IEntry entry;

		private EntryHandler(IEntry entry) {
			this.entry = entry;
		}

		public IEntry getEntry() {
			return entry;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			Object internalInvoke = internalInvoke(method, args);
			if (internalInvoke instanceof IEntry) {
				Class<?> returnType = method.getReturnType();
				internalInvoke = ProxyProvider.createProxy(returnType,
						(IEntry) internalInvoke);
			}
			if (internalInvoke instanceof Number) {
				Class<?> returnType = method.getReturnType();
				Number nm = (Number) internalInvoke;
				if (returnType == double.class) {
					internalInvoke = nm.doubleValue();
				} else if (returnType == float.class) {
					internalInvoke = nm.floatValue();
				} else if (returnType == long.class) {
					internalInvoke = nm.longValue();
				} else if (returnType == short.class) {
					internalInvoke = nm.shortValue();
				} else if (returnType == byte.class) {
					internalInvoke = nm.byteValue();
				}
			}
			if (internalInvoke instanceof Set) {
				Set<Object> ob = (Set<Object>) internalInvoke;
				Type genericReturnType = method.getGenericReturnType();
				Class<?> conv = null;

				if (genericReturnType instanceof ParameterizedType) {
					ParameterizedType tp = (ParameterizedType) genericReturnType;
					Type[] actualTypeArguments = tp.getActualTypeArguments();
					conv = (Class<?>) actualTypeArguments[0];
				}
				if (conv != null) {
					HashSet<Object> converted = new HashSet<Object>();
					for (Object o : ob) {
						if (o instanceof IEntry) {
							converted.add(ProxyProvider.createProxy(conv,
									(IEntry) o));
						} else {
							converted.add(o);
						}
					}
					internalInvoke = converted;
				}
			}
			return internalInvoke;
		}

		private Object internalInvoke(Method method, Object[] args)
				throws NoSuchMethodException {
			String name = method.getName();

			if (name.equals("hashCode")) {
				return entry.hashCode();
			}
			if (name.equals("equals")) {
				InvocationHandler invocationHandler = Proxy
						.getInvocationHandler(args[0]);
				if (invocationHandler instanceof EntryHandler) {
					EntryHandler hm = (EntryHandler) invocationHandler;
					return hm.entry.equals(entry);
				}
				return false;
			}
			if (name.startsWith("get")) {
				name = Character.toLowerCase(name.charAt(3))
						+ name.substring(4);
			} else if (name.startsWith("set")) {
				name = Character.toLowerCase(name.charAt(3))
						+ name.substring(4);
				IProperty property = entry
						.getPropertyProvider().getProperty(entry, name);
				if (property == null) {
					throw new NoSuchMethodException(name);
				}
				ICommandFactory fact=property.getCommandFactory();
				ICommand cmd = null;
				Object object = args[0];
				if (object instanceof Collection) {
					cmd = fact.createSetValuesCommand(property,
							entry, ((Collection<?>) object).toArray());
				} else {
					if (object instanceof Object[]) {
						cmd = fact.createSetValuesCommand(property,
								entry, (Object[]) object);
					}
					cmd = fact.createSetValueCommand(property, entry, object);
				}
				ExecutableCommand executableCommand = currentCommand.get();
				if (executableCommand != null) {
					executableCommand.addCommand(cmd);
				} else {
					property.getCommandExecutor().execute(cmd);
				}
			}
			IProperty property = entry
					.getPropertyProvider().getProperty(entry, name);
			if (property == null) {
				property = entry.getPropertyProvider().getProperty(entry,
						name.replace("_", "::"));
				if (property == null) {
					throw new NoSuchMethodException(method.getName());
				}
			}
			if (Set.class == method.getReturnType()) {
				try {
					return property.getValues(entry);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return property.getValue(entry);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<T> clazz, final IEntry entry) {
		if (entry != null) {
			InvocationHandler h = new EntryHandler(entry);
			return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
					new Class[] { clazz }, h);
		}
		return null;
	}

	public static boolean isInstance(String typeName, IEntry entry) {
		if (entry != null) {
			Set<IType> types = entry.getTypes();
			for (IType t : types) {
				String name = t.getId();
				if (name.equals(typeName)) {
					return true;
				}
			}
		}
		return false;
	}

}
