package com.onpositive.businessdroids.ui.dataview.renderers;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.IViewer;

import android.view.View;


public interface IFieldRenderer {

	public View renderField(IField column, Object fieldValue,
			IViewer table, Object object);

	public void setPropValueToView(View renderedField, IField column,
			Object fieldValue, IViewer table, Object parenObj);

}
