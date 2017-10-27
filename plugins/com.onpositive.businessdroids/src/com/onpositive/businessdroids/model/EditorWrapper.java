package com.onpositive.businessdroids.model;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.editors.IFieldEditor;

public class EditorWrapper {

	protected IFieldEditor editor;
	protected IField property;
	protected Object object;

	public EditorWrapper(IFieldEditor editor, IField property, Object object) {
		super();
		this.editor = editor;
		this.property = property;
		this.object = object;

		init();
	}

	protected void init() {
		Object propertyValue = property.getPropertyValue(object);
		editor.setValue(propertyValue);
	}

	public void commit() {
		Object val = editor.getValue();
		property.setPropertyValue(object, val);
	}
}
