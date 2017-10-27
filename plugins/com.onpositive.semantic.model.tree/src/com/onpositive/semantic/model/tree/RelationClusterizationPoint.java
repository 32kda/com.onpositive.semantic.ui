package com.onpositive.semantic.model.tree;

import com.onpositive.commons.platform.registry.IAdaptable2;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.labels.IHasPresentationObject;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.relation.IRelation;


public class RelationClusterizationPoint<T> implements IClusterizationPoint<T>,
		IHasPresentationObject {

	IClusterizationPointProvider<T> provider;
	Object owner;
	IRelation relation;
	IPresentationFactory factory;
	boolean visible;
	boolean hideEmpty;

	public boolean isVisible() {
		return this.visible;
	}

	public IRelation getRelation() {
		return this.relation;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public RelationClusterizationPoint(IRelation relation) {
		super();
		this.relation = relation;
	}

	public RelationClusterizationPoint(
			IClusterizationPointProvider<T> provider, IRelation relation) {
		super();
		this.provider = provider;
		this.relation = relation;
	}

	public IClusterizationPointProvider<T> getSubClusterizationProvider() {
		return this.provider;
	}

	public String toString() {
		return this.relation.toString();
	}

	static int count;

	public void processDelta(ISetDelta<T> changes,
			IClusterNodeCallback<T> callback) {
		count++;
		int sz = 0;
		
		for (final T el : changes.getAddedElements()) {
			final boolean accept = this.relation.accept(el);
			if (accept) {
				callback.add(el);
				sz++;
			}
		}
		for (final T el : changes.getChangedElements()) {

			final boolean accept = this.relation.accept(el);
			if (callback.getPointElements().contains(el)) {
				if (!accept) {
					callback.removeChanged(el);
					sz--;
				}
			} else {
				if (accept) {
					callback.add(el);
					sz++;
				}
			}
		}
		for (final T el : changes.getRemovedElements()) {
			final boolean accept = this.relation.accept(el);

			if (!accept) {
				sz++;
			}

		}
		final boolean b = this.isVisible()
				&& (!this.hideEmpty || (callback.getPointElements().size() + sz > 0));
		callback.setVisible(b);

	}

	public void setHideEmpty(boolean b) {
		this.hideEmpty = b;
	}

	public boolean isHideEmpty() {
		return this.hideEmpty;
	}

	public void setClusterizationPointProvider(
			IClusterizationPointProvider<T> prov) {
		this.provider = prov;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.hideEmpty ? 1231 : 1237);
		result = prime * result
				+ ((this.provider == null) ? 0 : this.provider.hashCode());
		result = prime * result
				+ ((this.relation == null) ? 0 : this.relation.hashCode());
		result = prime * result + (this.visible ? 1231 : 1237);
		return result;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final RelationClusterizationPoint other = (RelationClusterizationPoint) obj;
		if (this.hideEmpty != other.hideEmpty) {
			return false;
		}
		if (this.provider == null) {
			if (other.provider != null) {
				return false;
			}
		} else if (!this.provider.equals(other.provider)) {
			return false;
		}
		if (this.relation == null) {
			if (other.relation != null) {
				return false;
			}
		} else if (!this.relation.equals(other.relation)) {
			return false;
		}
		if (this.visible != other.visible) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public int compareTo(IClusterizationPoint<T> o) {
		if (o instanceof RelationClusterizationPoint) {
			final RelationClusterizationPoint q = (RelationClusterizationPoint) o;
			final IRelation relation2 = q.getRelation();
			if (this.relation instanceof Comparable) {
				if (relation2 instanceof Comparable) {
					return -((Comparable<Object>) this.relation)
							.compareTo(relation2);
				}
			}
			final Object presentationObject = relation2.getPresentationObject();
			try {
				if (presentationObject instanceof Comparable<?>) {
					final Object presentationObject2 = this.relation
							.getPresentationObject();
					final Comparable<Object> c = (Comparable<Object>) presentationObject;
					if (c==null){
						return 1;
					}
					if (presentationObject==null){
						return -1;
					}
					return -c.compareTo(presentationObject2);
				}
			} catch (final ClassCastException e) {
			}
			return LabelAccess.getLabel(this).compareTo(LabelAccess.getLabel(o));
		}
		return 0;
	}

	public IPresentationFactory getFactory() {
		return factory;
	}

	public void setFactory(IPresentationFactory factory) {
		this.factory = factory;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (this.owner != null) {
			final Object adapter2 = Platform.getAdapter(owner, adapter);
			if (adapter2 != null) {
				return adapter2;
			}
		}
		if (this.relation instanceof IAdaptable2) {			
			return ((IAdaptable2) this.relation).getAdapter(adapter);
		}
		return Platform.getAdapter(this.relation, adapter);
	}

	public Object getElement() {
		if (factory!=null){
			return factory.getPresentationObject(this);
		}
		return this.relation.getPresentationObject();
	}

	public Object getOwner() {
		return this.owner;
	}

	public void setOwner(Object owner) {
		this.owner = owner;
	}

	public Object getPrimaryValue() {
		return getElement();
	}



}
