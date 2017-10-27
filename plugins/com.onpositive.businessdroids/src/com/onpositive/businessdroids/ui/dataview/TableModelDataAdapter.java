package com.onpositive.businessdroids.ui.dataview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.ui.dataview.renderers.IHeaderRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IRecordRenderer;

public class TableModelDataAdapter extends BaseAdapter {

	TableModel tableModel;
	StructuredDataView dataView;
	IRecordRenderer recordRenderer;
	private int oldParentWidth = 0;

	protected TableModelDataAdapter(Context context,
			StructuredDataView tableModel, IHeaderRenderer headerRenderer,
			IRecordRenderer recordRenderer) {
		super();
		this.dataView = tableModel;
		this.recordRenderer = recordRenderer;
		this.tableModel = tableModel.getTableModel();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// System.out.println("TableModelDataAdapter.getView() " + position);
		if (position < this.tableModel.getItemCount()) {
			Object item = this.tableModel.getItem(position);
			return this.getView(position, convertView, parent, item);
		} else if (position == 0) {
			TextView view = new TextView(parent.getContext());
			view.setText("No items to display");
			view.setGravity(Gravity.CENTER);
			return view;
		}
		return new TextView(parent.getContext());
	}

	@Override
	public int getCount() {
		int itemCount = this.tableModel.getItemCount();
		return itemCount;
	}

	public View getView(int position, View convertView, ViewGroup parent,
			Object item) {
		int width = parent.getWidth();
		if (width != this.oldParentWidth) {
			boolean horizontalScrollable = this.dataView.isHorizontalScrollable();
			TableModelDataAdapter.fitFieldWidths(this.dataView.getColumns(),
					this.dataView.fieldWidths, width,
					horizontalScrollable);
			dataView.customFit(dataView.fieldWidths, horizontalScrollable);
			this.oldParentWidth = width;
		}
		if ((convertView == null)
				|| !this.recordRenderer.isReusableView(convertView, item,
						position, this.dataView)) {
			convertView = this.recordRenderer.renderRecord(item, this.dataView,
					position, width, convertView);
		} else {
			this.recordRenderer.setRecordToView(convertView, item,
					this.dataView, position);
		}
		return convertView;
	}

	public static void fitFieldWidths(IColumn[] columns, int[] fieldWidth,
			int width, boolean b) {
		int sum = 0;
		int oldCaptionWidth = 0;
		int captionIndex = 0;
		//FIXME GROWING COLUMNS
		int growCount = 0;
		int shrinkCoun = 0;
		for (int a = 0; a < columns.length; a++) {
			IColumn column = columns[a];
			if (column.canGrow()) {
				captionIndex = a;
				growCount += 1;
				if (column.canShrink()) {
					shrinkCoun++;
				}
				oldCaptionWidth = fieldWidth[a];
			} else {
				sum = sum + fieldWidth[a];
			}
		}
		int newCaptionWidth = width - sum ;	
		if ((newCaptionWidth > oldCaptionWidth || (shrinkCoun>0)) && !b) {
			fieldWidth[captionIndex] = newCaptionWidth;
		}
	}

	@Override
	public Object getItem(int arg0) {
		return tableModel.getItem(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public void notifyDataSetChanged() {
		tableModel.update();
		super.notifyDataSetChanged();
	}

}
