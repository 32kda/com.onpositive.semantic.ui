package com.onpositive.datamodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import com.onpositive.datamodel.core.IEntry;
import com.onpositive.semantic.model.api.property.java.ClassAdapterFactory;
import com.onpositive.semantic.model.realm.IValidator;

public class DataModel {

	private static final String THING_CLASS = "Thing";
	
	private static final String JAVA_CLASS = "Java";

	private static final String INT_CLASS = "int";

	private static final String LONG_CLASS = "long";

	private static final String STRING_CLASS = "string";

	private static final String BOOLEAN_CLASS = "boolean";

	private static final String DOUBLE_CLASS = "double";

	public static final String TYPE = "com/jetface/datamodel/core/type"; //$NON-NLS-1$

	private final IdentityHashMap<String, ValueClass> classes = new IdentityHashMap<String, ValueClass>();
	private final IdentityHashMap<String, DefaultModelProperty> properties = new IdentityHashMap<String, DefaultModelProperty>();

	private final HashMap<String, ArrayList<Runnable>> loadWaiters = new HashMap<String, ArrayList<Runnable>>();
	
	private HashSet<ClassLoader> classLoader=new HashSet<ClassLoader>();
	

	public final void addClassLoader(ClassLoader classLoader) {
		this.classLoader.add(classLoader);
	}

	DefaultModelProperty getModelProperty(String id) {
		return this.properties.get(id);
	}

	public ValueClass getValueClass(String id) {
		return this.classes.get(id);
	}

	public DataModel() {
		this.initBuiltins();
	}

	private void initBuiltins() {
		final DefaultModelProperty value = new DefaultModelProperty(TYPE);
		value.setTransitive(true);
		value.setName("Type"); //$NON-NLS-1$
		this.properties.put(TYPE, value);
		this.addBuiltin(IEntry.class, THING_CLASS);
		this.addBuiltin(Object.class, JAVA_CLASS);
		this.addBuiltin(boolean.class, BOOLEAN_CLASS);
		this.addBuiltin(double.class, DOUBLE_CLASS);
		this.addBuiltin(int.class, INT_CLASS);
		this.addBuiltin(long.class, LONG_CLASS);
		this.addBuiltin(String.class, STRING_CLASS);
	}

	private void addBuiltin(Class<?> class1, String id) {
		final ValueClass thing = new ValueClass(this);
		thing.setId(id);
		thing.setSubjectClass(class1);
		if (!thing.isObjectClass()) {
			final IValidator<?> adapter = (IValidator<?>) new ClassAdapterFactory()
					.getAdapter(class1, IValidator.class);
			if (adapter != null) {
				thing.addValidator(adapter);
			}
		}
		this.classes.put(id, thing);
	}

	public void addLoadWaiter(String classId, Runnable r) {
		final ValueClass valueClass = this.getValueClass(classId);
		if (valueClass != null) {
			r.run();
		} else {
			ArrayList<Runnable> arrayList = this.loadWaiters.get(classId);
			if (arrayList == null) {
				arrayList = new ArrayList<Runnable>();
				this.loadWaiters.put(classId, arrayList);
			}
			arrayList.add(r);
		}
	}

	public void registerProperty(final DefaultModelProperty pa) {
		final String stringRange = pa.getStringRange();
		pa.setOwner(this);
		this.properties.put(pa.getId(), pa);
		this.addLoadWaiter(stringRange, new Runnable() {

			public void run() {
				pa.setRange(DataModel.this.getValueClass(stringRange));
			}
		});
		final String stringDomain = pa.getStringDomain();
		this.addLoadWaiter(stringDomain, new Runnable() {

			public void run() {
				pa.setDomain(DataModel.this.getValueClass(stringDomain));
			}
		});
	}

	public void registerClass(ValueClass vclass) {
		final String id = vclass.getId();
		vclass.setOwner(this);
		this.classes.put(id, vclass);

		final ArrayList<Runnable> arrayList = this.loadWaiters.get(id);
		if (arrayList != null) {
			for (final Runnable r : arrayList) {
				r.run();
			}
		}
	}

	public DefaultModelProperty getProperty(String name) {
		return this.properties.get(name);
	}

	public Set<DefaultModelProperty> getProperties() {
		return new HashSet<DefaultModelProperty>(
				this.properties.values());
	}

	public Class<?> resolveClass(String name) {
		for (ClassLoader l:classLoader){
			try {
				return l.loadClass(name);
			} catch (ClassNotFoundException e) {
				
			}
		}
		return null;
	}
}
