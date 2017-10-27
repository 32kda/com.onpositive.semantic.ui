package com.onpositive.businessdroids.model.aggregation;

import com.onpositive.businessdroids.model.IColumn;

public interface IAggregatorChangeListener {
	public void aggregatorChanged(IAggregator oldAggregator,
			IAggregator newAggregator, IColumn column);
}
