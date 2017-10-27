package com.onpositive.semantic.model.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.realm.AbstractFilter;
import com.onpositive.semantic.model.api.relation.IRelation;

public abstract class GroupingPointProvider<T> extends
		AbstractClusterizationPointProvider<T> {

	private final class XRelation extends AbstractFilter implements IRelation,
			Comparable<XRelation> {
		private final Object o;

		private XRelation(Object o) {
			this.o = o;
		}

		@SuppressWarnings("unchecked")
		public boolean accept(Object element) {
			final Set<? extends Object> group = GroupingPointProvider.this
					.getGroup((T) element);
			if (group == null) {
				return this.o == null;
			}
			return group.contains(this.o);
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.getOuterType().hashCode();
			result = prime * result
					+ ((this.o == null) ? 0 : this.o.hashCode());
			return result;
		}

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
			final XRelation other = (XRelation) obj;
			if (!this.getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (this.o == null) {
				if (other.o != null) {
					return false;
				}
			} else if (!this.o.equals(other.o)) {
				return false;
			}
			return true;
		}

		public String getName() {
			return ""; //$NON-NLS-1$
		}

		private GroupingPointProvider<?> getOuterType() {
			return GroupingPointProvider.this;
		}

		public Object getPresentationObject() {
			return GroupingPointProvider.this.getPresentationObject(this.o);
		}

		@SuppressWarnings("unchecked")
		public int compareTo(XRelation o) {
			try {
				if (this.o instanceof Comparable) {
					if (o.o instanceof Comparable) {
						return -((Comparable) this.o).compareTo(o.o);
					}
				}
			} catch (final ClassCastException e) {
			}
			final Object presentationObject = o.getPresentationObject();
			if (presentationObject instanceof Comparable<?>) {
				final Object presentationObject2 = this.getPresentationObject();
				final Comparable<Object> c = (Comparable<Object>) presentationObject;
				try {
					return -c.compareTo(presentationObject2);
				} catch (final Exception e) {
					return 0;
				}
			}
			return 0;
		}

	}

	@SuppressWarnings("unchecked")
	public ISetDelta<IClusterizationPoint<T>> processDelta(ISetDelta<T> delta,
			Collection<IClusterizationPoint<T>> currentPoints,
			Collection<T> currentElements) {
		final HashSet<Object> ss = new HashSet<Object>();
		for (final T o : currentElements) {
			ss.addAll(this.getGroup(o));
		}
		final HashSet<IClusterizationPoint<T>> result = new HashSet<IClusterizationPoint<T>>();
		for (final Object o : ss) {
			result.add(this.createPoint(o));
		}
		final HashDelta<IClusterizationPoint<T>> buildFrom = HashDelta
				.buildFrom(currentPoints, result);
		return buildFrom;
	}

	public IClusterizationPoint<T> createPoint(final Object o) {
		final IRelation relation = new XRelation(o);
		final RelationClusterizationPoint<T> relationClusterizationPoint = new RelationClusterizationPoint<T>(
				relation);
		relationClusterizationPoint.setHideEmpty(true);
		relationClusterizationPoint.setVisible(true);
		return relationClusterizationPoint;
	}

	public abstract Set<? extends Object> getGroup(T o);

	public abstract Object getPresentationObject(Object o);

}