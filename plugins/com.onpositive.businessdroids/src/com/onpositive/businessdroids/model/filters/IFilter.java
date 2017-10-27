package com.onpositive.businessdroids.model.filters;

import java.io.Serializable;

import com.onpositive.businessdroids.ui.dataview.persistence.ISaveable;


public interface IFilter extends Serializable, ISaveable {

	public boolean matches(Object record);

	/**
	 * @return Human readable title for this filter
	 */
	public abstract String getTitle();

}
