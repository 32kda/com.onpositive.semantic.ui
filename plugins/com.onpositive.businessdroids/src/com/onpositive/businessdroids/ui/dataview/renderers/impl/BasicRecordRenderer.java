package com.onpositive.businessdroids.ui.dataview.renderers.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.dataview.Group;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dataview.renderers.IFieldRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IKnowsLongestValue;
import com.onpositive.businessdroids.ui.dataview.renderers.IRecordRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IRecycleAwareFieldRenderer;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class BasicRecordRenderer implements IRecordRenderer {
	
	protected class ViewContainer extends LinearLayout implements Checkable{

		private boolean checked;
		private final StructuredDataView dataView;

		public ViewContainer(Context context, StructuredDataView dataView ) {
			super(context);
			this.dataView = dataView;
		}

		@Override
		public boolean isChecked() {
			return checked;
		}

		@Override
		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		@Override
		public void toggle() {
			checked = !checked;
		}
		
		public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
			boolean dispatchTouchEvent = super.dispatchTouchEvent(ev);
			if (ev.getX()<dataView.getCurrentTheme().getBaseLeftPadding()+dataView.getCurrentTheme().getIndicatorBound()){
				return false;						
			}
			TableModel tableModel = dataView.getTableModel();
			if( getTag() instanceof MTag ){
				MTag tag = (MTag) getTag() ;				
				tableModel.setSelectedRecord(tag.getRecord()) ;
			}
			
//			String str =
//			  "x = " + ev.getX() + "y = " + ev.getY() + "\n"
//			+ "raw x = " + ev.getRawX() + "raw y = " + ev.getRawY() + "\n"
//			+ this.toString() ;
//			
//			System.out.println( str ) ;
			return dispatchTouchEvent;
		};
		
		public String toString(){
			return getChildCount() > 0 ? getChildAt(0).toString() : "No chidren" ;
		}
		
	}

	// private static final String RECORD_VIEW_ID = "record_view";
	private static final String GROUP_VIEW_ID = "group_view";

	protected static final int MIN_GROUP_HEIGHT = 5;

	public static final int PROB_COUNT = 20;

	public static class MTag {
		protected Object record;
		protected List<IColumn> columns;

		public MTag(Object record, List<IColumn> columns) {
			super();
			this.record = record;
			this.columns = columns;
		}

		public Object getRecord() {
			return record;
		}

		public List<IColumn> getColumns() {
			return columns;
		}
	}

	@Override
	public View renderRecord(final Object record, final StructuredDataView dataView,
			int position, int maxWidth, View convertView) {
		Context context = dataView.getContext();
		ITheme currentTheme = dataView.getCurrentTheme();
		IColumn[] columns = dataView.getColumns();
		final int indicatorBound = currentTheme.getIndicatorBound();
		LinearLayout result;
		int shift = 0;
		
		if (convertView == null) {
			result = new ViewContainer(dataView.getContext(), dataView);
		} else {
			result = (LinearLayout) convertView;
			result.removeAllViews();
		}

		result.setBackgroundColor(Color.WHITE);
		result.setOrientation(LinearLayout.HORIZONTAL);
		int sum = 0;
		int[] columnWidths = new int[columns.length];
		List<IColumn> tagList = new ArrayList<IColumn>();
		for (int i = 0; i < columns.length; i++) {
			int width = dataView.getFieldWidth(i);
			if (width == 0) {
				continue;
			}
			IColumn iColumn = columns[i];
			if (iColumn.getVisible() == IColumn.AUTOMATIC) {
				if (sum + width > maxWidth) {
					width = maxWidth - sum;
				}
				if (width <= 0) {
					continue;
				}
			}
			columnWidths[i] = width;
			sum += width;
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					width, LayoutParams.WRAP_CONTENT);
			Object propertyValue;
			if (record instanceof Group) {
				propertyValue = ((Group) record).getPropertyValue(iColumn);
			} else {
				propertyValue = iColumn.getPropertyValue(record);
			}
			IFieldRenderer renderer;
			if (record instanceof Group) {
				renderer = dataView.getViewRendererService().getGroupRenderer(
						iColumn, (Group) record);
			} else {

				renderer = iColumn.getRenderer();
				if (renderer == null) {
					renderer = dataView.getViewRendererService().getRenderer(
							iColumn);
				}
			}
			View renderedField = renderer.renderField(iColumn, propertyValue,
					dataView, record);
			if (renderedField == null) {
				renderedField = renderer.renderField(iColumn, propertyValue,
						dataView, record);
			}
			if ((i == 0) && dataView.isGrouped()) {
				
				shift += indicatorBound;
			}
			configurePadding(currentTheme, renderedField, shift);
			layoutParams.gravity = Gravity.CENTER;
			layoutParams.weight = 1;
			tagList.add(iColumn);
			result.addView(renderedField, layoutParams);
		}
		int measuredHeight = applyBackground(record, dataView, position,
				context, currentTheme, result, columnWidths);
		if (record instanceof Group) {
			this.adjustGroupPadding(result, measuredHeight, new TextView(
					dataView.getContext()).getTextSize());
			result.setTag(BasicRecordRenderer.GROUP_VIEW_ID);
		} else {
			result.setTag(new MTag(record, tagList));
		}
		
		return result;
	}

	protected int applyBackground(Object record, StructuredDataView dataView,
			int position, Context context, ITheme currentTheme,
			View result, int[] columnWidths) {
		result.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		Drawable bgDrawable;
		int measuredHeight = result.getMeasuredHeight();
		if (record instanceof Group) {
			bgDrawable = currentTheme.getGroupBackgroundDrawable(dataView,
					position, columnWidths, measuredHeight);
		} else {
			bgDrawable = currentTheme.getRecordBackgroundDrawable(dataView,
					position, columnWidths, measuredHeight);
		}
		result.setMinimumHeight((int) currentTheme.getMinListItemHeight(context));
		result.setBackgroundDrawable(bgDrawable);
		return measuredHeight;
	}

	protected void configurePadding(ITheme currentTheme, View renderedField,
			int shift) {
		renderedField.setPadding(currentTheme.getBaseLeftPadding() + shift,
				currentTheme.getBaseTopPadding(),
				currentTheme.getBaseRightPadding(),
				currentTheme.getBaseBottomPadding());
	}

	protected void adjustGroupPadding(LinearLayout result, int measuredHeight,
			float textSize) {
		if (measuredHeight < textSize * 3) {
			int vPadding = Math.round(Math.max(
					(textSize * 3 - measuredHeight) / 2, 1));
			result.setPadding((int) (result.getPaddingLeft() + textSize / 2),
					vPadding, result.getPaddingRight(), vPadding);
		}
	}

	@Override
	public int measureField(StructuredDataView dataView, IColumn column) {
		int maxWidth = 0;
		View renderedField = null;

		IFieldRenderer renderer = column.getRenderer();
		if (renderer == null) {
			renderer = dataView.getRenderer(column);
		}
		if (renderer instanceof IKnowsLongestValue) {
			IKnowsLongestValue ls = (IKnowsLongestValue) renderer;
			Object longestValue = ls.longestValue(column, dataView);
			if (longestValue != null) {
				renderedField = renderer.renderField(column, longestValue,
						dataView, null);
				this.configureView(renderedField, dataView);
				int currWidth = renderedField.getMeasuredWidth();
				return currWidth;
			}
		}
		TableModel tableModel = dataView.getTableModel();
		// FIXME
		int min = Math.min(tableModel.getItemCount(), PROB_COUNT);
		for (int a = 0; a < min; a++) {
			Object record = tableModel.getItem(a);
			Object propertyValue = column.getPropertyValue(record);
			renderedField = renderer.renderField(column, propertyValue,
					dataView, null);
			int currWidth = this.configureView(renderedField, dataView);
			if (currWidth > maxWidth) {
				maxWidth = currWidth;
			}
		}
		return maxWidth;
	}

	protected int configureView(View renderedField, StructuredDataView view) {
		ITheme currentTheme = view.getCurrentTheme();
		renderedField.setPadding(currentTheme.getBaseLeftPadding(),
				currentTheme.getBaseTopPadding(),
				currentTheme.getBaseRightPadding(),
				currentTheme.getBaseBottomPadding());
		renderedField.measure(
				View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int currWidth = renderedField.getMeasuredWidth();
		return currWidth;
	}

	@Override
	public void setRecordToView(View convertView, Object record,
			StructuredDataView dataView, int position) {

		MTag tag = (MTag) convertView.getTag();
		int width = convertView.getWidth();
		if (tag instanceof List<?>) {
			List<IColumn> list = ((MTag) tag).columns;
			int[] columnWidths = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				IColumn column = list.get(i);
				Object propertyValue = column.getPropertyValue(record);
				View child = ((ViewGroup) convertView).getChildAt(i);
				if (child == null) {
					this.renderRecord(record, dataView, position,
							width, (ViewGroup) convertView); // Rendering
																				// failed,
																				// rerender
					return;
				}
				dataView.getRenderer(column).setPropValueToView(child, column,
						propertyValue, dataView, record);
				columnWidths[i] = child.getWidth();
			}
			ITheme currentTheme = dataView.getCurrentTheme();
			Drawable bgDrawable = currentTheme.getRecordBackgroundDrawable(
					dataView, position, columnWidths, convertView.getHeight());
			convertView.setBackgroundDrawable(bgDrawable);
		} else {
			if (width == 0) {
				width = dataView.getWidth();
			}
			this.renderRecord(record, dataView, position,
					width, (ViewGroup) convertView); // Rendering
																		// failed,
																		// rerender
		}

	}

	@Override
	public View render(StructuredDataView dataView, int maxWidth) {
		return null;
	}

	@Override
	public boolean isReusableView(View convertView, Object item, int position,
			AbstractViewer dataView) {
		//TODO REUSE GROUP VIEWS
		return (convertView != null) && (convertView instanceof LinearLayout)
				&& (convertView.getTag() instanceof MTag)
				&& (((List<?>) ((MTag) convertView.getTag()).columns).size() > 0);
	}

	@Override
	public void recycled(AbstractViewer dataView, View view) {
		Object tag = view.getTag();
		if (tag instanceof MTag) {
			MTag mt = (MTag) tag;
			List<IColumn> list = ((MTag) tag).columns;
			for (int i = 0; i < list.size(); i++) {
				IColumn column = list.get(i);
				View child = ((ViewGroup) view).getChildAt(i);
				if (child == null) {
					return;
				}
				IFieldRenderer renderer = ((StructuredDataView) dataView)
						.getRenderer(column);
				if (renderer instanceof IRecycleAwareFieldRenderer) {
					IRecycleAwareFieldRenderer fl = (IRecycleAwareFieldRenderer) renderer;
					fl.viewRecycled(child, column,
							column.getPropertyValue(mt.record), dataView,
							mt.record);
				}
			}
		}
	}

}
