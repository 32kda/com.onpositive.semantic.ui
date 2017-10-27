package com.onpositive.businessdroids.ui;

public interface IRefCountingElement {

	public void incRef(Object obj);
	public void decRef(Object obj);
}
