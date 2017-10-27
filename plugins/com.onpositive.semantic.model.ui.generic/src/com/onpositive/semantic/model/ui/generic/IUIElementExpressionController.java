package com.onpositive.semantic.model.ui.generic;

import com.onpositive.commons.xml.language.IAttributeHandler;
import com.onpositive.commons.xml.language.IExpressionController;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class IUIElementExpressionController extends IExpressionController {
	
	public IUIElementExpressionController(){}
	
	protected IListenableExpression<?> expression ;
	protected String expressionString ;
	protected IUIElement<? extends Object> modelObject ;
	protected IAttributeHandler attributeHandler ;
	
	public IUIElementExpressionController( String expressionString, Object modelObject, IAttributeHandler attributeHandler )
	{
		this.expressionString = expressionString ;
		setObject( modelObject ) ;
		
		
		this.attributeHandler = attributeHandler ;
	}
	
	
	
	public void setExpressionString(String s) {
		this.expressionString = s ;		
	}
	
	public void setObject(final Object modelObject ) {
		try{
			this.modelObject = (IUIElement<?>)modelObject ;
		}catch( ClassCastException e){
			System.err.print( "Error^ cannot cast " + modelObject.getClass().toString() +
							  " to " + IUIElement.class.toString() + ";"  ) ;			
		}
		this.modelObject.addElementListener(new AbstractComponentController(expressionString,this.modelObject) {
			
			
			protected void setValue(Object newValue) {
				attributeHandler.handleAttribute(modelObject, newValue, null);
			}
		});
	}
	public void setAttributeHandler(IAttributeHandler ah) {
		this.attributeHandler = ah ;		
	}
}
