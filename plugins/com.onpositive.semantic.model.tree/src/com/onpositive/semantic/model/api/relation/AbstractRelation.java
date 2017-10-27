package com.onpositive.semantic.model.api.relation;

import com.onpositive.semantic.model.api.changes.AbstractListenable;

public abstract class AbstractRelation extends AbstractListenable {


	public String getName() {
		return "Unnamed Relation";
	}

	public Object getPresentationObject() {
		return this;
	}

	public abstract boolean accept(Object element);

	protected void fireFilterChanged(Object extra) {
		fireChanged();
	}

}
