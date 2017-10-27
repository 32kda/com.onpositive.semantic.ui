package com.onpositive.businessdroids.model.impl;

import java.util.Arrays;
import java.util.Iterator;

import com.onpositive.businessdroids.model.IArray;

public class BasicArray implements IArray{

	Object[]l;
	
	public BasicArray(Object[] l) {
		super();
		this.l = l;
	}

	@Override
	public Iterator<Object> iterator() {
		return Arrays.asList(l).iterator();
	}

	@Override
	public int getItemCount() {
		return l.length;
	}

	@Override
	public Object getItem(int i) {
		return l[i];
	}

	@Override
	public Class<?> getComponentType() {
		return l.getClass().getComponentType();
	}

}
