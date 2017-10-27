package com.onpositive.semantic.ui.core;

import java.io.Serializable;

public class Dimension implements Serializable{

	public Dimension(double value, int units) {
		this.value = value;
		this.unit  = units;
	}
	
	public Dimension(int x, int unitPixels) {
		this.value=x;
		this.unit=unitPixels;
	}
	public double value;
	public int unit;
	
	public final static int UNIT_PIXELS=1;
	public final static int UNIT_DLU=2;
	//public final static int UNIT_PERCENTS=3;
}
