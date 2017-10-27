package com.onpositive.businessdroids.ui.dataview.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.IAsyncFieldImageProvider;
import com.onpositive.businessdroids.ui.IFieldImageProvider;
import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

public class ImageProxyRenderer extends AbstractBackgroundAwareFieldRenderer
		implements IKnowsLongestValue {

	protected final IFieldRenderer fieldRenderer;
	protected final IFieldImageProvider imageProvider;
	
	protected ScaleType scaleType=ScaleType.FIT_CENTER;
	
	boolean isImageAtRight;

	public boolean isImageAtRight() {
		return isImageAtRight;
	}

	public void setImageAtRight(boolean isImageAtRight) {
		this.isImageAtRight = isImageAtRight;
	}

	public ScaleType getScaleType() {
		return scaleType;
	}

	public void setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
	}

	public ImageProxyRenderer(IFieldRenderer fieldRenderer) {
		this(fieldRenderer, null);
	}

	public ImageProxyRenderer(IFieldRenderer fieldRenderer,
			IFieldImageProvider imageProvider) {
		this.fieldRenderer = fieldRenderer;
		this.imageProvider = imageProvider;
	}

	public View renderField1(IField column, Object fieldValue,
			IViewer tableModel, Object object) {
		IFieldImageProvider imageProvider = getImageProvider(column, tableModel);
		Context context = tableModel.getContext();
		if (imageProvider == null) {
			return this.fieldRenderer.renderField(column, fieldValue,
					tableModel, object);
		}
		Bitmap image = imageProvider.getImage(context, object, column,
				fieldValue);
		// if (image != null) {
		LinearLayout layout = new LinearLayout(context);
		ImageView imageView = new ImageView(context);
		imageView.setScaleType(scaleType);		
		imageView.setImageBitmap(image);

		imageView.setPadding(0,1, 2, 1); // TODO make padding to be
											// obtained from theme/settings
											// or calculated, get rid of
											// these numbers
		int height = image != null ? image.getHeight() : 1;
		int width = image != null ? image.getWidth() : 1;

		View renderedField = this.fieldRenderer.renderField(column, fieldValue,
				tableModel, object);
		LinearLayout.LayoutParams imageLayoutParams;
		LinearLayout.LayoutParams contentLayoutParams;
		try{
		renderedField.measure(
				View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		}catch (Exception e) {
			renderedField.measure(
					View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
		}
		renderedField.setPadding(0, 0, 0, 0);
		Point fixedImageSize = getFixedImageSize(column);
		if (fixedImageSize != null) {
			Point size = fixedImageSize;
			imageLayoutParams = new LinearLayout.LayoutParams(size.x, size.y, 0);
			contentLayoutParams = new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 2);
			layout.setMinimumHeight(size.y+2);
			
		} else if (isFitImageToContent(column)) {
			int measuredHeight = renderedField.getMeasuredHeight();
			measuredHeight=Math.max(measuredHeight, tableModel.getCurrentTheme().getMinListItemHeight(context)-2);
			imageLayoutParams = new LinearLayout.LayoutParams(
					measuredHeight * width / height,
					measuredHeight, 0);
			contentLayoutParams = new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 2);
			
		} else {
			imageLayoutParams = new LinearLayout.LayoutParams(width, height,0);
			contentLayoutParams = new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 2);
			
		}
		imageLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		contentLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		contentLayoutParams.weight=1;
		if (isImageAtRight){
			layout.addView(renderedField, contentLayoutParams);
			layout.addView(imageView, imageLayoutParams);			
		}
		else{
			layout.addView(imageView, imageLayoutParams);
			layout.addView(renderedField, contentLayoutParams);	
		}
		
		return layout;		
	}

	public IFieldImageProvider getImageProvider(IField column,
			IViewer tableModel) {
		IFieldImageProvider imageProvider = this.imageProvider;
		if (imageProvider == null) {
			imageProvider = tableModel.getImageProviderService()
					.getImageProvider(column);
		}
		return imageProvider;
	}

	public boolean isFitImageToContent(IField column) {
		if (column instanceof IColumn) {
			return ((IColumn) column).isFitImageToContent();
		}
		return true;
	}

	public Point getFixedImageSize(IField column) {
		if (column instanceof IColumn) {
			return ((IColumn) column).getFixedImageSize();
		}
		return null;
	}


	public void setPropValueToView1(View renderedField, IField field,
			Object propertyValue, IViewer tableModel, Object parenObj) {
		if (renderedField == null) {
			return;
		}
		IFieldImageProvider imageProvider = tableModel
				.getImageProviderService().getImageProvider(field);
		if (imageProvider == null) {
			this.fieldRenderer.setPropValueToView(renderedField, field,
					propertyValue, tableModel, parenObj);
			return;
		}
		Bitmap image = imageProvider.getImage(renderedField.getContext(),
				parenObj, field, propertyValue);
		if (image != null) {
			if (!(renderedField instanceof ViewGroup)) {
				throw new AssertionError("Given view isn't ViewGroup");
			}
			View imageView = ((ViewGroup) renderedField).getChildAt(0);
			if (!(imageView instanceof ImageView)) {
				throw new AssertionError(
						"imageView view isn't ImageView class instance");
			}
			((ImageView) imageView).setImageBitmap(image);
			View contentView = ((ViewGroup) renderedField).getChildAt(1);
			this.fieldRenderer.setPropValueToView(contentView, field,
					propertyValue, tableModel, parenObj);
			return;
		} else {
			View imageView = ((ViewGroup) renderedField).getChildAt(0);
			((ImageView) imageView).setImageBitmap(null);
			View contentView = ((ViewGroup) renderedField).getChildAt(1);
			this.fieldRenderer.setPropValueToView(contentView, field,
					propertyValue, tableModel, parenObj);
		}
	}

	@Override
	public Object longestValue(IColumn column, StructuredDataView model) {
		if (this.fieldRenderer instanceof IKnowsLongestValue) {
			IKnowsLongestValue ls = (IKnowsLongestValue) this.fieldRenderer;
			if (column.getFixedImageSize() != null) {
				return ls.longestValue(column, model);
			} else if (column.isFitImageToContent()) {
				return ls.longestValue(column, model);
			}
		}
		return null;
	}

	public IFieldRenderer getFieldRenderer() {
		return this.fieldRenderer;
	}

	@Override
	protected void basicUpdate(View v, IField column, Object fieldValue,
			IViewer table, Object object) {
		setPropValueToView1(v, column, fieldValue, table, object);
	}

	@Override
	protected void updateWithDetails(View v, IField column, Object fieldValue,
			IViewer table, Object object) {
		// do nothing here
	}

	@Override
	protected View renderBasic(IField column, Object fieldValue, IViewer table,
			Object object) {
		return renderField1(column, fieldValue, table, object);
	}

	public void dataFetched2(View v, IColumn column, Object fv, IViewer table,
			Object object) {
		if (column == null) {
			return;
		}
		IFieldImageProvider imageProvider = table.getImageProviderService()
				.getImageProvider(column);
		if (imageProvider == null) {
			return;
		}
		Bitmap image = imageProvider.getImage(v.getContext(), object, column,
				fv);
		
		if (image != null) {
			if (!(v instanceof ViewGroup)) {
				throw new AssertionError("Given view isn't ViewGroup");
			}
			View imageView = ((ViewGroup) v).getChildAt(0);
			if (!(imageView instanceof ImageView)) {
				throw new AssertionError(
						"imageView view isn't ImageView class instance");
			}
			((ImageView) imageView).setImageBitmap(image);
			return;
		} else {
			View imageView = ((ViewGroup) v).getChildAt(0);
			((ImageView) imageView).setImageBitmap(null);
		}
	}
	@Override
	public void dataFetched(View v, IColumn column, Object fv, IViewer table,
			Object object) {		
	}

	@Override
	public boolean hasAllInfo(IField column, Object fieldValue, IViewer v,
			Object object) {
		IFieldImageProvider imageProvider2 = getImageProvider(column, v);
		if (imageProvider2 != null&&object!=null) {
			if (imageProvider2 instanceof IAsyncFieldImageProvider) {
				IAsyncFieldImageProvider m = (IAsyncFieldImageProvider) imageProvider2;
				return m.hasAllInfo(column, fieldValue, v, object);
			}
		}
		return true;
	}

	@Override
	protected Object getUpdateKey(IField column, Object fieldValue,
			IViewer table, Object parenObj) {
		IFieldImageProvider imageProvider2 = getImageProvider(column, table);
		if (imageProvider2 != null) {
			if (imageProvider2 instanceof IAsyncFieldImageProvider) {
				IAsyncFieldImageProvider m = (IAsyncFieldImageProvider) imageProvider2;
				return m.getUpdateKey(column, fieldValue, table, parenObj);
			}
		}
		return super.getUpdateKey(column, fieldValue, table, parenObj);
	}

	HashSet<IAsyncFieldImageProvider> mm = new HashSet<IAsyncFieldImageProvider>();

	@Override
	protected synchronized void doGet(final Map<Object, ArrayList<View>> select) {

		for (final Object o : select.keySet()) {
			mm.clear();
			for (final View v : select.get(o)) {
				final IField c = (IField) v.getTag(COLUMN_TAG_INDEX);
				final IViewer t = (IViewer) v.getTag(TABLE_TAG_INDEX);
				final IAsyncFieldImageProvider imageProvider2 = (IAsyncFieldImageProvider) getImageProvider(
						c, t);
				if (!mm.contains(imageProvider2)) {
					AsyncTask<Object,Object,Object>z=new AsyncTask<Object, Object, Object>(){

						@Override
						protected Object doInBackground(Object... params) {
							imageProvider2.doGet(o, c, t);
							return null;
						}
						
						@Override
						protected void onPostExecute(Object result) {
							for (final View v : select.get(o)) {
								Object tag = v.getTag(FIELD_VALUE_TAG_INDEX);
								Object ot = v.getTag(OBJECT_TAG_INDEX);
								dataFetched2(v, (IColumn) c,tag, t, ot);
							}
						}
						
					};
					z.execute();
					mm.add(imageProvider2);
				}
				break;
			}
		}
	}

	@Override
	protected Map<Object, ArrayList<View>> select(
			LinkedHashMap<Object, ArrayList<View>> vs2) {
		return new HashMap<Object, ArrayList<View>>(vs2);
	}

}
