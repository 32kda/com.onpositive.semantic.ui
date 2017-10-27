package com.onpositive.businessdroids.ui.dataview;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.impl.AbstractComputableField;
import com.onpositive.businessdroids.model.impl.BasicTableModel;
import com.onpositive.businessdroids.model.impl.Column;
import com.onpositive.businessdroids.model.impl.FieldLabel;
import com.onpositive.businessdroids.model.impl.StaticFieldTitle;
import com.onpositive.businessdroids.model.impl.StaticFieldValue;
import com.onpositive.businessdroids.model.impl.ValueOfField;
import com.onpositive.businessdroids.model.impl.pojo.POJOFactory;
import com.onpositive.businessdroids.ui.IFieldImageProvider;
import com.onpositive.businessdroids.ui.dataview.handlers.ITablePartClickHandler;
import com.onpositive.businessdroids.ui.dataview.renderers.IEditableColumn;
import com.onpositive.businessdroids.ui.dataview.renderers.ImageProxyRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.MultiFieldRenderer;

public class DataViewFactory {

	public static StructuredDataView createEmptyPOJOView(Context ctx,
			Class<?> cl, String... ids) {
		BasicTableModel bm = new BasicTableModel(
				new POJOFactory(cl).createColumns(ids));
		return new StructuredDataView(ctx, bm);
	}

	@SuppressWarnings("unchecked")
	public static <T> StructuredDataView createPOJOView(Context ctx,
			Class<T> cl, Collection<T> content, String... ids) {
		BasicTableModel bm = new BasicTableModel(
				new POJOFactory(cl).createColumns(ids));
		bm.addAll((Collection<Object>) content);
		return new StructuredDataView(ctx, bm);
	}

	public static StructuredDataView createOneObjectView(Context ctx,
			Object object, IField... fld) {
		Column label = new Column(new FieldLabel());
		Column value = new Column(new ValueOfField(object, "value"));
		BasicTableModel tm = new BasicTableModel(new IColumn[] { label, value });
		tm.addAll(Arrays.asList(fld));
		StructuredDataView view = new StructuredDataView(ctx, tm);
		view.setHeaderVisible(false);
		return view;
	}
	
	public static IColumn[] toColumns(IField... fields) {
		IColumn[] columns = new IColumn[fields.length];
		for (int a = 0; a < columns.length; a++) {
			if (fields[a] instanceof IColumn) {
				columns[a] = (IColumn) fields[a];
			} else {
				columns[a] = new Column(fields[a]);
			}
		}
		return columns;
	}

	public static IColumn createMultiColumn(IFieldImageProvider ip,
			final IField... fields) {
		Column c = new Column(
				new AbstractComputableField(Object.class, "", "") {

					@Override
					public Object getPropertyValue(Object object) {
						return fields[0].getPropertyValue(object);
					}
				});
		IColumn[] columns = toColumns(fields);
		for (IColumn ca : columns) {
			if (ca instanceof IEditableColumn) {
				IEditableColumn m = (IEditableColumn) ca;
				m.setCaption(true);
			}
		}

		c.setCaption(true);
		if (ip != null) {
			c.setImageProvider(ip);
			c.setFitImageToContent(true);
			c.setRenderer(new ImageProxyRenderer(
					new MultiFieldRenderer(columns)));
		} else {
			c.setRenderer(new MultiFieldRenderer(columns));
		}
		return c;
	}

	public static StructuredDataView createList(Context ctx,
			Collection<? extends Object> obj, IField groupField,
			IFieldImageProvider provider, IField... listFields) {
		BasicTableModel tm = new BasicTableModel(
				new IColumn[] { createMultiColumn(provider, listFields) });
		tm.addAll(obj);
		if (groupField != null) {
			tm.setCurrentGrouping(null);
			tm.setGroupSortField(groupField);
		}
		StructuredDataView ds = new StructuredDataView(ctx, tm);
		ds.setActionBarVisible(false);
		ds.setHeaderVisible(false);
		ds.setFooterVisible(false);
		return ds;

	}
	
	public static StructuredDataView createTable(Context ctx,
			Collection<? extends Object> obj, IField groupField,
			IFieldImageProvider provider, IField... listFields) {
		BasicTableModel tm = new BasicTableModel( toColumns(listFields) );
		tm.addAll(obj);
		if (groupField != null) {
			tm.setCurrentGrouping(null);
			tm.setGroupSortField(groupField);
		}
		StructuredDataView ds = new StructuredDataView(ctx, tm);
		ds.setActionBarVisible(false);
		ds.setHeaderVisible(false);
		ds.setFooterVisible(false);
		return ds;

	}
}
