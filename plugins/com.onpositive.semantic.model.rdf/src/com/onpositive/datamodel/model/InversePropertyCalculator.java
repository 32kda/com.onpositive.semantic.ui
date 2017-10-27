package com.onpositive.datamodel.model;

import java.util.Collections;
import java.util.Set;

import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.impl.IDataStoreRealm;

public class InversePropertyCalculator implements ICalculatableProperty {

	private final String of;
	private IDataStoreRealm realm;

	public IDataStoreRealm getRealm() {
		return realm;
	}

	public void setRealm(IDataStoreRealm realm) {
		this.realm = realm;
	}

	public InversePropertyCalculator(String of) {
		super();
		this.of = of.intern();
	}

	public Object getValue(Object obj, String id) {
		Set<Object> values = getValues(obj, id);
		if (values!=null&&!values.isEmpty()){
			return values.iterator().next();
		}
		return null;
	}

	public int getValueCount(Object obj, String id) {
		Set<Object> values = getValues(obj, id);
		return values!=null?values.size():0;
	}

	@SuppressWarnings("unchecked")
	public Set<Object> getValues(Object obj, String id) {
		if (obj instanceof IEntry){
			return (Set)realm.findEntries(of, obj);
		}
		return Collections.emptySet();
	}

	public boolean hasValue(Object obj, Object value, String id) {
		Set<Object> value2 = getValues(obj, id);
		return value2!=null&&value2.contains(value);
	}

	public void setHost(CalculatablePropertyHost property) {
		realm = property.getRealm();
	}
}
