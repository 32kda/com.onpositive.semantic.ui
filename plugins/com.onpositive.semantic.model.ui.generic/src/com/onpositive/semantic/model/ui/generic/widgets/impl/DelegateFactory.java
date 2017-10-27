package com.onpositive.semantic.model.ui.generic.widgets.impl;

import com.onpositive.semantic.model.ui.generic.widgets.IButton;
import com.onpositive.semantic.model.ui.generic.widgets.IDateTimeEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IStackComposite;
import com.onpositive.semantic.model.ui.generic.widgets.ITextElement;

public class DelegateFactory {

	
	public static IElementBehaviorDelegate createDelegate(BasicUIElement<?> element){
		if (element instanceof IButton){
			return new ButtonDelegate(element);
		}
		if (element instanceof ITextElement){
			return new TextDelegate(element);
		}
		if (element instanceof IDateTimeEditor){
			return new DateTimeDelegate(element);
		}
		if (element instanceof IStackComposite) {
			return new StackBehaviourDelegate(element);
		}
		return null;		
	}
}
