package com.onpositive.businessdroids.model;

import com.onpositive.businessdroids.model.aggregation.IAggregatorChangeListener;

public interface IModelChangeListener extends IAggregatorChangeListener {
	public void modelChanged(TableModel model);
}
