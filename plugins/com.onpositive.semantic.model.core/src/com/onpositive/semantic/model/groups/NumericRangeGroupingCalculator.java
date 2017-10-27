package com.onpositive.semantic.model.groups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.onpositive.semantic.model.api.property.FunctionOperator;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.groups.BasicGroupingOperators.Group;


public class NumericRangeGroupingCalculator extends FunctionOperator {

	private static final int MAX_UNRANGED_COUNT = 8;

	protected int rangeCount;

	public NumericRangeGroupingCalculator(int rangeCount) {
		super();
		this.rangeCount = rangeCount;
	}

	@SuppressWarnings("unchecked")
	public List<Group> calculateGroups(IFunction q, Object vl) {
		Collection<Object> c=ValueUtils.toCollection(vl);
	
		HashMap<Object, ArrayList<Object>> groups = new HashMap<Object, ArrayList<Object>>();
		Object[] allValues = getValuesForField(c,q);

		// remove nulls;
		ArrayList<Object> notNulls = new ArrayList<Object>(allValues.length);
		ArrayList<Object> nulls = new ArrayList<Object>();
		int a=0;
		for (Object qq:c) {
			if (allValues[a] == null) {
				nulls.add(allValues[a]);
			} else {
				notNulls.add(allValues[a]);
			}
			a++;
		}
		allValues = notNulls.toArray();
		if (allValues.length == 0) {
			ArrayList<Group> arrayList = new ArrayList<Group>();
			if (nulls.size() > 0) {
				Group z = new Group(null,q, nulls.toArray());
				arrayList.add(z);
			}
			return arrayList;
		}
		Arrays.sort(allValues);
		HashSet<Object>m=new HashSet<Object>(Arrays.asList(allValues));
		if (m.size()<MAX_UNRANGED_COUNT){
			return (List<Group>) BasicGroupingOperators.getInstance().calc(q, c);
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
			Number prev = getNumber(allValues[0]);
			for (int i = 1; i < allValues.length; i++) {
				if ((i % rangeLength == 0) || (i == allValues.length - 1)) {
					NumericRange numericRange = new NumericRange(prev,
							getNumber(allValues[i]));
					ranges.add(numericRange);
					groups.put(numericRange, new ArrayList<Object>());
					if (i < allValues.length - 1) {
						prev = getNumber(allValues[i + 1]);
					} else {
						prev = getNumber(allValues[i]);
					}
				}
			}
		}

		for (Object item: c) {
			Object propertyValue = q.getValue(item);
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
				this.doRangeCorrection((NumericRange) key, valuesArray, q);
				Group z = new Group( key,q, valuesArray);
				result.add(z);
			}
		}
		Collections.sort(result,new Comparator<Group>(){

			@Override
			public int compare(Group group1, Group group2) {
				return (int) Math.signum(((NumericRange)group1.key).end.doubleValue() - ((NumericRange)group2.key).end.doubleValue());
			}
			
		});
		if (nulls.size() > 0) {
			Group z = new Group(null,q, nulls.toArray());
			result.add(z);
		}
		return result;
	}
	
	protected Number getNumber(Object object) {
		if (object instanceof Number)
			return (Number) object;
		return null;
	}

	protected NumericRange getRange(Object propertyValue,
			List<NumericRange> ranges) {
		for (NumericRange numericRange : ranges) {
			if (numericRange.compare(getNumber(propertyValue)) == 0) {
				return numericRange;
			}
		}
		throw new AssertionError("Can't find range for " + propertyValue);
	}

	private Object[] getValuesForField(Collection<Object> c, IFunction q) {
		Object[] vl=new Object[c.size()];
		int a=0;
		for (Object qm:c){
			vl[a++]=q.getValue(qm);
		}
		return vl;
	}

	protected void doRangeCorrection(NumericRange key, Object[] records,
			IFunction field) {
		if (records.length == 0) {
			return;
		}
		double min = ((Number) getNumber(field.getValue(records[0])))
				.doubleValue();
		double max = min;
		int minIdx = 0;
		int maxIdx = 0;
		for (int i = 1; i < records.length; i++) {
			double value = ((Number) getNumber(field.getValue(records[i]))).doubleValue();
			if (value < min) {
				min = value;
				minIdx = i;
			}
			if (value > max) {
				max = value;
				maxIdx = i;
			}
		}
		key.setStart(((Number) getNumber(field.getValue(records[minIdx]))));
		key.setEnd(((Number) getNumber(field.getValue(records[maxIdx]))));
	}

	


	@Override
	protected Object calc(IFunction f, Object o2) {
		return calculateGroups(f, o2);
	}

}
