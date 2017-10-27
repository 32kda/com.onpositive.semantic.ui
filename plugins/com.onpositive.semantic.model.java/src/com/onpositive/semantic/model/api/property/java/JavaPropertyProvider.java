package com.onpositive.semantic.model.api.property.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.api.globals.IKey;
import com.onpositive.semantic.model.api.globals.IKeyResolver;
import com.onpositive.semantic.model.api.globals.Key;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.method.IEvaluatorProvider;
import com.onpositive.semantic.model.api.method.IMethodEvaluator;
import com.onpositive.semantic.model.api.method.java.JavaMethodEvaluator;
import com.onpositive.semantic.model.api.property.BasicExecutableOperation;
import com.onpositive.semantic.model.api.property.CommonPropertyProvider;
import com.onpositive.semantic.model.api.property.CompositeExecutableOperation;
import com.onpositive.semantic.model.api.property.DefaultExecutor;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.ITargetDependentReadonly;
import com.onpositive.semantic.model.api.undo.IUndoManager;
import com.onpositive.semantic.model.api.undo.IUndoableOperation;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;

//TODO THINK ABOUT inheritance of meta for methods....
//from super classes and interfaces
public class JavaPropertyProvider extends DefaultExecutor implements IPropertyProvider,
		ICommandExecutor, IEvaluatorProvider {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final JavaPropertyProvider instance = new JavaPropertyProvider();

	static IKey PROPERTY_META = new Key(null, "property");
	static {
		GlobalAccess.addResolver("property", new IKeyResolver() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Object resolveKey(String key) {
				String[] ps = key.split("-");
				if (ps != null && ps.length != 0) {
					try {
						Class<?> clz = JavaPropertyProvider.class
								.getClassLoader().loadClass(ps[0]);
						if (instance != null) {
							ClassPropertyInfo info = instance
									.getProperties(clz);
							IProperty p = info.getProperty(ps[1]);
							if (p != null) {
								return p;
							}
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				return null;
			}

			public IKey getKey(Object obj) {
				return PROPERTY_META;
			}
		});
	}

	private transient HashMap<Class<?>, ClassPropertyInfo> properties = new HashMap<Class<?>, ClassPropertyInfo>();

	private static WeakHashMap<String, ResourceBundle> bundles = new WeakHashMap<String, ResourceBundle>();

	public class ClassPropertyInfo {

		final Class<?> cls;

		LinkedHashMap<String, IProperty> properties = new LinkedHashMap<String, IProperty>();

		LinkedHashMap<String, IProperty> Allproperties = new LinkedHashMap<String, IProperty>();

		public Collection<IProperty> getAll() {
			init();

			return Allproperties.values();
		}

		boolean inited;

		private IHasMeta meta;

		protected void init() {
			if (!inited) {
				Field[] declaredFields = cls.getDeclaredFields();
				for (Field n : declaredFields) {
					getProperty(n.getName());
				}
				Method[] declaredMFields = cls.getDeclaredMethods();
				for (Method n : declaredMFields) {
					if (n.getName().startsWith("get")) {
						getProperty(n.getName().substring(3));
					}
					if (n.getName().startsWith("is")) {
						getProperty(n.getName().substring(2));
					}
				}
				Class<?> superclass = cls.getSuperclass();
				if (superclass != null && superclass != Object.class) {
					Collection<IProperty> all = instance.getProperties(
							superclass).getAll();
					for (IProperty p : all) {
						Allproperties.put(p.getId(), p);
					}
				}
				LinkedHashMap<String, IProperty> properties2 = properties;
				for (String s : properties2.keySet()) {
					IProperty iProperty = properties2.get(s);
					if (iProperty != null) {
						Allproperties.put(s, iProperty);
					}
				}
				for (IProperty p : CommonPropertyProvider.INSTANCE
						.getProperties(null)) {
					Allproperties.remove(p.getId());
				}
				inited = true;
			}
		}

		public IProperty getProperty(String name) {

			
			final IProperty property = this.properties.get(name);
			if (property != null) {
				return property;
			}
			
			final IProperty internalgetProperty = this
					.internalGetProperty(name);
			if (internalgetProperty != null) {
				this.properties.put(name, internalgetProperty);
				IMeta m = internalgetProperty.getMeta();

				if (m instanceof BaseMeta) {
					BaseMeta bm = (BaseMeta) m;					
					bm.registerServiceTransfer(ITargetDependentReadonly.class,meta);
				}
			} else {
				this.properties.put(name, null);
			}

			return internalgetProperty;
		}

		private IProperty internalGetProperty(String name) {
			if (name == null || name.length() == 0) {
				return null;
			}
			final Field field = this.getField(name);
			if (field != null) {
				return new FieldProperty(this,field);
				
			}
			final Method method = this.getMethod(name);
			if (method != null) {
				return new MethodInvocationProperty(method);
				
			}
			String name0 = name;
			name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			Method method2 = this.getMethod("get" + name, new Class[0]); //$NON-NLS-1$
			if (method2 == null) {
				method2 = this.getMethod("is" + name); //$NON-NLS-1$
			}
			if (method2 != null) {
				return new GetterSetterProperty(this,method2, name);
			}
			final Class<?> superclass = this.cls.getSuperclass();
			if (superclass != null) {
				ClassPropertyInfo classPropertyInfo = instance.properties
						.get(superclass);
				if (classPropertyInfo == null) {
					classPropertyInfo = new ClassPropertyInfo(superclass);
					instance.properties.put(superclass, classPropertyInfo);
				}
				return classPropertyInfo.getProperty(name0);
			}
			return CommonPropertyProvider.INSTANCE.getProperty(null, name0);
		}

		private Field getField(String name) {
			try {
				return this.cls.getDeclaredField(name);
			} catch (final SecurityException e) {
				throw new IllegalStateException(e);
			} catch (final NoSuchFieldException e) {
				return null;
			}
		}

		private Method getMethod(String name, Class... cl) {
			try {
				return this.cls.getDeclaredMethod(name, cl);
			} catch (final SecurityException e) {
				throw new IllegalStateException(e);
			} catch (final NoSuchMethodException e) {
				return null;
			}
		}

		public ClassPropertyInfo(Class<?> cls) {
			super();
			this.cls = cls;
			this.meta = MetaAccess.getMeta(cls);
		}

		public void register(IProperty gm) {
			inited = false;
			properties.put(gm.getId(), gm);
			Allproperties.clear();
			ObjectChangeManager.markChanged(JavaPropertyProvider.this);
		}

		public void remove(IProperty gm) {
			inited = false;
			properties.remove(gm.getId());
			Allproperties.clear();
			ObjectChangeManager.markChanged(JavaPropertyProvider.this);
		}
	}

	public Iterable<IProperty> getProperties(Object obj) {
		if (obj == null) {
			return Collections.emptySet();
		}
		ClassPropertyInfo properties2 = getProperties(obj.getClass());
		return properties2.getAll();
	}

	public IProperty getProperty(Object obj, String name) {
		try {
			if (obj == null) {
				return null;
			}
			final Class<?> class1 = (Class<?>) (obj instanceof Class<?> ? obj : obj.getClass());
			ClassPropertyInfo classPropertyInfo = getProperties(class1);
			return (IProperty) classPropertyInfo.getProperty(name);

		} catch (final Throwable e) {
			return null;
		}
	}

	public ClassPropertyInfo getProperties(final Class<?> class1) {
		if (this.properties==null){
			this.properties=new HashMap<Class<?>, JavaPropertyProvider.ClassPropertyInfo>();
		}
		ClassPropertyInfo classPropertyInfo = this.properties.get(class1);
		if (classPropertyInfo == null) {
			classPropertyInfo = new ClassPropertyInfo(class1);
			this.properties.put(class1, classPropertyInfo);
		}
		return classPropertyInfo;
	}

	public static String externalizeString(Class<?> cl, String str) {
		if (str.length() > 0) {
			if (str.charAt(0) != '%') {
				return str;
			}
			final ResourceBundle bundle = getBundle(cl);
			if (bundle == null) {
				return "Missing resource bundle with name " + cl.getPackage().getName() + ".messages"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			final String substring = str.substring(1);
			try {
				return bundle.getString(substring);
			} catch (final MissingResourceException e) {
				return cl.getPackage().getName()
						+ ".messages" + " does not contain key " + substring; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return str;
	}

	static ResourceBundle getBundle(Class<?> bundle) {
		final String baseName = bundle.getPackage().getName() + ".messages"; //$NON-NLS-1$
		final ResourceBundle resourceBundle = bundles.get(baseName);
		if (resourceBundle != null) {
			return resourceBundle;
		}
		final ResourceBundle bundle2 = ResourceBundle.getBundle(baseName,
				Locale.getDefault(), bundle.getClassLoader());
		if (bundle2 != null) {
			bundles.put(baseName, bundle2);
			return bundle2;
		}
		return null;
	}

	// private final IChangeManager manager = UndoRedoSupport
	// .getUndoRedoChangeManager();

	public void execute(ICommand cmd) {
//		boolean undo = true;
		// TODO FIXME
//		undo = UndoMetaUtils.undoAllowed(cmd);
		if (!DefaultMetaKeys.getValue(cmd, DefaultMetaKeys.IGNORE_PREPROCESSORS)){
		cmd=INVERSE_OF_COMMAND_PRE_PROCESSOR.preProcess(cmd);
		}
		final IUndoableOperation op = this.convertToUndoable(cmd);
		// if (this.manager != null&&undo) {
		// this.manager.execute(op);
		// } else {
		IUndoManager undoManager = UndoMetaUtils.getUndoManager();
		if (undoManager != null) {
			undoManager.execute(op);
		} else {
			op.execute();
		}
		// }
	}

	private IUndoableOperation convertToUndoable(final ICommand cmd) {
		Object undoContext = null;
		undoContext = UndoMetaUtils.undoContext(cmd);
		final CompositeExecutableOperation ca = new CompositeExecutableOperation(
				undoContext);
		this.createCommands(ca, cmd);
		return ca;
	}

	protected void createCommands(CompositeExecutableOperation ca, final ICommand cm) {
		if (cm instanceof CompositeCommand) {
			final CompositeCommand cac = (CompositeCommand) cm;
			for (final ICommand m : cac) {
				this.createCommands(ca, m);
			}
			return;
		} else if (cm instanceof SimpleOneArgCommand) {
			final SimpleOneArgCommand oneArg = (SimpleOneArgCommand) cm;
			final BasicExecutableOperation exec = new BasicExecutableOperation(
					oneArg);
			ca.add(exec);
		} else {
			throw new RuntimeException();
		}
	}

	public ICommandExecutor getCommandExecutor() {
		return this;
	}

	@Override
	public IMethodEvaluator getMethodEvaluator(Object obj, String name) {
		return new JavaMethodEvaluator(obj, name);
	}

	@Override
	public Iterable<IMethodEvaluator> getAllMethodEvaluators(Object obj) {
		Class<?> baseClass = (Class<?>) ((obj instanceof Class) ? obj : obj.getClass());
		Method[] declaredMethods = baseClass.getDeclaredMethods();
		if (declaredMethods.length > 0) {
			List<IMethodEvaluator> evaluators = new ArrayList<IMethodEvaluator>();
			for (int i = 0; i < declaredMethods.length; i++) {
				evaluators.add(new JavaMethodEvaluator(obj, declaredMethods[i].getName()));
			}
			return evaluators;
		}
		return Collections.emptyList();
	}

}
