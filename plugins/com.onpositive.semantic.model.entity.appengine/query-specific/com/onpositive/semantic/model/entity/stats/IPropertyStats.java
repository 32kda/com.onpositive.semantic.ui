package com.onpositive.semantic.model.entity.stats;

import java.io.Serializable;

public interface IPropertyStats extends Serializable{

	String id();
	int count();
	IValueModel model();
	
	Class<?>type();
	boolean multiValue();
	boolean ordered();
}
