package com.onpositive.commons.platform.configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.onpositive.commons.platform.registry.ServiceMap;
import com.onpositive.commons.platform.registry.ServiceObject;
import com.onpositive.commons.xml.language.Activator;

@SuppressWarnings( { "unchecked", "unused" })
public class ConfigurationPersistence extends
		ServiceMap<ServiceObject<IConfigurationPersistenceDelegate>> {

	private static ConfigurationPersistence instance;
	private static HashMap<String, Class> classes = new HashMap<String, Class>();

	private ConfigurationPersistence() {
		super(
				"com.onpositive.commons.platform.configurationPersistence", ServiceObject.class); //$NON-NLS-1$
	}

	public static ConfigurationPersistence getInstance() {
		if (instance == null) {
			instance = new ConfigurationPersistence();
		}
		return instance;
	}

	public IConfigurationPersistenceDelegate<Object> getStoreLoad(Class<?> cl) {
		final ServiceObject<IConfigurationPersistenceDelegate> serviceObject = this
				.get(cl);
		if (serviceObject == null) {
			return new DefaultConfigurationPersistenceDelegate();
		}
		return serviceObject.getService();
	}

	static <T> T internalRestore(IAbstractConfiguration config,
			Class<T> toRestore) {
		try {
			if (toRestore == String.class) {
				return (T) config.getStringAttribute("value"); //$NON-NLS-1$			
			} else if (toRestore == Integer.class) {
				return (T) new Integer(config.getIntAttribute("value")); //$NON-NLS-1$			
			} else if (toRestore == Long.class) {
				return (T) new Long(config.getLongAttribute("value")); //$NON-NLS-1$			
			} else if (toRestore == Float.class) {
				return (T) new Float((float) config.getDoubleAttribute("value")); //$NON-NLS-1$			
			} else if (toRestore == Character.class) {
				return (T) new Character(config
						.getStringAttribute("value").charAt(0)); //$NON-NLS-1$			
			} else if (toRestore == Double.class) {
				return (T) new Double(config.getDoubleAttribute("value")); //$NON-NLS-1$			
			} else if (toRestore == Short.class) {
				return (T) new Short((short) config.getIntAttribute("value")); //$NON-NLS-1$			
			} else if (toRestore == Boolean.class) {
				return (T) Boolean.valueOf(config.getBooleanAttribute("value")); //$NON-NLS-1$			
			}
			try {
				final Constructor<T> constructor = toRestore
						.getDeclaredConstructor();
				if (!constructor.isAccessible()) {
					constructor.setAccessible(true);
				}
				final T newInstance = constructor.newInstance();
				loadObject(config, newInstance, toRestore);
				return newInstance;
			} catch (final NoSuchMethodException e) {
				throw new IllegalArgumentException(e);
			}
		} catch (final InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (final IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (final SecurityException e) {
			throw new IllegalArgumentException(e);
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} catch (final InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}
	}

	static void internalStore(IAbstractConfiguration config, Object toStore) {
		try {
			storeObject(config, toStore, toStore.getClass());
		} catch (final IllegalArgumentException e) {
			throw e;
		} catch (final IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static void loadObject(IAbstractConfiguration config, Object toLoad) {
		try {
			loadObject(config, toLoad, toLoad.getClass());
		} catch (final IllegalArgumentException e) {
			throw e;
		} catch (final IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (final InstantiationException e) {
			Activator.log(e);
		}
	}

	private static void loadObject(IAbstractConfiguration config,
			Object toLoad, Class<?> clazz) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException {

		l2: for (final Field f : clazz.getDeclaredFields()) {
			if (Modifier.isTransient(f.getModifiers())) {
				continue;
			}
			if (Modifier.isFinal(f.getModifiers())) {
				continue;
			}
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			final String name = f.getName();
			final Class<?> type = f.getType();
			f.setAccessible(true);
			if (type == boolean.class) {
				f.setBoolean(toLoad, config.getBooleanAttribute(name));
				continue l2;
			} else if (type == int.class) {
				f.setInt(toLoad, config.getIntAttribute(name));
				continue l2;
			} else if (type == long.class) {
				f.setLong(toLoad, config.getLongAttribute(name));
				continue l2;
			} else if (type == double.class) {
				f.setDouble(toLoad, config.getDoubleAttribute(name));
				continue l2;
			} else if (type == char.class) {
				final String stringAttribute = config.getStringAttribute(name);
				if ((stringAttribute != null) && (stringAttribute.length() > 0)) {
					f.setChar(toLoad, stringAttribute.charAt(0));
				}
				continue l2;
			} else if (type == short.class) {
				f.setShort(toLoad, (short) config.getIntAttribute(name));
				continue l2;
			} else if (type == float.class) {
				f.setFloat(toLoad, (float) config.getDoubleAttribute(name));
				continue l2;
			} else if (type == String.class) {
				
				f.set(toLoad, config.getStringAttribute(name));
				continue l2;
			}
			if (Collection.class.isAssignableFrom(type)) {
				final Collection<Object> c = (Collection<Object>) type
						.newInstance();
				final int size = config.getIntAttribute(name);
				final Type tp = f.getGenericType();
				if (tp instanceof ParameterizedType) {
					final ParameterizedType pt = (ParameterizedType) tp;
					final Type[] actualTypeArguments = pt
							.getActualTypeArguments();
					if (actualTypeArguments.length == 1) {
						if (actualTypeArguments[0] instanceof Class) {
							final Class pT = (Class) actualTypeArguments[0];
							if (Number.class.isAssignableFrom(pT)) {
								final int i = 0;
								if (pT == Integer.class) {
									for (int a = 0; a < size; a++) {
										c.add(config.getIntAttribute(name + "." //$NON-NLS-1$
												+ a));
									}
									f.set(toLoad, c);
									continue l2;
								} else if (pT == Number.class) {
									for (int a = 0; a < size; a++) {
										c.add(config.getDoubleAttribute(name
												+ "." //$NON-NLS-1$
												+ a));
									}
									f.set(toLoad, c);
									continue l2;
								} else if (pT == Double.class) {
									for (int a = 0; a < size; a++) {
										c.add(config.getDoubleAttribute(name
												+ "." //$NON-NLS-1$
												+ a));
									}
									f.set(toLoad, c);
									continue l2;
								} else if (pT == Short.class) {
									for (int a = 0; a < size; a++) {
										c.add((short) config
												.getIntAttribute(name + "." //$NON-NLS-1$
														+ a));
									}
									f.set(toLoad, c);
									continue l2;
								} else if (pT == Long.class) {
									for (int a = 0; a < size; a++) {
										c.add(config.getLongAttribute(name
												+ "." //$NON-NLS-1$
												+ a));
									}
									f.set(toLoad, c);
									continue l2;
								} else if (pT == Float.class) {
									for (int a = 0; a < size; a++) {
										c.add((float) config
												.getDoubleAttribute(name + "." //$NON-NLS-1$
														+ a));
									}
									f.set(toLoad, c);
									continue l2;
								}
							}
							if (Boolean.class.isAssignableFrom(pT)) {
								for (int a = 0; a < size; a++) {
									c.add(config.getBooleanAttribute(name + "." //$NON-NLS-1$
											+ a));
								}
								f.set(toLoad, c);
								continue l2;
							}
							if (String.class.isAssignableFrom(pT)) {
								for (int a = 0; a < size; a++) {
									c.add(config.getStringAttribute(name + "." //$NON-NLS-1$
											+ a));
								}
								f.set(toLoad, c);
								continue l2;
							}
							loadObjectCollection(config, toLoad, f, name, pT,
									c, size);
							continue l2;
						}
					}
				}
				throw new IllegalArgumentException(MessageFormat.format(
						"Field {0} may not be persisted", f)); //$NON-NLS-1$
			} else {
				final IAbstractConfiguration subConfiguration = config
						.getSubConfiguration(name);
				final String stringAttribute = subConfiguration
						.getStringAttribute("class"); //$NON-NLS-1$
				if ((stringAttribute == null) || stringAttribute.equals("")) { //$NON-NLS-1$
					continue;
				}
				final IConfigurationPersistenceDelegate<Object> storeLoad = loadObject(
						toLoad, f, type, subConfiguration, stringAttribute);
			}
		}
		final Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			loadObject(config, toLoad, superclass);
		}
	}

	private static IConfigurationPersistenceDelegate<Object> loadObject(
			Object toLoad, Field f, Class<?> type,
			IAbstractConfiguration subConfiguration, String stringAttribute)
			throws IllegalAccessException {
		IConfigurationPersistenceDelegate<Object> storeLoad;
		try {
			if (stringAttribute.equals(type.getName())) {
				storeLoad = getInstance().get(type).getService();
			} else {
				storeLoad = getInstance().get(
						getPersistingClass(stringAttribute)).getService();
			}
		} catch (final ClassNotFoundException e) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Field {0} may not be persisted", f)); //$NON-NLS-1$
		}
		if (storeLoad == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Field {0} may not be persisted", f)); //$NON-NLS-1$
		} else {
			final Object obj = storeLoad.load((Class<Object>) type,
					subConfiguration);
			f.set(toLoad, obj);
		}
		return storeLoad;
	}

	private static void loadObjectCollection(IAbstractConfiguration config,
			Object toLoad, Field f, String name, Class<?> type,
			Collection<Object> c, int size) throws IllegalAccessException {

		for (int a = 0; a < size; a++) {
			final IAbstractConfiguration subConfiguration = config
					.getSubConfiguration(name + "." + a); //$NON-NLS-1$
			final String stringAttribute = subConfiguration
					.getStringAttribute("class"); //$NON-NLS-1$
			if ((stringAttribute == null) || stringAttribute.equals("")) { //$NON-NLS-1$
				c.add(null);
				continue;
			}
			IConfigurationPersistenceDelegate<Object> storeLoad;
			try {
				if (stringAttribute.equals(type.getName())) {
					storeLoad = getInstance().get(type).getService();
				} else {
					storeLoad = getInstance().get(
							getPersistingClass(stringAttribute)).getService();
				}
			} catch (final ClassNotFoundException e) {
				throw new IllegalArgumentException(MessageFormat.format(
						"Field {0} may not be persisted", f)); //$NON-NLS-1$
			}
			if (storeLoad == null) {
				throw new IllegalArgumentException(MessageFormat.format(
						"Field {0} may not be persisted", f)); //$NON-NLS-1$
			} else {
				final Object obj = storeLoad.load((Class<Object>) type,
						subConfiguration);
				c.add(obj);
			}

		}
		f.set(toLoad, c);
	}

	public static Class<?> getPersistingClass(String stringAttribute)
			throws ClassNotFoundException {
		Class class1 = classes.get(stringAttribute);
		if (class1 == null) {
			class1 = Class.forName(stringAttribute);
			registerPersistingClass(class1);
		}
		return class1;
	}

	private static <T> void storeObject(IAbstractConfiguration config,
			Object toStore, Class<?> clazz) throws IllegalArgumentException,
			IllegalAccessException {
		if ((clazz == String.class) || (clazz == Float.class)
				|| (clazz == Integer.class) || (clazz == Boolean.class)
				|| (clazz == Long.class) || (clazz == Short.class)
				|| (clazz == Character.class) || (clazz == Double.class)) {
			config.setStringAttribute("value", toStore.toString()); //$NON-NLS-1$
			return;
		}

		final Field[] declaredFields = clazz.getDeclaredFields();
		l2: for (final Field f : declaredFields) {
			
			f.setAccessible(true);
			int modifiers = f.getModifiers();
			if (Modifier.isTransient(modifiers)) {
				continue;
			}
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			final String name = f.getName();
			final Class<?> type = f.getType();
			if (type.isPrimitive()) {
				if (type == boolean.class) {
					config.setBooleanAttribute(name, f.getBoolean(toStore));
				} else if (type == int.class) {
					config.setIntAttribute(name, f.getInt(toStore));
				} else if (type == long.class) {
					config.setLongAttribute(name, f.getLong(toStore));
				} else if (type == double.class) {
					config.setDoubleAttribute(name, f.getDouble(toStore));
				} else if (type == char.class) {
					config.setStringAttribute(name, f.getChar(toStore) + ""); //$NON-NLS-1$
				} else if (type == short.class) {
					config.setIntAttribute(name, f.getShort(toStore));
				} else if (type == float.class) {
					config.setDoubleAttribute(name, f.getFloat(toStore));
				}
			} else {
				Object obj = f.get(toStore);
				if (type == String.class) {
					if (obj == null) {
						obj = "";
					}
					config.setStringAttribute(name, (String) obj);
					continue l2;
				}
				if (Collection.class.isAssignableFrom(type)) {
					Collection<Object> c = (Collection<Object>) obj;
					if (c==null){
						c=new ArrayList<Object>();
					}
					final int size = c.size();
					config.setIntAttribute(name, size);
					final Type tp = f.getGenericType();
					if (tp instanceof ParameterizedType) {
						final ParameterizedType pt = (ParameterizedType) tp;
						final Type[] actualTypeArguments = pt
								.getActualTypeArguments();
						if (actualTypeArguments.length == 1) {
							if (actualTypeArguments[0] instanceof Class) {
								final Class pT = (Class) actualTypeArguments[0];
								if (Number.class.isAssignableFrom(pT)) {
									int i = 0;
									for (final Object o : c) {
										config.setStringAttribute(name + "." //$NON-NLS-1$
												+ i++, o.toString());
									}
									continue l2;
								}
								if (Boolean.class.isAssignableFrom(pT)) {
									int i = 0;
									for (final Object o : c) {
										config.setStringAttribute(name + "." //$NON-NLS-1$
												+ i++, o.toString());
									}
									continue l2;
								}
								if (String.class.isAssignableFrom(pT)) {
									int i = 0;
									for (final Object o : c) {
										config.setStringAttribute(name + "." //$NON-NLS-1$
												+ i++, o.toString());
									}
									continue l2;
								}
							}
						}
					}
					int i = 0;
					for (final Object o : c) {
						final String name2 = name + "." //$NON-NLS-1$
								+ i++;
						final IAbstractConfiguration createSubConfiguration = config
								.createSubConfiguration(name2);
						if (o == null) {
							createSubConfiguration.setStringAttribute(
									"class", ""); //$NON-NLS-1$ //$NON-NLS-2$
							continue;
						}
						createSubConfiguration.setStringAttribute(
								"class", o.getClass().getName()); //$NON-NLS-1$
						final IConfigurationPersistenceDelegate<Object> storeLoad = getInstance()
								.get(o.getClass()).getService();
						if (storeLoad == null) {
							throw new IllegalArgumentException(
									MessageFormat
											.format(
													"Field {0} may not be persisted", f)); //$NON-NLS-1$
						} else {
							storeLoad.store(o, createSubConfiguration);
						}
					}
					continue l2;
				} else {
					final IAbstractConfiguration subConfiguration = config
							.createSubConfiguration(name);
					if (obj == null) {
						subConfiguration.setStringAttribute("class", ""); //$NON-NLS-1$ //$NON-NLS-2$
						continue l2;
					}
					subConfiguration.setStringAttribute(
							"class", obj.getClass().getName()); //$NON-NLS-1$
					ServiceObject<IConfigurationPersistenceDelegate> serviceObject = getInstance()
							.get(obj.getClass());
					final IConfigurationPersistenceDelegate<Object> storeLoad = serviceObject!=null?serviceObject.getService():new DefaultConfigurationPersistenceDelegate();
					if (storeLoad == null) {
						throw new IllegalArgumentException(MessageFormat
								.format("Field {0} may not be persisted", f)); //$NON-NLS-1$
					} else {
						storeLoad.store(obj, subConfiguration);
						continue l2;
					}
				}
			}

		}
		final Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			storeObject(config, toStore, superclass);
		}
	}

	public static void store(Object p, IAbstractConfiguration cnf) {
		final IConfigurationPersistenceDelegate<Object> storeLoad = ConfigurationPersistence
				.getInstance().getStoreLoad(p.getClass());
		storeLoad.store(p, cnf);
	}

	public static <T> T load(Class<T> clazz, IAbstractConfiguration cnf) {
		return (T) ConfigurationPersistence.getInstance().getStoreLoad(clazz)
				.load((Class) clazz, cnf);
	}

	public static void registerPersistingClass(Class clazz) {
		classes.put(clazz.getName(), clazz);
	}
}