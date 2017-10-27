package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.ExpressionBinding;

public class ExpressionHandler extends GeneralElementHandler{

	public ExpressionHandler() {
		super(null,null);
		setObjectClass(Binding.class);
	}

	protected Object produceNewInstance(Element element, Object parentContext, Context context) {
		Binding b = (Binding) parentContext;
		IListenableExpression<?> parse = ExpressionAccess.parse(element.getAttribute("value"),b);
		ExpressionBinding bs=new ExpressionBinding(parse);
		return bs;	
	}
		
}
