package com.onpositive.semantic.model.entity.stats;

import com.onpositive.semantic.model.api.changes.ISetDelta;

public interface IStatsManager {

	void processDelta(ISetDelta<Object>objectDelta);
	
	IEntityStats getStats(String kind);
	
}
