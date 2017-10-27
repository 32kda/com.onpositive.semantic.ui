package com.onpositive.semantic.model.entity.stats;

import java.io.Serializable;


public interface IEntityStats extends Serializable{

	int totalCount();
	
	int directCount();
	
	String kind();

	IPropertyStats getPropertyStats(String prop);
	IPropertyStats[] getAllPropertyStats();
}
