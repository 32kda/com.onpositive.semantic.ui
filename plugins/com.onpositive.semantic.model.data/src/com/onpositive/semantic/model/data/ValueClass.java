package com.onpositive.semantic.model.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import org.eclipse.core.runtime.Platform;

import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.realm.CompositeValidator;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IType;
import com.onpositive.semantic.model.realm.IValidator;

public class ValueClass implements Cloneable, IType {

	private String name;
	private String id;
	private String description;
	private String superClassesString;
	
	private final HashSet<DefaultModelProperty> properties = new HashSet<DefaultModelProperty>();
	
	private final IdentityHashMap<Class<?>, Object> adapters = new IdentityHashMap<Class<?>, Object>();
	
	private final HashSet<IValidator<?>> validators = new HashSet<IValidator<?>>();
	
	private Class<?> subjectClass;
	
	private HashSet<IInstanceListener>listeners;

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> adapter) {
		final Object o = this.adapters.get(adapter);

		if (o == null) {
			for (final ValueClass v : this.getSuperClasses()) {
				final T r = v.getAdapter(adapter);
				if (r != null) {
					return r;
				}
			}
			if (adapter == ITextLabelProvider.class) {
				return (T) LabelManager.getInstance().getLabelProvider();
			}
			if (!this.isObjectClass()) {
				final Object adapter2 = Platform.getAdapterManager()
						.getAdapter(this.getSubjectClass(), adapter);
				this.adapters.put(adapter, adapter2);
				return (T) adapter2;
			}
		}

		return adapter.cast(o);
	}

	public IValidator<?> getValidator() {
		final CompositeValidator<Object> vl = new CompositeValidator<Object>();
		this.fillValidators(vl);
		if (!vl.isEmpty()) {
			return vl;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void fillValidators(CompositeValidator<Object> va) {
		for (final IValidator<?> vls : this.validators) {
			va.addValidator((IValidator<Object>) vls);

		}

		for (final ValueClass v : this.getSuperClasses()) {
			v.fillValidators(va);
		}
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	private DataModel onthology;
	private final HashSet<ValueClass> superClasses = new HashSet<ValueClass>();
	private Set<ValueClass> sc;

	public ValueClass(DataModel onthology) {
		super();
		this.onthology = onthology;
	}

	public DataModel getOnthology() {
		return this.onthology;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public void setId(String id) {
		this.id = id.intern();
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	void addSuperClass(ValueClass vClass) {
		this.superClasses.add(vClass);
		this.sc = Collections.unmodifiableSet(this.superClasses);
	}

	void removeSuperClass(ValueClass vClass) {
		this.superClasses.add(vClass);
	}

	void setOwner(final DataModel md) {
		this.onthology = md;
		if ((this.superClassesString != null)
				&& (this.superClassesString.length() > 0)) {
			final String[] split = this.superClassesString.split(","); //$NON-NLS-1$
			for (int a = 0; a < split.length; a++) {
				final String string = split[a].trim().intern();
				md.addLoadWaiter(string, new Runnable() {

					public void run() {
						final ValueClass valueClass = md.getValueClass(string);
						ValueClass.this.addSuperClass(valueClass);
					}

				});
			}
		}
	}

	public String getSuperClassesString() {
		return this.superClassesString;
	}

	public void setSuperClassesString(String superClassesString) {
		this.superClassesString = superClassesString;
	}

	public Set<ValueClass> getSuperClasses() {
		if (this.sc == null) {
			this.sc = Collections.unmodifiableSet(this.superClasses);
		}
		return this.sc;
	}
	
	public String toString(){
		return this.id;
	}

	public boolean isSuperClassOf(String url) {
		final HashSet<ValueClass> va = new HashSet<ValueClass>();
		return this.internalIsSuperClass(url, va);
	}

	private boolean internalIsSuperClass(String url, HashSet<ValueClass> va) {
		if (va.contains(this)) {
			return false;
		}
		va.add(this);
		for (final ValueClass s : this.superClasses) {
			if (url.equals(s.id)) {
				return true;
			}
		}
		for (final ValueClass s : this.superClasses) {
			if (s.internalIsSuperClass(url, va)) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof IEntry) {
			final IEntry e = (IEntry) obj;
			return this.id.equals(e.getId());
		}
		if (obj instanceof ValueClass) {
			final ValueClass vl = (ValueClass) obj;
			return this.id.equals(vl.getId());
		}
		return false;
	}

	public Set<String> getPropertyIds() {
		final HashSet<String> result = new HashSet<String>();
		final HashSet<ValueClass> visited = new HashSet<ValueClass>();
		this.fillProps(result, visited);
		return result;
	}

	private void fillProps(HashSet<String> result, HashSet<ValueClass> visited) {
		if (visited.contains(this)) {
			return;
		}
		visited.add(this);
		for (final DefaultModelProperty p : this.properties) {
			result.add(p.getId());
		}
		for (final ValueClass s : this.superClasses) {
			s.fillProps(result, visited);
		}
	}

	void addProperty(DefaultModelProperty defaultModelProperty) {
		this.properties.add(defaultModelProperty);
	}

	public String getId() {
		return id;
	}

	public Set<IProperty> getProperties() {
		return null;
	}

	public void addValidator(IValidator<?> vl) {
		this.validators.add(vl);
	}

	public <T> void registerAdapter(Class<T> clazz, T adapter) {
		this.adapters.put(clazz, adapter);
	}

	Boolean isObject;

	@SuppressWarnings("unchecked")
	public boolean isObjectClass() {
		if (this.isObject != null) {
			return this.isObject;
		}
		this.isObject = (this.getSubjectClass() == (Class) IEntry.class);
		return this.isObject;
	}

	@SuppressWarnings("unchecked")
	public Class<Object> getSubjectClass() {
		if (this.subjectClass != null) {
			return (Class<Object>) this.subjectClass;
		}
		for (final ValueClass v : this.getSuperClasses()) {
			final Class<Object> x = v.getSubjectClass();
			if (x != null) {
				return x;
			}
		}
		return null;
	}

	public void setSubjectClass(Class<?> class1) {
		this.subjectClass = class1;
	}

	public IRealm<Object> getRealm() {
		return null;
	}

	public void addInstanceListener(IInstanceListener newInstance) {
		if (listeners==null){
			listeners=new HashSet<IInstanceListener>();
		}
		listeners.add(newInstance);
	}
	
	public Iterable<IInstanceListener>getInstanceListeners(){
		return listeners;
	}
	
	Boolean isJava;
	Boolean broadCastChanges;

	public final Boolean getBroadCastChanges() {
		return broadCastChanges;
	}

	public final void setBroadCastChanges(Boolean broadCastChanges) {
		this.broadCastChanges = broadCastChanges;
	}

	public boolean isJava() {
		if (isJava!=null){
			return isJava;
		}
		for (ValueClass v:getSuperClasses()){
			if (v.isJava()){
				isJava=true;
				return isJava;
			}
		}
		if (this.getId().equals("Java")){
			isJava=true;
		}
		else{
			isJava=false;
		}
		return isJava;
	}

	public boolean isBroadCastChanges() {
		if (broadCastChanges!=null){
			return broadCastChanges;
		}
		broadCastChanges=false;
		for (ValueClass v:getSuperClasses()){
			if (v.isBroadCastChanges()){
				broadCastChanges=true;
				return broadCastChanges;
			}
		}		
		return broadCastChanges;
	}

}