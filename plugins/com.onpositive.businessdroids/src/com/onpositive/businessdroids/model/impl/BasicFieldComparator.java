package com.onpositive.businessdroids.model.impl;

import java.util.Comparator;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.dataview.Group;
import com.onpositive.businessdroids.ui.dataview.renderers.IFieldRenderer;


public class BasicFieldComparator implements Comparator<Object> {

	protected final IField field;
	protected final boolean ascending;

	public BasicFieldComparator(IField field, boolean ascending) {
		this.field = field;
		this.ascending = ascending;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int compare(Object object1, Object object2) {
		Object value1 = getValue(object1);
		Object value2 = getValue(object2);
		if (value2 == null) {
			if (value1 == null) {
				return 0;
			}
			return -1;
		}
		if (value1 instanceof Comparable) {

			// TODO HANDLE DIFFERENT TYPES
			int result = -1;
			try {
				result = ((Comparable) value1).compareTo(value2);
			} catch (Exception e) {
				result = value1.toString().compareTo(value2.toString());
			}
			if (!this.ascending) {
				result = -result;
			}
			return result;
		} else if (value1 == null) {
			return 1;
		}		
		return 0;
	}

	public Object getValue(Object object1) {
		if (object1 instanceof Group){
			Group gr=(Group) object1;
			IColumn lm = (IColumn) field;
			if (lm.isCaption()){
				return gr.getKey();
			}
			return gr.getPropertyValue(lm);
		}
		return this.field.getPropertyValue(object1);
	}

}
