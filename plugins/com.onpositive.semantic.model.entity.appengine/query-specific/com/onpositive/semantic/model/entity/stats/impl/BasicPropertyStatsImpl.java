package com.onpositive.semantic.model.entity.stats.impl;

import com.onpositive.semantic.model.entity.stats.IPropertyStats;
import com.onpositive.semantic.model.entity.stats.IValueModel;

public class BasicPropertyStatsImpl implements IPropertyStats{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String id;
	protected int count;
	protected Class<?>type;
	protected boolean mv;
	protected boolean or;

	@Override
	public String id() {
		return id;
	}

	@Override
	public int count() {
		return count;
	}

	@Override
	public IValueModel model() {
		return null;
	}

	@Override
	public Class<?> type() {
		return type;
	}

	@Override
	public boolean multiValue() {
		return mv;
	}

	@Override
	public boolean ordered() {
		return or;
	}

}
