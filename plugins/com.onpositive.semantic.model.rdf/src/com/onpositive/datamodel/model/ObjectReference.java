package com.onpositive.datamodel.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.semantic.model.api.roles.LabelManager;

public class ObjectReference implements Serializable, IReference {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String url;
	private String name;

	public ObjectReference(IEntry entry) {
		url = entry.getId();
		name=LabelManager.getInstance().getText(entry, null, null);
	}

	public ObjectReference(Object obj) {
		if (obj instanceof IEntry){
			IEntry e=(IEntry) obj;
			url=e.getId();
			name=LabelManager.getInstance().getText(e, null, null);
		}
		else{
		IEntry entry = ProxyProvider.getEntry(obj);
		url = entry.getId();
		name=LabelManager.getInstance().getText(entry, null, null);
		}
	}

	public IEntry resolve(IDataStoreRealm realm) {
		return realm.getObject(url);
	}

	public boolean isBroken(IDataStoreRealm realm) {
		return realm.getObject(url) == null;
	}

	public Set<? extends Object> resolve(IDataStoreRealm realm,
			Map<String, Object> parameters) {
		return Collections.singleton(resolve(realm));
	}

	public boolean isResolvable(IDataStoreRealm ra,
			Map<String, Object> parameters) {
		return !isBroken(ra);
	}
	
	public String toString(){
		return name;
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof IEntry){
			IEntry e=(IEntry) obj;
			return this.url.equals(e.getId());
		}
		if (getClass() != obj.getClass())
			return false;
		ObjectReference other = (ObjectReference) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}