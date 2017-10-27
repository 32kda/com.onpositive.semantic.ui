package com.onpositive.businessdroids.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;


public class BasicTableModel extends AbstractCollectionBasedModel {

	protected ArrayList<Object> items = new ArrayList<Object>();
	

	public BasicTableModel(IColumn[] columns) {
		super(columns);
	}

	@Override
	protected List<?> items() {
		return this.items;
	}
	
}