package com.onpositive.semantic.model.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;

import com.onpositive.commons.platform.registry.IAdaptable2;
import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.relation.HasValueTransientRelation;
import com.onpositive.semantic.model.api.relation.IRelation;

public class PropertyValueHierarchicalPointProvider<T> extends
		AbstractClusterizationPointProvider<T> implements IAdaptable2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final IProperty property;

	private Object value;

	private IdentityHashMap<Class<?>, Object> adapters;

	private IPresentationFactory presentationFactory;

	public PropertyValueHierarchicalPointProvider(IProperty property) {
		super();
		this.property = property;
		adapters = new IdentityHashMap<Class<?>, Object>();
	}

	public PropertyValueHierarchicalPointProvider(
			IProperty property2, Object o,
			IdentityHashMap<Class<?>, Object> adapters2) {
		this.property = property2;
		this.value = o;
		this.adapters = adapters2;
	}

	@SuppressWarnings("unchecked")
	public ISetDelta<IClusterizationPoint<T>> processDelta(ISetDelta<T> delta,
			Collection<IClusterizationPoint<T>> currentPoints,
			Collection<T> currentElements) {
		final HashSet<Object> ss = new HashSet<Object>();

		for (final T o : currentElements) {
			Collection<Object> values = (Collection<Object>) PropertyAccess.getValues(property,o);
			if (this.value == null) {
				if (values.isEmpty()) {
					ss.add(o);
				}				
			} else {
				if (values.contains(value)) {
					ss.add(o);
				}
			}
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
		final IRelation relation = new HasValueTransientRelation(this.property, o);
		final RelationClusterizationPoint<T> relationClusterizationPoint = new RelationClusterizationPoint<T>(
				new PropertyValueHierarchicalPointProvider<T>(property,o,adapters),
				relation);
		relationClusterizationPoint.setFactory(presentationFactory);
		relationClusterizationPoint.setHideEmpty(true);
		relationClusterizationPoint.setVisible(true);
		relationClusterizationPoint.setOwner(this);
		return relationClusterizationPoint;
	}	public Object getPresentationObject(Object o) {
		return o;
	}

	public void registerAdapter(Class c, Object object) {
		this.adapters.put(c, object);
	}

	public Object getAdapter(Class adapter) {
		return this.adapters.get(adapter);
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyValueHierarchicalPointProvider other = (PropertyValueHierarchicalPointProvider) obj;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.getId().equals(other.property.getId()))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public void setPresentationFactory(IPresentationFactory factory) {
		this.presentationFactory=factory;
	}

	public IProperty getProperty() {
		return property;
	}

}