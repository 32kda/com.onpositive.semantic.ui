package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import java.io.Serializable;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.ui.generic.EditorBindingController;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class BindingExpressionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	IUIElement<?>element;
	IBindable bindable;
	
	public BindingExpressionController(IUIElement<?> element, IBindable bindable)
	{
		super();
		this.element = element;
		this.bindable = bindable;
	}
	
	private EditorBindingController editorBindingController;
	private String currentBindingExpression;

	@HandlesAttributeDirectly("bindTo")
	public void setBindingFromString(String bindTo) {
		if (currentBindingExpression == null
				|| !currentBindingExpression.equals(bindTo)) {
			this.currentBindingExpression = bindTo;
			if (editorBindingController != null) {
				element.removeElementListener(editorBindingController);
				editorBindingController.dispose();
			}
			editorBindingController = new EditorBindingController(bindable, bindTo);
			element.addElementListener(editorBindingController);
			editorBindingController.hierarchyChanged(element);
		}
	}
	
	public String getCurrentBindingExpression(){
		return currentBindingExpression;
	}
}
