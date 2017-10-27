package com.onpositive.semantic.ui.core;

import java.io.Serializable;

public class Rectangle implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Rectangle(int x2, int y2, int width2, int height) {
		this.x=x2;
		this.y=y2;
		this.width=width2;
		this.height=height;
	}
	public int x;
	public int y;
	public int width;
	public int height;
}
