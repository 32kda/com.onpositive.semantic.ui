package com.onpositive.semantic.model.api.relation;

import java.util.Collections;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;
import com.onpositive.semantic.model.api.realm.IDescribableToQuery;

public class HasValueRelation extends PropertyRelation implements
		IDescribableToQuery {

	private final class Lookup implements IPropertyProvider {
		public IProperty getProperty(Object obj, String name) {
			if (name!=null&&name.equals(property.getId())) {

				final AbstractRealmProperty pr = new AbstractRealmProperty(
						(IProperty) HasValueRelation.this.property, this) {

					public Object getValue(Object obj) {
						return value;
					}

				};
				return (IProperty) pr;
			}
			// } else {
			// final IProperty property2 =
			// ((IProperty)HasValueRelation.this.property)
			// .getPropertyProvider().getProperty(
			// HasValueRelation.this.value, name);
			// if (property2 != null) {
			// final IMassPropertyProvider adapter=
			// DefaultMetaKeys.getService(property2,
			// IMassPropertyProvider.class);
			// if (adapter != null) {
			// return adapter.getProperty();
			// }
			// }
			// }
			// TODO FIXME
			return null;
		}

		public Iterable<IProperty> getProperties(Object obj) {
			return Collections.emptySet();
		}
	}

	private final Object value;

	public HasValueRelation(IProperty property2, Object value) {
		super(property2);
		this.value = value;
	}

	public boolean accept(Object element) {
		return ValueUtils.hasValue(property, element, this.value);
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
	public Object getAdapter(Class adapter) {
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
		final HasValueRelation other = (HasValueRelation) obj;
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
		if (value == null) {
			return "<none>";
		}
		return this.value;
	}

	public String toString() {
		return this.property.getId() + "==" + this.value;
	}

	@Override
	public boolean adapt(Query query) {
		query.addFilter(new QueryFilter(property.getId(), value,
				QueryFilter.FILTER_CONTAINS));
		return true;
	}

}