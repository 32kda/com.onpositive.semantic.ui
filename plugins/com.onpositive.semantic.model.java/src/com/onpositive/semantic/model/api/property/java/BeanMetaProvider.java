package com.onpositive.semantic.model.api.property.java;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.access.IExternalizer;
import com.onpositive.semantic.model.api.changes.DefaultListenersProvider;
import com.onpositive.semantic.model.api.changes.IObjectListenersProvider;
import com.onpositive.semantic.model.api.changes.IRoledListener;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.LyfecycleUtils;
import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.api.globals.IKey;
import com.onpositive.semantic.model.api.globals.IKeyResolver;
import com.onpositive.semantic.model.api.globals.Key;
import com.onpositive.semantic.model.api.id.IIdentifierProvider;
import com.onpositive.semantic.model.api.id.SimpleRealmBasedIdentifierProvider;
import com.onpositive.semantic.model.api.labels.IDefaultProvider;
import com.onpositive.semantic.model.api.labels.ILabelLookup;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.labels.TextProviderAdapter;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.IMetaProvider;
import com.onpositive.semantic.model.api.meta.IServiceJoiner;
import com.onpositive.semantic.model.api.meta.IServiceProvider;
import com.onpositive.semantic.model.api.meta.IServiceRegistrator;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.method.IEvaluatorProvider;
import com.onpositive.semantic.model.api.property.ComputedProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.property.java.annotations.OnCreate;
import com.onpositive.semantic.model.api.property.java.annotations.OnDelete;
import com.onpositive.semantic.model.api.property.java.annotations.OnModify;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaContributor;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ValidatorJoiner;
import com.onpositive.semantic.model.api.realm.ConstantRealmProvider;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.RealmAccess;
import com.onpositive.semantic.model.api.validation.DefaultValidationContextProvider;
import com.onpositive.semantic.model.api.validation.IFindAllWithSimilarValue;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.IValidationContextProvider;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.IterableValidator;

@SuppressWarnings("serial")
public class BeanMetaProvider implements IMetaProvider {

	private static final class DefaultLabelProvider extends TextProviderAdapter implements  IDefaultProvider{
		public String getText(IHasMeta meta, Object parent, Object object) {
			Iterable<Object> collection = ValueUtils
					.toCollectionIfCollection(object);
			if (collection != null) {
				StringBuilder bl = new StringBuilder();
				int a = 0;
				char c = ',';
				String stringValue = DefaultMetaKeys.getStringValue(meta,
						DefaultMetaKeys.SEPARATOR_CHARACTERS_KEY);
				if (stringValue != null && stringValue.length() > 0) {
					c = stringValue.charAt(0);
				}
				for (Object o : collection) {
					bl.append(LabelAccess.getLabel(o));
					bl.append(c);
					bl.append(' ');
					if (a == 3) {
						bl.append("...");
						return bl.toString();
					}
					a++;
				}
				if (bl.length() > 0) {
					bl.delete(bl.length() - 2, bl.length());
				}
				return bl.toString();
			}
			if (object == null) {
				return "";
			}
			if (object instanceof Number) {
				return NumberFormat.getInstance().format(object);
			}
			return object.toString();
		}
	}

	private static final class MethodListener implements IValueListener<Object>,IRoledListener {
		private final Method m;
		private String role;

		private MethodListener(Method m, String update) {
			this.m = m;
			this.role=update;
		}

		@Override
		public void valueChanged(Object oldValue, Object newValue) {
			try {
				m.invoke(newValue);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public int hashCode() {
			return m.getName().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MethodListener other = (MethodListener) obj;
			return m.getName().equals(other.m.getName())&&Arrays.equals(m.getParameterTypes(), other.m.getParameterTypes())&&this.role.equals(other.role);
		}

		@Override
		public String getRole() {
			return role;
		}
	}

	static IdentityHashMap<Class<?>, IMeta> ms = new IdentityHashMap<Class<?>, IMeta>();
	public static IKey CLASS_KEY = new Key(null, "class:");
	static IKey CLASS_META = new Key(null, "class_meta:");

	@SuppressWarnings("unchecked")
	public IMeta getMeta(Object object) {
		Class<? extends Object> class1 = (Class<? extends Object>) (object instanceof Class ? object
				: object.getClass());
		IMeta iMeta = ms.get(class1);
		if (iMeta != null) {
			return iMeta;
		}
		IMeta m = buildMeta(class1);
		ms.put(class1, m);
		return m;
	}

	private static IMeta getInternalMeta(Object object) {
		Class<? extends Object> class1 = (Class<? extends Object>) (object instanceof Class ? object
				: object.getClass());
		IMeta iMeta = ms.get(class1);
		if (iMeta != null) {
			return iMeta;
		}
		IMeta m = buildMeta(class1);
		ms.put(class1, m);
		return m;
	}

	static {
		GlobalAccess.addResolver("class_meta", new IKeyResolver() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Object resolveKey(String key) {
				try {
					Class<?> clz = BeanMetaProvider.class.getClassLoader()
							.loadClass(key);
					return BeanMetaProvider.buildMeta(clz);
				} catch (ClassNotFoundException e) {
					return null;
				}
			}

			public IKey getKey(Object obj) {
				return CLASS_META;
			}
		});
		GlobalAccess.addResolver("class", new IKeyResolver() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Object resolveKey(String key) {
				try {
					return BeanMetaProvider.class.getClassLoader().loadClass(
							key);
				} catch (ClassNotFoundException e) {
					return null;
				}
			}

			public IKey getKey(Object obj) {
				return CLASS_KEY;
			}
		});
		GlobalAccess.addResolver("class_realm", new IKeyResolver() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Object resolveKey(String key) {
				try {
					Class<?> loadClass = BeanMetaProvider.class.getClassLoader().loadClass(
							key);
					IRealm<Object> realm = (IRealm<Object>) RealmAccess.getRealm(loadClass);
					return realm;
				} catch (ClassNotFoundException e) {
					return null;
				}
			}

			public IKey getKey(Object obj) {
				return CLASS_KEY;
			}
		});
	}

	public static final BeanMetaProvider INSTANCE = new BeanMetaProvider();

	static BaseMeta mb = new BaseMeta();
	static BaseMeta annotation;
	static {
		mb.registerService(IIdentifierProvider.class,
				new SimpleRealmBasedIdentifierProvider());

		mb.registerService(IValidationContextProvider.class,
				new DefaultValidationContextProvider());
		mb.registerService(IPropertyProvider.class,
				JavaPropertyProvider.instance);
		mb.registerService(IEvaluatorProvider.class,
				JavaPropertyProvider.instance);
		mb.registerService(IKeyResolver.class, new DefaultKeyResolverProvider());
		mb.putMeta(IValidationContext.DEEP_VALIDATION, false);
		mb.putMeta(DefaultMetaKeys.PROP_ID_KEY, new ComputedProperty(
				DefaultMetaKeys.PROP_ID_KEY, DefaultMetaKeys.PROP_ID_KEY) {

			public Object getValue(Object obj) {
				Iterable<IProperty> properties = PropertyAccess
						.getProperties(obj);
				for (IProperty p : properties) {
					// TODO CACHE;
					if (DefaultMetaKeys.getValue(p, DefaultMetaKeys.ID_KEY,
							Boolean.class, false)) {
						return p.getValue(obj);
					}
				}
				return null;
			}
		});
		mb.lock();
		mb.registerService(IFindAllWithSimilarValue.class,
				new IFindAllWithSimilarValue() {

					public Iterable<Object> find(IHasMeta meta, Object object,
							Object value, IProperty prop) {
						IRealm<Object> realm = RealmAccess.getRealm(meta,
								object, value);
						try{
						if (realm != null) {
							IFindAllWithSimilarValue service = DefaultMetaKeys
									.getService(realm,
											IFindAllWithSimilarValue.class);

							if (service != null) {
								return service.find(meta, object, value, prop);
							}
							ArrayList<Object> result = null;
							if (prop != null) {
								for (Object o : realm) {
									if (ValueUtils.hasValue(prop, o, value)) {
										if (result == null) {
											result = new ArrayList();
										}
										result.add(o);
									}
								}
							}
							if (result != null) {
								return result;

							}
						}
						return Collections.emptyList();
						}finally{
							LyfecycleUtils.disposeIfShortLyfecycle(realm);
						}
					}
				});
		mb.registerService(ITextLabelProvider.class, new DefaultLabelProvider());
		mb.registerService(IExternalizer.class,
				new IServiceProvider<IExternalizer>() {

					public IExternalizer getService(IHasMeta meta,
							Class<IExternalizer> serv, IHasMeta original) {
						final Class<?> subjectClass = DefaultMetaKeys
								.getSubjectClass(meta);
						if (subjectClass != null) {
							return new IExternalizer() {

								public String externalizeMessage(String message) {
									return JavaPropertyProvider
											.externalizeString(subjectClass,
													message);
								}
							};
						}
						return null;
					}
				});

		BaseMeta baseMeta = new BaseMeta(mb);
		baseMeta.registerService(IServiceJoiner.class, new ValidatorJoiner());
		ms.put(IValidator.class, baseMeta);

		BaseMeta baseMeta1 = new BaseMeta(mb);
		baseMeta1.registerService(IServiceJoiner.class,
				new ObjectListenersJoiner());
		ms.put(IObjectListenersProvider.class, baseMeta1);
		annotation = new BaseMeta(mb);
		annotation.registerService(IServiceRegistrator.class,
				new IServiceRegistrator() {

					public void registerService(IWritableMeta meta,
							Class<?> servClass, Object value) {
						MetaContributor.contribute(meta, (Annotation) value);
					}
				});
		ms.put(Annotation.class, annotation);
		ms.put(Object.class, mb);
	}

	private static IMeta buildMeta(Class<? extends Object> class1) {
		if (class1 == Object.class) {
			return mb;
		}
		if (class1.isPrimitive()) {
			class1 = mapPrimitive(class1);
		}

		Class<?> superclass = class1.getSuperclass();
		if (class1.isAnnotation()) {
			superclass = Annotation.class;
		}
		
		BaseMeta ma = registerDefaultValidators(class1, superclass);
		if (class1==Boolean.class){
			ma.putMeta(DefaultMetaKeys.IMAGE_IS_ENOUGH_IN_CELLS,true);
		}
		ma.setDefaultServiceProvider(MetaAccess.getDefaultServiceProvider());
		ms.put(class1, ma);
		ma.putMeta("class", class1);

		StringBuilder sb = new StringBuilder();
		sb.append(CLASS_KEY);
		sb.append("//");
		sb.append(class1.getName());
		String string = sb.toString();
		ma.putMeta(DefaultMetaKeys.OBJECT_KEY, string);
		registerDefaultClassResolver(class1, ma);
		MetaContributor.contribute(ma, class1);
		ma.putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, class1);
		MetaAccess.contribute(CLASS_KEY, ma);
		if (Map.class.isAssignableFrom(class1)){
			ma.registerService(IPropertyProvider.class, MapPropertyProvider.instance);
		}
		ma.putMeta(BaseMeta.KEY_FIELD, string+"/"+"@meta");
		ma.lock();
		return ma;
	}

	private static Class<?>[] primitives = new Class[] { boolean.class,
			short.class, char.class, int.class, long.class, float.class,
			double.class };
	private static Class<?>[] normals = new Class[] { Boolean.class,
			Short.class, Character.class, Integer.class, Long.class,
			Float.class, Double.class };

	static Class<? extends Object> mapPrimitive(Class<? extends Object> class1) {
		for (int a = 0; a < primitives.length; a++) {
			if (primitives[a] == class1) {
				return normals[a];
			}
		}
		return class1;
	}

	private static void registerDefaultClassResolver(
			final Class<? extends Object> class1, BaseMeta ma) {
		ma.registerService(IClassResolver.class, new IClassResolver() {

			public Class<?> resolveClass(String className) {
				ClassLoader classLoader = class1.getClassLoader();
				if (classLoader == null) {
					classLoader = ClassLoader.getSystemClassLoader();
				}
				if (classLoader != null) {
					try {
						return classLoader.loadClass(className);
					} catch (ClassNotFoundException e) {
						//throw new IllegalArgumentException(e);
					}
				}
				return null;
			}

			@Override
			public URL resolveResource(String className) {
				final URL resource = class1.getResource(className);
				return resource;
			}

			@Override
			public InputStream openResourceStream(String path)
					throws IOException {
				return resolveResource(path).openStream();
			}
		});

	}

	static ITextLabelProvider number = new ITextLabelProvider() {

		public String getText(IHasMeta meta, Object parent, Object object) {
			if (object == null) {
				return "";
			}
			try {
				return NumberFormat.getInstance().format(object);
			} catch (IllegalArgumentException e) {
				return object.toString();
			}
		}

		public String getDescription(Object object) {
			return getText(null, null, object);
		}
	};

	private static BaseMeta registerDefaultValidators(
			final Class<? extends Object> class1, Class<?> superclass) {
		BaseMeta ma = new BaseMeta(
				superclass != null ? getInternalMeta(superclass) : mb);
		Method[] declaredMethods = class1.getDeclaredMethods();
		DefaultListenersProvider lp = null;		
		for (final Method m : declaredMethods) {
			Annotation[] annotations = m.getAnnotations();
			try{
			m.setAccessible(true);
			}catch (Exception e) {
				continue;
			}
			for (Annotation a : annotations) {
				if (a instanceof OnModify) {
					if (lp == null) {
						lp = new DefaultListenersProvider();

					}
					lp.add(new MethodListener(m,IRoledListener.UPDATE));
				}
				if (a instanceof OnDelete) {
					if (lp == null) {
						lp = new DefaultListenersProvider();

					}
					lp.add(new MethodListener(m,IRoledListener.DELETE));
				}
				if (a instanceof OnCreate) {
					if (lp == null) {
						lp = new DefaultListenersProvider();

					}
					lp.add(new MethodListener(m,IRoledListener.ADD));
				}
			}
		}
		if (lp!=null){
			ma.registerService(IObjectListenersProvider.class, lp);
		}
		
		if (class1.getName().startsWith("java.")
				| class1.getName().startsWith("javax.")) {
			ma.putMeta(IValidationContext.DEEP_VALIDATION, false);
			ma.putMeta(IValidationContext.NEVER_VALIDATE, true);
		}
		if (class1.isEnum()) {
			ma.registerService(IRealmProvider.class, new ConstantRealmProvider(
					class1.getEnumConstants()));
			ma.putMeta(DefaultMetaKeys.FIXED_BOUND_KEY, true);
		}
		if (class1.isArray()) {
			ma.registerService(IValidator.class, new IterableValidator());
		} else if (Iterable.class.isAssignableFrom(class1)) {
			ma.registerService(IValidator.class, new IterableValidator());
		} else if (Iterable.class.isAssignableFrom(class1)) {
			ma.registerService(IValidator.class, new IterableValidator());
		} else if (Number.class.isAssignableFrom(class1)) {
			NumberValidator object = new NumberValidator(class1);
			ma.registerService(IValidator.class, object);
			ma.registerService(ILabelLookup.class, object);
			ma.registerService(ITextLabelProvider.class, number);
		} else if (Boolean.class.isAssignableFrom(class1)) {
			NumberValidator object = new NumberValidator(class1);
			ma.registerService(IValidator.class, object);
			ma.registerService(ILabelLookup.class, object);
			ma.registerService(IRealmProvider.class, new ConstantRealmProvider(
					Boolean.FALSE, Boolean.TRUE));
		} else if (Character.class.isAssignableFrom(class1)) {
			NumberValidator object = new NumberValidator(class1);
			ma.registerService(IValidator.class, object);
			ma.registerService(ILabelLookup.class, object);
		}
		return ma;
	}
}