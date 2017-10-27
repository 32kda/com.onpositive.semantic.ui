package com.onpositive.semantic.ui.realm.fastviewer;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class ColumnPaintInfo {

	public final String leftString;

	public final String rightString;

	public Color leftColor;

	public Color rightColor;
	
	public Image image;

	public ColumnPaintInfo(Color leftColor, String leftString,
			Color rightColor, String rightString) {
		super();
		this.leftColor = leftColor;
		this.leftString = leftString;
		this.rightColor = rightColor;
		this.rightString = rightString;
	}
}
