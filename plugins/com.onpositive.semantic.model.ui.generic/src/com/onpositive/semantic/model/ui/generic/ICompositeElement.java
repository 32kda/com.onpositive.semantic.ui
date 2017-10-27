package com.onpositive.semantic.model.ui.generic;

import java.util.List;

import com.onpositive.commons.xml.language.ChildSetter;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.ui.core.Rectangle;

public interface ICompositeElement<T extends IUIElement<?>,A>extends IUIElement<A> {

	public List<T> getChildren();

	@ChildSetter( value = "uielement" ,
			      needCasting = true )
	public void add(T element);

	public void remove(T element);
	
	@HandlesAttributeDirectly("margin")
	public void setMargin(Rectangle parseRectangle);
	
	public void onDisplayable(IUIElement<?> element);

	IUIElement<?>getElement(String id);

	
	
}
