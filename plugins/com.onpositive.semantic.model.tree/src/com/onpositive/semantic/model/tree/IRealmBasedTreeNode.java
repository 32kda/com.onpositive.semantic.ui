package com.onpositive.semantic.model.tree;

import com.onpositive.semantic.model.api.realm.IRealm;

public interface IRealmBasedTreeNode<T> extends ITreeNode<T>{

	public void setRealm(IRealm<?> r);
	
	public IRealm<?>getChildRealm();
	
}
