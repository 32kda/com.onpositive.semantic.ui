package com.onpositive.businessdroids.model.groups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.impl.AbstractCollectionBasedModel;
import com.onpositive.businessdroids.model.types.NumericRange;
import com.onpositive.businessdroids.ui.dataview.Group;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;
import com.onpositive.businessdroids.ui.dataview.persistence.NoSuchElement;

public class NumericRangeGroupingCalculator implements IFieldGroupingCalculator {

	protected static final int MAX_UNRANGED_COUNT = 12;
	protected int rangeCount;
	protected IField field;
	protected SimpleFieldGroupingCalculator simpleCalculator;

	public NumericRangeGroupingCalculator(IColumn field, int rangeCount) {
		this.field = field;
		this.rangeCount = rangeCount;
		simpleCalculator = new SimpleFieldGroupingCalculator(field);
	}

	@Override
	public List<Group> calculateGroups(TableModel tableModel) {
		AbstractCollectionBasedModel cbm = (AbstractCollectionBasedModel) tableModel;
		if (field == null) {
			return new ArrayList<Group>();
		}
		HashMap<Object, ArrayList<Object>> groups = new HashMap<Object, ArrayList<Object>>();
		int itemCount = tableModel.getItemCount();
		Object[] allValues = cbm.getValuesForField(field);

		// remove nulls;
		ArrayList<Object> notNulls = new ArrayList<Object>(allValues.length);
		ArrayList<Object> nulls = new ArrayList<Object>();
		for (int a = 0; a < allValues.length; a++) {
			if (allValues[a] == null) {
				nulls.add(tableModel.getItem(a));
			} else {
				notNulls.add(allValues[a]);
			}
		}
		allValues = notNulls.toArray();
		if (allValues.length == 0) {
			ArrayList<Group> arrayList = new ArrayList<Group>();
			if (nulls.size() > 0) {
				Group z = new Group(tableModel, field, null, nulls.toArray());
				arrayList.add(z);
			}
			return arrayList;
		}
		Arrays.sort(allValues);
		HashSet<Object>m=new HashSet<Object>(Arrays.asList(allValues));
		if (m.size()<MAX_UNRANGED_COUNT){
			return simpleCalculator.calculateGroups(tableModel);
		}
		int rangeLength = allValues.length / this.rangeCount + 1;
		List<NumericRange> ranges = new ArrayList<NumericRange>(this.rangeCount);
		if (rangeLength >= allValues.length) // Then we have only one interval
		{
			NumericRange numericRange = new NumericRange((Number) allValues[0],
					(Number) allValues[allValues.length - 1]);
			groups.put(numericRange, new ArrayList<Object>());
			ranges.add(numericRange);
		} else {
			Number prev = (Number) allValues[0];
			for (int i = 1; i < allValues.length; i++) {
				if ((i % rangeLength == 0) || (i == allValues.length - 1)) {
					NumericRange numericRange = new NumericRange(prev,
							(Number) allValues[i]);
					ranges.add(numericRange);
					groups.put(numericRange, new ArrayList<Object>());
					if (i < allValues.length - 1) {
						prev = (Number) allValues[i + 1];
					} else {
						prev = (Number) allValues[i];
					}
				}
			}
		}

		for (int a = 0; a < itemCount; a++) {
			Object item = tableModel.getItem(a);
			Object propertyValue = field.getPropertyValue(item);
			if (propertyValue == null) {
				continue;
			}
			NumericRange numericRange = this.getRange(propertyValue, ranges);
			ArrayList<Object> arrayList = groups.get(numericRange);
			arrayList.add(item);
		}
		List<Group> result = new ArrayList<Group>(groups.size());
		for (Object key : groups.keySet()) {
			Object[] valuesArray = groups.get(key).toArray();
			if (valuesArray.length >= 0) {
				this.doRangeCorrection((NumericRange) key, valuesArray, field);
				Group z = new Group(tableModel, field, key, valuesArray);
				result.add(z);
			}
		}
		if (nulls.size() > 0) {
			Group z = new Group(tableModel, field, null, nulls.toArray());
			result.add(z);
		}
		return result;
	}

	protected void doRangeCorrection(NumericRange key, Object[] records,
			IField field) {
		if (records.length == 0) {
			return;
		}
		double min = ((Number) field.getPropertyValue(records[0]))
				.doubleValue();
		double max = min;
		int minIdx = 0;
		int maxIdx = 0;
		for (int i = 1; i < records.length; i++) {
			double value = ((Number) field.getPropertyValue(records[i]))
					.doubleValue();
			if (value < min) {
				min = value;
				minIdx = i;
			}
			if (value > max) {
				max = value;
				maxIdx = i;
			}
		}
		key.setStart(((Number) field.getPropertyValue(records[minIdx])));
		key.setEnd(((Number) field.getPropertyValue(records[maxIdx])));
	}

	protected NumericRange getRange(Object propertyValue,
			List<NumericRange> ranges) {
		for (NumericRange numericRange : ranges) {
			if (numericRange.compare((Number) propertyValue) == 0) {
				return numericRange;
			}
		}
		throw new AssertionError("Can't find range for " + propertyValue);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.field == null) ? 0 : this.field.hashCode());
		result = prime * result + this.rangeCount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		NumericRangeGroupingCalculator other = (NumericRangeGroupingCalculator) obj;
		if (this.field == null) {
			if (other.field != null) {
				return false;
			}
		} else if (!this.field.equals(other.field)) {
			return false;
		}
		if (this.rangeCount != other.rangeCount) {
			return false;
		}
		return true;
	}

	@Override
	public void save(IStore store) {
		store.putInt("rangeCount", this.rangeCount);
	}

	@Override
	public void load(IStore store) throws NoSuchElement {
		this.rangeCount = store.getInt("rangeCount", 5);
	}

	@Override
	public IField getGroupField() {
		return field;
	}

	@Override
	public void setGroupField(IField field) {
		this.field = field;
	}

	@Override
	public String getId() {
		return field.getId();
	}

}
