package com.onpositive.businessdroids.ui.dataview.renderers;

import java.util.HashMap;
import java.util.Map;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.IFieldImageProvider;
import com.onpositive.businessdroids.ui.dataview.Group;


@SuppressWarnings("rawtypes")
public class ViewRendererService {

	protected Map<IField, IFieldRenderer> renderers = new HashMap<IField, IFieldRenderer>();
	protected Map<Class, IFieldRenderer> classRenderers = new HashMap<Class, IFieldRenderer>();

	protected Map<IField, IFieldRenderer> groupFieldRenderers = new HashMap<IField, IFieldRenderer>();
	protected Map<Class, IFieldRenderer> groupClassRenderers = new HashMap<Class, IFieldRenderer>();

	protected IFieldImageProvider groupImageProvider;// = new BasicImageProvider();

	protected IFieldRenderer defaultRenderer = new ImageProxyRenderer(
			new StringRenderer());
	
	

	public IFieldRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	public void setDefaultRenderer(IFieldRenderer defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	public void registerRenderer(IField field, IFieldRenderer renderer) {
		this.renderers.put(field, renderer);
	}

	public void registerClassRenderer(Class clazz, IFieldRenderer renderer) {
		this.classRenderers.put(clazz, renderer);
	}

	public void registerGroupClassRenderer(Class clazz, IFieldRenderer renderer) {
		this.groupClassRenderers.put(clazz, renderer);
	}

	public void registerGroupFieldRenderer(IField field, IFieldRenderer renderer) {
		this.groupFieldRenderers.put(field, renderer);
	}

	public IFieldRenderer getClassRenderer(Class type) {
		IFieldRenderer fieldRenderer = this.defaultRenderer;
		while (type != Object.class) {
			fieldRenderer = this.classRenderers.get(type);
			if (fieldRenderer != null) {
				return fieldRenderer;
			}
			type = type.getSuperclass();
		}
		return fieldRenderer;
	}

	public IFieldRenderer getRenderer(IField field) {
		IFieldRenderer fieldRenderer = this.renderers.get(field);
		if (fieldRenderer == null) {
			Class<?> type = field.getType();
			while (type!=null&&type != Object.class) {
				fieldRenderer = this.classRenderers.get(type);
				if (fieldRenderer != null) {
					return fieldRenderer;
				}
				type = type.getSuperclass();
			}
		}
		if (fieldRenderer == null) {
			fieldRenderer = this.defaultRenderer;
		}
		return fieldRenderer;
	}

	public IFieldRenderer getGroupRenderer(IColumn column, Group group) {
		IColumn field = column;
		Object key = group.getKey();
		if (field.isCaption()) {
			IField groupField = group.getColumn();
			IFieldRenderer groupRenderer = this.groupFieldRenderers
					.get(groupField);
			if (groupRenderer == null) {
				groupRenderer = this.groupClassRenderers
						.get(key != null ? key.getClass()
								: String.class);
			}
			if (groupRenderer == null) {
				groupRenderer = this.getRenderer(groupField);
			}
			if (groupRenderer instanceof ImageProxyRenderer) {
				groupRenderer = ((ImageProxyRenderer) groupRenderer)
						.getFieldRenderer();
			}
			if (this.groupImageProvider != null) {
				ImageProxyRenderer proxyRenderer = new ImageProxyRenderer(
						groupRenderer, this.groupImageProvider);
				return proxyRenderer;
			} else {
				return groupRenderer;
			}
		}
		IFieldRenderer groupRenderer = this.groupFieldRenderers.get(field);
		if (groupRenderer == null) {
			Object propertyValue = group.getPropertyValue(field);
			//FIXME POSSIBLE ISSUE HERE (VIEW REUSE)
			groupRenderer = this.groupClassRenderers.get(propertyValue!=null?propertyValue.getClass():String.class);
		}
		if (groupRenderer == null) {
			groupRenderer = this.getRenderer(field);
		}
		return groupRenderer;
	}

}
