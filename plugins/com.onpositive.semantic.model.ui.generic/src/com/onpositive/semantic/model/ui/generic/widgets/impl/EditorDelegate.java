package com.onpositive.semantic.model.ui.generic.widgets.impl;



public abstract class EditorDelegate implements IElementBehaviorDelegate{

	public EditorDelegate(BasicUIElement<?> ui) {
		super();
		this.ui = ui;
	}

	protected final BasicUIElement<?>ui;

	@Override
	public void onCreateStart(BasicUIElement<?> element) {
		
	}

	@Override
	public void onCreateEnd(BasicUIElement<?> element) {
		
	}

	@Override
	public void onDispose(BasicUIElement<?> element) {
		
	}
	
	
}
