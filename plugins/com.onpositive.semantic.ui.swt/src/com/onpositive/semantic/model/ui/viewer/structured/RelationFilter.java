package com.onpositive.semantic.model.ui.viewer.structured;

import com.onpositive.semantic.model.realm.FilteringRealm;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IRelation;
import com.onpositive.semantic.model.tree.RealmNode;

public class RelationFilter<T> {

	private IRelation relation;

	private final IRealm<T> originalRealm;
	private FilteringRealm<T> filteringRealm;
	private final RealmNode<T> owner;

	public RelationFilter(IRealm<T> originalRealm, RealmNode<T> owner) {
		super();
		this.originalRealm = originalRealm;
		this.owner = owner;
	}

	public IRelation getRelation() {
		return this.relation;
	}

	public void setRelation(IRelation relation) {
		if (relation == null) {
			return;
		}
		this.relation = relation;
		this.install();
	}

	void install() {
		if (this.filteringRealm != null) {
			this.filteringRealm.dispose();
		}
		this.filteringRealm = new FilteringRealm<T>(this.originalRealm, this.relation);
		this.owner.setRealm(this.filteringRealm);
	}

	void uninstall() {
		if (this.relation == null) {
			return;
		}
		this.owner.setRealm(this.originalRealm);
		this.filteringRealm.dispose();
	}
}
