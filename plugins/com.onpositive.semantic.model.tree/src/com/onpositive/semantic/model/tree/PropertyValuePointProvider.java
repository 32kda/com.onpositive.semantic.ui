package com.onpositive.semantic.model.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.realm.IDescribableToQuery;
import com.onpositive.semantic.model.api.relation.HasValueRelation;
import com.onpositive.semantic.model.api.relation.IRelation;

public class PropertyValuePointProvider<T> extends
		AbstractClusterizationPointProvider<T> implements IDescribableToQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final IProperty property;
	private final IdentityHashMap<Class<?>, Object> adapters = new IdentityHashMap<Class<?>, Object>();
	private IPresentationFactory presentationFactory;

	public PropertyValuePointProvider(IProperty property) {
		super();
		this.property = property;
	}

	public IProperty getProperty() {
		return property;
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

	public IClusterizationPoint<T> createPoint(final Object value) {
		if (value == null) {
			final IRelation relation = new HasValueRelation(this.property,
					value) { // XXX: value can only be null here

				public boolean accept(Object element) {
					return !ValueUtils.hasValue(property, element);
				}
			};
			final RelationClusterizationPoint<T> relationClusterizationPoint = new RelationClusterizationPoint<T>(
					relation);
			relationClusterizationPoint.setHideEmpty(true);
			relationClusterizationPoint.setFactory(presentationFactory);
			relationClusterizationPoint.setVisible(true);
			relationClusterizationPoint.setOwner(this);
			return relationClusterizationPoint;
		}
		final IRelation relation = new HasValueRelation(this.property, value);
		final RelationClusterizationPoint<T> relationClusterizationPoint = new RelationClusterizationPoint<T>(
				relation);
		relationClusterizationPoint.setHideEmpty(true);
		relationClusterizationPoint.setFactory(presentationFactory);
		relationClusterizationPoint.setVisible(true);
		relationClusterizationPoint.setOwner(this);
		return relationClusterizationPoint;
	}

	public Collection<? extends Object> getGroup(T o) {
		Collection<Object> values = (Collection<Object>) PropertyAccess
				.getValues(property, o);
		if (groupByNone && values.isEmpty()) {
			values = Collections.singleton((Object) null);
		}
		return values;
	}

	public Object getPresentationObject(Object o) {
		return o;
	}

	public void registerAdapter(Class<?> c, Object object) {
		this.adapters.put(c, object);
	}

	public Object getAdapter(Class<?> adapter) {
		return this.adapters.get(adapter);
	}

	public void setPresentationFactory(IPresentationFactory pfactory) {
		this.presentationFactory = pfactory;
	}

	boolean groupByNone;

	public void setGroupByNone(boolean b) {
		groupByNone = b;
	}

	@Override
	public boolean adapt(Query query) {
		if (property.getId().length() > 0) {
			query.setGroupBy(property.getId());
			return true;
		}
		return false;
	}

}