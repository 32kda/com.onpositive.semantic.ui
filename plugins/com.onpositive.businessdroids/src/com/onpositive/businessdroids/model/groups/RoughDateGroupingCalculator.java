package com.onpositive.businessdroids.model.groups;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.impl.AbstractComputableField;
import com.onpositive.businessdroids.model.impl.Column;
import com.onpositive.businessdroids.model.types.ComparableRange;
import com.onpositive.businessdroids.model.types.NumericRange;
import com.onpositive.businessdroids.ui.dataview.Group;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;
import com.onpositive.businessdroids.ui.dataview.persistence.NoSuchElement;
import com.onpositive.businessdroids.ui.dataview.renderers.PrettyFormat;

import android.text.format.Time;

public class RoughDateGroupingCalculator implements IGroupingCalculator,IHasId,IFieldGroupingCalculator {

	protected IColumn column;

	public RoughDateGroupingCalculator(IColumn column) {
		super();
		this.column = column;
	}
	public RoughDateGroupingCalculator() {
		super();
	}

	@Override
	public List<Group> calculateGroups(TableModel tm) {
		HashMap<Object, ArrayList<Object>> groups = new HashMap<Object, ArrayList<Object>>();
		int itemCount = tm.getItemCount();
		final IField groupProperty = column;
		HashMap<String , Object>m=new HashMap<String, Object>();
		for (int a = 0; a < itemCount; a++) {
			Object item = tm.getItem(a);
			Object fieldValue = getPrimaryValue(groupProperty, item);
			if (fieldValue instanceof Date) {
				String format = PrettyFormat.format(fieldValue, true);
				m.put((String) format, fieldValue);
				fieldValue = format;				
				m.put((String) fieldValue, format);
			}
			if (fieldValue instanceof Time) {
				Time t = (Time) fieldValue;
				long millis = t.toMillis(false);
				String format = PrettyFormat.format(new Date(millis), true);
				m.put((String) format, fieldValue);
				fieldValue = format;
				
			}
			ArrayList<Object> arrayList = groups.get(fieldValue);
			if (arrayList == null) {
				arrayList = new ArrayList<Object>();
				groups.put(fieldValue, arrayList);
			}
			arrayList.add(item);
		}
		ArrayList<Group> groupsList = new ArrayList<Group>(groups.size());
		for (Object o : groups.keySet()) {
			Group z = new Group(tm, groupProperty, m.get(o), groups.get(o).toArray());
			groupsList.add(z);
		}
		if (groupsList.size() > 5) {
			Column calculatedColumn = new Column(
					new AbstractComputableField() {

						@Override
						public Object getPropertyValue(Object object) {
							Object fieldValue = getPrimaryValue(groupProperty, object);
							if (fieldValue instanceof Date) {
								Date dt = (Date) fieldValue;
								return dt.getTime();
							}
							if (fieldValue instanceof Time) {
								Time t = (Time) fieldValue;
								long millis = t.toMillis(false);
								return millis;
							}
							return null;
						}
					});
			NumericRangeGroupingCalculator numericRangeGroupingCalculator = new NumericRangeGroupingCalculator(
					(IColumn) calculatedColumn, 5);
			List<Group> calculateGroups = numericRangeGroupingCalculator
					.calculateGroups(tm);
			for (Group g : calculateGroups) {
				Object key = g.getKey();
				if (key instanceof NumericRange){
					NumericRange r=(NumericRange) key;
					g.setKey(new ComparableRange( new Date(r.getStart().longValue()),new Date(r.getEnd().longValue())));
				}	
				if (key instanceof Number){
					Date date = new Date(((Number)key).longValue());
					g.setKey(date);
				}
			}
			return calculateGroups;
		}
		return groupsList;
	}

	protected Object getPrimaryValue(final IField groupProperty, Object item) {
		return groupProperty.getPropertyValue(item);
	}

	@Override
	public void save(IStore store) {

	}

	@Override
	public void load(IStore store) throws NoSuchElement {

	}
	@Override
	public String getId() {
		return column.getId();
	}
	@Override
	public IField getGroupField() {
		return column;
	}
	@Override
	public void setGroupField(IField field) {
		
	}

}
