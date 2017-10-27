package com.onpositive.businessdroids.model.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.ui.dataview.Group;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;
import com.onpositive.businessdroids.ui.dataview.persistence.NoSuchElement;

public class SimpleFieldGroupingCalculator implements IFieldGroupingCalculator {

	protected IField field;

	public SimpleFieldGroupingCalculator(IField field) {
		this.field = field;
	}

	@Override
	public List<Group> calculateGroups(TableModel tableModel) {
		return actualBasicGroup(tableModel,field);
	}

	@Override
	public void save(IStore store) {
		//Do nothing
	}

	@Override
	public void load(IStore store) throws NoSuchElement {
		//Do nothing
	}

	@Override
	public IField getGroupField() {
		return field;
	}

	@Override
	public void setGroupField(IField field) {
		this.field = field;
	}
	
	protected static ArrayList<Group> actualBasicGroup(TableModel tm,IField groupColumn) {
		HashMap<Object, ArrayList<Object>> groups = new HashMap<Object, ArrayList<Object>>();
		int itemCount = tm.getItemCount();
		for (int a = 0; a < itemCount; a++) {
			Object item = tm.getItem(a);
			Object propertyValue = groupColumn.getPropertyValue(item);
			
			if (propertyValue instanceof Iterable){
				Iterable<?> m=(Iterable<?>) propertyValue;
				for (Object p:m){
					process(groups, item, p);
				}
			}
			else if (propertyValue instanceof Object[]){
				Object[]m=(Object[]) propertyValue;
				for (Object p:m){
					process(groups, item, p);
				}
			}
			else{
				process(groups, item, propertyValue);
			}
		}
		ArrayList<Group> groupsm = new ArrayList<Group>(groups.size());
		for (Object o : groups.keySet()) {
			Group z = new Group(tm, (IColumn) groupColumn, o, groups.get(o)
					.toArray());
			groupsm.add(z);
		}
		return groupsm;
	}

	protected static void process(HashMap<Object, ArrayList<Object>> groups,
			Object item, Object propertyValue) {
		
		ArrayList<Object> arrayList = groups.get(propertyValue);
		if (arrayList == null) {
			arrayList = new ArrayList<Object>();
			groups.put(propertyValue, arrayList);
		}
		arrayList.add(item);
	}

	@Override
	public String getId() {
		return field.getId();
	}

}
