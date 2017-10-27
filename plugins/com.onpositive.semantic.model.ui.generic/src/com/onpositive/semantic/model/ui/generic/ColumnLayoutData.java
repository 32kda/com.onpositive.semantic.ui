package com.onpositive.semantic.model.ui.generic;

import java.io.Serializable;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public class ColumnLayoutData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean resizeable;
	int width;
	int growth;
	
	public boolean isResizeable() {
		return resizeable;
	}

	public void setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
	}

	public int getWidth() {
		return width;
	}

	@HandlesAttributeDirectly("initialWidth")
	public void setWidth(int width) {
		this.width = width;
	}

	public int getGrowth() {
		return growth;
	}
	
	@HandlesAttributeDirectly("resizeWeight")
	public void setGrowth(int growth) {
		this.growth = growth;
	}

	public ColumnLayoutData(int i, boolean b) {
		this.resizeable=b;
		this.width=-1;
		this.growth=i;
	}

	public ColumnLayoutData(int growth2, int initialWidth, boolean b) {
		this.growth=growth2;
		this.width=initialWidth;
		this.resizeable=b;
	}

}
