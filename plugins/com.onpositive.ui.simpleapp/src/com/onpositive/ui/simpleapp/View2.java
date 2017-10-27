package com.onpositive.ui.simpleapp;

import com.onpositive.semantic.ui.workbench.elements.XMLView;

public class View2 extends XMLView {
	
	String text;
	
	public static final String ID = "com.onpositive.ui.simpleapp.view2";

	public View2() {
		super("view2.dlf");
	}
	
	private void okPressed() {
		System.out.println("TestActivity.okPressed()" + text);

	}
	
	private void cancelPressed() {
		System.out.println("TestActivity.cancelPressed()");
	}
}