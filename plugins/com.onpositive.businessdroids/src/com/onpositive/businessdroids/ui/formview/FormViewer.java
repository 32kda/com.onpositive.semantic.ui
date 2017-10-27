package com.onpositive.businessdroids.ui.formview;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Point;
import android.view.View;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.IFieldGroup;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.dataview.ImageProviderService;
import com.onpositive.businessdroids.ui.dataview.renderers.IFieldRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.ViewRendererService;

public class FormViewer extends AbstractViewer {

	public FormViewer(Context context) {
		super(context);
	}

	protected ViewRendererService service;
	protected IFieldGroup fields;
	protected Object input;

	protected Map<IField, View> buildViews(IFieldGroup g) {
		IField[] fields2 = fields.getFields();
		HashMap<IField, View> pp = new HashMap<IField, View>();
		for (int a = 0; a < fields2.length; a++) {
			IFieldRenderer renderer = service.getRenderer(fields2[a]);
			Object propertyValue = fields2[a].getPropertyValue(input);
			View renderField = renderer.renderField(fields2[a], propertyValue,
					this, input);
			pp.put(fields2[a], renderField);
		}
		IFieldGroup[] childGroups = fields.getChildGroups();
		for (IFieldGroup gr : childGroups) {
			Map<IField, View> buildViews = buildViews(gr);
			pp.putAll(buildViews);
		}
		return pp;
	}
	
	Point basicMeasure(IFieldGroup gr,Map<IField,View> views,boolean isVertical,int width){
		IField[] fields2 = gr.getFields();
		int height = 0;
		int mW=0;
		for (IField f:fields2){
			View view = views.get(f);
			view.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int measuredWidth = view.getMeasuredWidth();
			int measuredHeight = view.getMeasuredHeight();
			height+=measuredHeight;
			mW=isVertical?Math.max(mW, measuredWidth):(mW+measuredWidth);
		}		
		return null;		
	}
	
	@Override
	protected void initView() {
		int width = getWidth();
		int height = getHeight();
		if (width <= 0 || height <= 0) {
			return;
		}
		boolean isHoriz = width > height;		
		inited = true;
	}

	@Override
	public ImageProviderService getImageProviderService() {
		return null;
	}

	@Override
	public ViewRendererService getViewRendererService() {
		return null;
	}
}
