package com.onpositive.businessdroids.ui.dataview.renderers;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class ImageRenderer implements IFieldRenderer, IKnowsLongestValue {

	Point fixedSize;

	public Point getFixedSize() {
		return fixedSize;
	}

	public void setFixedSize(Point fixedSize) {
		this.fixedSize = fixedSize;
	}

	@Override
	public View renderField(IField column, Object fieldValue, IViewer table,
			Object object) {
		ImageView imageView = new ImageView(table.getContext());
		imageView.setScaleType(ScaleType.FIT_CENTER);

		imageView.setAdjustViewBounds(true);
		if (fieldValue instanceof Bitmap) {
			imageView.setImageBitmap((Bitmap) fieldValue);
		} else if (fieldValue instanceof Drawable) {
			imageView.setImageDrawable((Drawable) fieldValue);
		}
		imageView.setPadding(0, 0, 2, 0); // TODO make padding to be obtained
											// from theme/settings or
											// calculated, get rid of these
											// numbers
		if (fixedSize != null) {
			imageView.setLayoutParams(new LayoutParams(
					fixedSize.x,
					fixedSize.y));
			imageView.setMaxHeight(fixedSize.y);
			imageView.setMaxWidth(fixedSize.x);
		} else {
			imageView.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT));
		}
		
		return imageView;
	}

	@Override
	public void setPropValueToView(View renderedField, IField column,
			Object fieldValue, IViewer table, Object parenObj) {
		if (renderedField instanceof ImageView) {
			if (fieldValue instanceof Bitmap) {
				((ImageView) renderedField).setImageBitmap((Bitmap) fieldValue);
			} else if (fieldValue instanceof Drawable) {
				((ImageView) renderedField)
						.setImageDrawable((Drawable) fieldValue);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Object longestValue(IColumn column, StructuredDataView model) {
		return null;
	}

}
