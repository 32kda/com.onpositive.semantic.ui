package com.onpositive.semantic.model.entity.stats.impl;

import java.util.HashMap;

import com.onpositive.semantic.model.entity.stats.IEntityStats;
import com.onpositive.semantic.model.entity.stats.IPropertyStats;

public class BasicEntityStatsImpl implements IEntityStats{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String kind;
	protected int totalCount;
	protected int directCount;
	
	@Override
	public int totalCount() {
		return totalCount;
	}

	@Override
	public int directCount() {
		return directCount;
	}

	@Override
	public String kind() {
		return kind;
	}
	
	
	transient HashMap<String,IPropertyStats>map;

	@Override
	public IPropertyStats getPropertyStats(String prop) {
		if (map==null){
			synchronized (this) {
				map=new HashMap<String, IPropertyStats>();
				IPropertyStats[] allPropertyStats = getAllPropertyStats();
				for (IPropertyStats s:allPropertyStats){
					map.put(s.id(),s);
				}
			}		
		}
		return map.get(prop);
	}
	
	protected IPropertyStats[] stats;

	@Override
	public IPropertyStats[] getAllPropertyStats() {
		return stats;
	}

}
