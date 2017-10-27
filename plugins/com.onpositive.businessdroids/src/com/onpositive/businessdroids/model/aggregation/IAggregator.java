package com.onpositive.businessdroids.model.aggregation;

import java.io.Serializable;

import com.onpositive.businessdroids.model.IArray;

public interface IAggregator extends Serializable{
	public abstract Object getAggregatedValue(IArray values);

	public abstract String getTitle();
	
	public String getId();
}
