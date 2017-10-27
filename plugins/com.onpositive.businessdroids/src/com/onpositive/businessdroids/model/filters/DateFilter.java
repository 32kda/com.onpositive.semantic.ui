package com.onpositive.businessdroids.model.filters;

import java.util.Date;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;


public class DateFilter extends ComparableFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateFilter(TableModel tableModel, IColumn column, Date min, Date max) {
		super(tableModel, column, min, max);
	}

}
