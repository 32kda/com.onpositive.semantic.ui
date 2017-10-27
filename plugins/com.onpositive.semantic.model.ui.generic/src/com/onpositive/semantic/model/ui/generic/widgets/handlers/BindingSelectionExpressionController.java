package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import java.io.Serializable;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.ui.generic.EditorBindingController;
import com.onpositive.semantic.model.ui.generic.SelectionBindingController;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class BindingSelectionExpressionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	IUIElement<?>element;
	IBindable bindable;
	
	public BindingSelectionExpressionController(IUIElement<?> element, IBindable bindable)
	{
		super();
		this.element = element;
		this.bindable = bindable;
	}
	
	private EditorBindingController editorBindingController;
	private String currentBindingExpression;

	@HandlesAttributeDirectly("bindSelectionTo")
	public void setBindingFromString(String bindTo) {
		if (currentBindingExpression == null
				|| !currentBindingExpression.equals(bindTo)) {
			this.currentBindingExpression = bindTo;
			if (editorBindingController != null) {
				element.removeElementListener(editorBindingController);
				editorBindingController.dispose();
			}
			editorBindingController = new SelectionBindingController((IListElement<?>) bindable, bindTo);
			element.addElementListener(editorBindingController);
			editorBindingController.hierarchyChanged(element);
		}
	}
	
	public String getCurrentBindingExpression(){
		return currentBindingExpression;
	}
}
