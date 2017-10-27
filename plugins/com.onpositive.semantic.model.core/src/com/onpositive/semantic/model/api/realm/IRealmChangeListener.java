package com.onpositive.semantic.model.api.realm;

import java.io.Serializable;

import com.onpositive.semantic.model.api.changes.ISetDelta;

public interface IRealmChangeListener<T> extends Serializable{

	void realmChanged(IRealm<T> realmn, ISetDelta<T> delta);
}
