package com.onpositive.semantic.ui.core;

import java.io.Serializable;

public class Point implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Point(int x, int y) {
		this.horizontal=new Dimension(x,Dimension.UNIT_PIXELS);
		this.vertical=new Dimension(y,Dimension.UNIT_PIXELS);
	}
	
	public Point(Double x, Double y) {
		this.horizontal = (x == null) ? null : new Dimension(x, Dimension.UNIT_PIXELS);
		this.vertical   = (y == null) ? null : new Dimension(y, Dimension.UNIT_PIXELS);
	}
	
	public Dimension horizontal;
	public Dimension vertical;
}
