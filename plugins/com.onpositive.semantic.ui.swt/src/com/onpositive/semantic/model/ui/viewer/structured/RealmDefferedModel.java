package com.onpositive.semantic.model.ui.viewer.structured;

import org.eclipse.jface.viewers.deferred.AbstractConcurrentModel;
import org.eclipse.jface.viewers.deferred.IConcurrentModelListener;

import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IRealmChangeListener;
import com.onpositive.semantic.model.realm.ISetDelta;

public class RealmDefferedModel extends AbstractConcurrentModel implements
		IRealmChangeListener<Object> {

	IRealm<Object> realm;

	public RealmDefferedModel(IRealm<?> realm) {
		super();
		this.realm = (IRealm<Object>) realm;
		realm.addRealmChangeListener((IRealmChangeListener) this);
	}

	public void requestUpdate(IConcurrentModelListener listener) {
		listener.setContents(this.realm.getContents().toArray());
	}

	public void realmChanged(IRealm<Object> realmn, ISetDelta<Object> delta) {
		super.fireAdd(delta.getAddedElements().toArray());
		super.fireRemove(delta.getRemovedElements().toArray());
		super.fireUpdate(delta.getChangedElements().toArray());
	}

}
