package com.onpositive.businessdroids.ui.dataview.renderers;

import android.view.View;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.IViewer;

public interface IRecycleAwareFieldRenderer extends IFieldRenderer{

	void viewRecycled(View renderedField,IField column, Object fieldValue,
			IViewer table, Object object);
}
