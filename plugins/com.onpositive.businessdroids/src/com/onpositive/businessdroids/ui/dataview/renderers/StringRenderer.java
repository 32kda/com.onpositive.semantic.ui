package com.onpositive.businessdroids.ui.dataview.renderers;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.dataview.ImageProviderService;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.BasicRecordRenderer;

/**
 * Renders field value with {@link TextView} containing field value's
 * <code>toString</code> val
 * 
 * @author 32kda
 */
public class StringRenderer implements IFieldRenderer, IKnowsLongestValue,
		IStringRenderer,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int PROB_COUNT = BasicRecordRenderer.PROB_COUNT;

	private int maxCount = -1;

	private int style = -1;

	private boolean lC;

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public void setLinksClickable(boolean lc) {
		this.lC = lc;
	}

	@Override
	public View renderField(IField column, Object fieldValue, IViewer dataView,
			Object object) {
		TextView textView = new TextView(dataView.getContext()) {

			@Override
			public boolean dispatchTouchEvent(MotionEvent event) {
				if (dispatch(event, this)) {
					return super.dispatchTouchEvent(event);
				}
				return false;
			}

		};
		if (lC) {
			textView.setLinksClickable(true);
			textView.setMovementMethod(LinkMovementMethod.getInstance());
			textView.setFocusable(false);
			// textView.setFocusableInTouchMode(true);
		}
		if (maxCount != -1) {
			textView.setMaxLines(maxCount);
			textView.setEllipsize(TruncateAt.MARQUEE);
			if (maxCount == 1) {
				textView.setSingleLine();
			}
		}
		if (style != -1) {
			textView.setTypeface(textView.getTypeface(), style);
		}
		CharSequence stringFromValue = fieldValue == null ? "" : this
				.getStringFromValue(fieldValue, dataView, object, column);
		textView.setText(stringFromValue);
		textView.setTextColor(dataView.getCurrentTheme().getRecordFontColor());
		if (!ImageProviderService.isCaption(column)) {
			textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		}
		textView.setEnabled(false);
		return textView;
	}

	protected boolean dispatch(MotionEvent event, TextView widget) {
		if (lC) {

			Layout layout = widget.getLayout();

//			int padding = widget.getTotalPaddingTop()
//					+ widget.getTotalPaddingBottom();
//			int areatop = widget.getScrollY();
			if (!(widget.getText() instanceof Spannable)) {
				return true;
			}
			Spannable buffer = (Spannable) widget.getText();
			int x = (int) event.getX();
			int y = (int) event.getY();

			x -= widget.getTotalPaddingLeft();
			y -= widget.getTotalPaddingTop();

			x += widget.getScrollX();
			y += widget.getScrollY();
			int line = layout.getLineForVertical(y);
			Rect rect2 = new Rect();
			Rect rect = rect2;
			layout.getLineBounds(line,rect);
			int gravity = widget.getGravity();
			int lm=(int) layout.getLineWidth(line);
			;
			//int i = rect.right-rect.left-lm;
			Gravity.apply(gravity, lm, rect.height(), rect, rect2);
			rect=rect2;
			if (x>rect.right){
				return false;
			}
			if (x<rect.left){
				return false;
			}
			int off = layout.getOffsetForHorizontal(line, x);

			ClickableSpan[] link = buffer.getSpans(off, off,
					ClickableSpan.class);

			if (link.length != 0) {
				return true;
			} else {
				return false;
			}

		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IStringRenderer#getStringFromValue
	 * (java.lang.Object, com.onpositive.android.dataview.StructuredDataView)
	 */
	@Override
	public CharSequence getStringFromValue(Object fieldValue,
			IViewer tableModel, Object object) {
		if (fieldValue == null) {
			return "";
		}
		if (fieldValue instanceof CharSequence) {
			return (CharSequence) fieldValue;
		}
		if( fieldValue instanceof Date ){
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MMM,dd,yyyy", Locale.US) ;
			return sdf.format(fieldValue) ;
		}
			
		return fieldValue.toString();
	}

	public CharSequence getStringFromValue(Object fieldValue,
			IViewer tableModel, Object object, IField fld) {
		return getStringFromValue(fieldValue, tableModel, object);
	}

	
	@Override
	public void setPropValueToView(View renderedField, IField column,
			Object propertyValue, IViewer tableModel, Object parenObj) {
		if (renderedField instanceof TextView) {
			((TextView) renderedField).setText(propertyValue == null ? ""
					: this.getStringFromValue(propertyValue, tableModel,
							parenObj, column));
		}
	}

	@Override
	public Object longestValue(IColumn column, StructuredDataView model) {
		// Collection<?> content = model.getTableModel().getContent();
		CharSequence maxLength = "";
		// FIXME
		TableModel tableModel = model.getTableModel();
		int min = Math.min(tableModel.getItemCount(), PROB_COUNT);
		for (int a = 0; a < min; a++) {
			Object record = tableModel.getItem(a);
			Object propertyValue = column.getPropertyValue(record);
			CharSequence stringFromValue = this.getStringFromValue(
					propertyValue, model, null);
			if (maxLength.length() < stringFromValue.length()) {
				maxLength = stringFromValue;
			}
		}
		return maxLength;
	}

}
