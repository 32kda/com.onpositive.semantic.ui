package com.onpositive.semantic.model.api.relation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;

public class HasValueTransientRelation extends PropertyRelation {

	private final class Lookup implements IPropertyProvider {
		public IProperty getProperty(Object obj, String name) {
			if (name!=null&&name.equals(IProperty.NAME_PROPERTY_ID)) {

				final AbstractRealmProperty pr = new AbstractRealmProperty(
						(IProperty) HasValueTransientRelation.this.property,
						this) {
					// public int getValueCount(Object obj) {
					// return 1;
					// }


					public Object getValue(Object obj) {
						return value;
					}

					// public boolean isReadOnly() {
					// return false;
					// }

				};
				return (IProperty) pr;
			}
//			else {
//				final IProperty property2 = ((IProperty) HasValueTransientRelation.this.property)
//						.getPropertyProvider().getProperty(
//								HasValueTransientRelation.this.value, name);
//				if (property2 != null) {
//					final IMassPropertyProvider adapter = DefaultMetaKeys.getService(property2, IMassPropertyProvider.class);
//					if (adapter != null) {
//						return adapter.getProperty();
//					}
//				}
//			}
			//TODO FIX ME
			return null;
		}

		public Iterable<IProperty> getProperties(Object obj) {
			return Collections.emptySet();
		}
	}

	private final Object value;

	public HasValueTransientRelation(IProperty property2, Object value) {
		super(property2);
		this.value = value;
	}

	public boolean accept(Object element) {
		boolean hasValue = ValueUtils
				.hasValue(property, element, this.value);
		if (hasValue) {
			return true;
		}
		HashSet<Object> current = new HashSet<Object>();
		fillAlValues(element, current);
		if (value instanceof Set) {
			return current.containsAll((Set<?>) this.value);
		}
		return current.contains(this.value);
	}

	@SuppressWarnings("rawtypes")
	private void fillAlValues(Object element, HashSet<Object> current) {
		Object values = property.getValue(element);
		if (values instanceof Iterable<?>) {
			Iterable i = (Iterable) values;
			for (Object o : i) {
				if (o==null){
					continue;
				}
				if (!current.contains(o)) {
					current.add(o);
					fillAlValues(o, current);
				}
			}
			return;
		}
		if (values != null) {
			if (!current.contains(values)) {
				current.add(values);
				fillAlValues(values, current);
			}
		}
	}

	public Object getValue() {
		return this.value;
	}

	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertyProvider.class && property instanceof IProperty) {
			return new Lookup();
		}
		return DefaultMetaKeys.getService(property, adapter);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final HasValueTransientRelation other = (HasValueTransientRelation) obj;
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	public Object getPresentationObject() {
		return this.value;
	}

	public String toString() {
		return this.property.getId() + "==" + this.value;
	}

}