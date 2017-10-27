package com.onpositive.businessdroids.model.aggregation;

import com.onpositive.businessdroids.model.IArray;
import com.onpositive.businessdroids.model.types.ComparableRange;
import com.onpositive.businessdroids.model.types.NumericRange;

public class RangeAggregator implements IAggregator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAggregatedValue(IArray values) {
		if (values.getItemCount() > 0) {
			if (Number.class.isAssignableFrom(values.getComponentType())) {
				Object item = values.getItem(0);
				double min = ((Number) item).doubleValue();
				double max = ((Number) item).doubleValue();
				for (Object i:values) {
					if (i == null) {
						continue;
					}
					double doubleValue = ((Number)i).doubleValue();
					if (doubleValue < min) {
						min = doubleValue;
					}
					if (doubleValue > max) {
						max = doubleValue;
					}
				}
				return new NumericRange(min, max);
			} else {
				Object item = values.getItem(0);
				Comparable min = ((Comparable) item);
				Comparable max = ((Comparable) item);
				for (Object i:values) {
					if (i == null) {
						continue;
					}
					if ((min == null) || (min.compareTo(i) > 0)) {
						min = (Comparable) i;
					}
					if ((max == null) || (max.compareTo(i) < 0)) {
						max = (Comparable) i;
					}
				}
				if ((min == null) && (max == null)) {
					return null;
				}
				return new ComparableRange(min, max);
			}
		}
		return null;
	}

	@Override
	public String getTitle() {
		return "Range";
	}

	@Override
	public String getId() {
		return "range";
	}

}
