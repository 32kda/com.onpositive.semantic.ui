package com.onpositive.semantic.ui.android;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.semantic.model.api.access.ClassLoaderResolver;
import com.onpositive.semantic.model.api.property.ExpressionValueProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.ui.generic.Column;

public class ColumnAdapterField implements IField {

	private final Column column;
	private ExpressionValueProperty property;

	public ColumnAdapterField(Column column) {
		this.column = column;
		property = new ExpressionValueProperty(column.getId(),new ClassLoaderResolver(getClass().getClassLoader()));
	}

	@Override
	public Object getPropertyValue(Object object) {
		if (property != null) {
			return property.getValue(object);
		}
		return column.getProperty().getValue(object);
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		PropertyAccess.setValue(column.getProperty(),object,value);		
	}

	@Override
	public boolean isReadOnly(Object object) {
		return !column.isEditable();
	}

	@Override
	public Class<?> getType() {
		return column.getType(null);
	}

	@Override
	public String getId() {
		return column.getId();
	}

	@Override
	public String getTitle() {
		return column.getCaption();
	}

	@Override
	public String[] getCategories() {
		return new String[0];
	}


}
