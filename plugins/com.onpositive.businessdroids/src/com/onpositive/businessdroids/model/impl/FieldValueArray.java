package com.onpositive.businessdroids.model.impl;

import java.util.Iterator;

import com.onpositive.businessdroids.model.IArray;
import com.onpositive.businessdroids.model.IField;

public class FieldValueArray implements IArray{

	final IField fld;
	final IArray array;

	public FieldValueArray(IField fld, IArray array) {
		super();
		this.fld = fld;
		this.array = array;
	}
	@Override
	public Iterator<Object> iterator() {
		return new Iterator<Object>() {

			Iterator<Object>b=array.iterator();
			
			@Override
			public boolean hasNext() {
				return b.hasNext();
			}

			@Override
			public Object next() {
				return fld.getPropertyValue(b.next());
			}

			@Override
			public void remove() {
				
			}
		};
	}
	@Override
	public int getItemCount() {
		return array.getItemCount();
	}
	@Override
	public Object getItem(int i) {
		return fld.getPropertyValue(array.getItem(i));
	}
	@Override
	public Class<?> getComponentType() {
		return fld.getType();
	}
}
