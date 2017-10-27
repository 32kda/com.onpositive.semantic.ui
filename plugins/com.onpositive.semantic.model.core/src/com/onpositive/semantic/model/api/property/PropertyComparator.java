package com.onpositive.semantic.model.api.property;

import java.util.Comparator;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.labels.LabelAccess;

public final class PropertyComparator implements Comparator<Object>,
		IValueListener<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected IProperty relation;
	protected boolean inverse;

	public boolean isInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
		ObjectChangeManager.markChanged(this);
	}

	public PropertyComparator(IProperty relation) {
		super();
		this.relation = relation;
		PropertyAccess.addPropertyStructureListener(relation, this);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compare(Object arg0, Object arg1) {
		Object value = relation.getValue(arg0);
		Object value1 = relation.getValue(arg1);
		int compareTo = 0;
		try {

			if (value != null) {
				compareTo = ((Comparable) value).compareTo(value1);
			} else if (value1 != null) {
				compareTo = -((Comparable) value1).compareTo(value);
			} else {
				compareTo = 0;
			}
		} catch (Exception e) {
		}
		if (compareTo == 0) {
			compareTo = labelCompare(value, value1);
		}
		return inverse ? -compareTo : compareTo;
	}

	public int labelCompare(Object value, Object value1) {
		return LabelAccess.getLabel(value).compareTo(
				LabelAccess.getLabel(value1));
	}

	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		ObjectChangeManager.markChanged(this);
	}
}