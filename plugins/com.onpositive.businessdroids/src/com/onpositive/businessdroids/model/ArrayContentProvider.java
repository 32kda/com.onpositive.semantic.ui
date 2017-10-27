package com.onpositive.businessdroids.model;

import java.util.ArrayList;
import java.util.Arrays;

import com.onpositive.businessdroids.ui.dashboard.IContentProvider;

public class ArrayContentProvider implements IContentProvider {

	Object[]contents;
	
	public ArrayContentProvider(Object... contents) {
		super();
		this.contents = contents;
	}

	@Override
	public ArrayList<Object> getContent() {
		return new ArrayList<Object>(Arrays.asList(contents));
	}

}
