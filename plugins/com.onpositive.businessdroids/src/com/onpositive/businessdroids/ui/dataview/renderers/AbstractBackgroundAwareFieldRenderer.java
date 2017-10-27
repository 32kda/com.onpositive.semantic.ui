package com.onpositive.businessdroids.ui.dataview.renderers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.view.View;

import com.onpositive.businessdroids.R;
import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.IViewer;

public abstract class AbstractBackgroundAwareFieldRenderer implements
		IRecycleAwareFieldRenderer {

	private final class MM extends AsyncTask<Object, Object, Object> {
		private final View renderedField;

		private MM(View renderedField) {
			this.renderedField = renderedField;
		}

		protected Object doInBackground(Object... params) {
			try {
				Thread.sleep(initial_sleep);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Map<Object, ArrayList<View>> select = null;
			synchronized (vs) {
				
				select = select(vs);
				for (Object v : select.keySet()) {
					vs.remove(v);
				}
				
			}
			doGet(select);
			return select;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Object result) {
			Map<Object, ArrayList<View>> select = (Map<Object, ArrayList<View>>) result;
			for (Object q : select.keySet()) {
				ArrayList<View> arrayList2 = select.get(q);
				for (View v : arrayList2) {
					IColumn column = (IColumn) v.getTag(1);
					Object fv = v.getTag(2);
					IViewer table = (IViewer) v.getTag(3);
					Object object = v.getTag(4);
					dataFetched(v, column, fv, table, object);
				}
			}
			synchronized (vs) {
				if (task!=null){
					if (!vs.isEmpty()){
						task=new MM(renderedField);
						task.execute( new Object[]{} );											
												
					}
					else{
						task=null;
					}
				}
			}
		}
	}

	static final int OBJECT_TAG_INDEX = R.string.OBJECT_TAG_INDEX;

	public static final int FIELD_VALUE_TAG_INDEX = R.string.FIELD_VALUE_TAG_INDEX;

	public static final int TABLE_TAG_INDEX = R.string.TABLE_TAG_INDEX;

	public static final int COLUMN_TAG_INDEX = R.string.COLUMN_TAG_INDEX;




	@Override
	public View renderField(IField column, Object fieldValue, IViewer table,
			Object object) {
		View v = renderBasic(column, fieldValue, table, object);
		if (hasAllInfo(column, fieldValue, table, object)) {
			updateWithDetails(v, column, fieldValue, table, object);
		} else {
			scheduleUpdate(v, column, fieldValue, table, object);
		}
		return v;
	}

	protected abstract void basicUpdate(View v, IField column,
			Object fieldValue, IViewer table, Object object);

	protected abstract void updateWithDetails(View v, IField column,
			Object fieldValue, IViewer table, Object object);

	protected abstract View renderBasic(IField column, Object fieldValue,
			IViewer table, Object object);

	public abstract boolean hasAllInfo(IField column, Object fieldValue, IViewer v,
			Object object) ;



	@Override
	public void setPropValueToView(View renderedField, IField column,
			Object fieldValue, IViewer table, Object parenObj) {
		basicUpdate(renderedField, column, fieldValue, table, parenObj);
		if (hasAllInfo(column, fieldValue, table, parenObj)) {
			updateWithDetails(renderedField, column, fieldValue, table,
					parenObj);
		} else {
			scheduleUpdate(renderedField, column, fieldValue, table, parenObj);
		}
		// reusing view;
	}

	protected int initial_sleep = 200;

	protected LinkedHashMap<Object, ArrayList<View>> vs = new LinkedHashMap<Object, ArrayList<View>>();
	protected AsyncTask<Object, Object, Object> task;
	
	
	

	protected void scheduleUpdate(final View renderedField, IField column,
			Object fieldValue, IViewer table, Object parenObj) {
		Object updateKey = getUpdateKey(column, fieldValue, table, parenObj);
		renderedField.setTag(COLUMN_TAG_INDEX, column);
		renderedField.setTag(FIELD_VALUE_TAG_INDEX, fieldValue);
		renderedField.setTag(TABLE_TAG_INDEX, table);
		renderedField.setTag(OBJECT_TAG_INDEX, parenObj);
		synchronized (vs) {
			ArrayList<View> arrayList = vs.get(updateKey);
			if (arrayList == null) {
				arrayList = new ArrayList<View>();
				vs.put(updateKey, arrayList);
			}
			arrayList.add(renderedField);
			if (task == null) {
				task = new MM(renderedField);
				task.execute(renderedField);
			}
		}
	}

	protected abstract void doGet(Map<Object, ArrayList<View>> select);

	protected abstract Map<Object, ArrayList<View>> select(
			LinkedHashMap<Object, ArrayList<View>> vs2);

	protected Object getUpdateKey(IField column, Object fieldValue,
			IViewer table, Object parenObj) {
		return fieldValue;
	}

	@Override
	public void viewRecycled(View renderedField, IField column,
			Object fieldValue, IViewer table, Object object) {
		Object updateKey = getUpdateKey(column, fieldValue, table, object);
		synchronized (vs) {
			ArrayList<View> arrayList = vs.get(updateKey);
			if (arrayList != null) {
				arrayList.remove(renderedField);
			
			if (arrayList.size()==0){
				vs.remove(updateKey);
			}
			}
		}
	}

	public void dataFetched(View v, IColumn column, Object fv, IViewer table,
			Object object) {
		updateWithDetails(v, column, fv, table, object);
	}

	
}
