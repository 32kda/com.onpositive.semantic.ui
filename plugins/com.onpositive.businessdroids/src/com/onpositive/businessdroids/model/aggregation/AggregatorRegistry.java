package com.onpositive.businessdroids.model.aggregation;

import java.util.HashMap;
import java.util.Map;

public class AggregatorRegistry {
	static Map<String,IAggregator> aggregators = new HashMap<String, IAggregator>();
	
	public static void registerAggregator(String id, IAggregator aggregator) {
		aggregators.put(id,aggregator);
	}
	
	public static IAggregator getAggregator(String id) {
		return aggregators.get(id);
	}
}
