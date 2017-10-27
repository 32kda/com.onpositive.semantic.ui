package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public interface IComboSelector<T> extends ISelectorElement<T>,ICanBeReadOnly<T>{

	public boolean isSelectDefault();

	@HandlesAttributeDirectly("selectDefault")
	public void setSelectDefault(boolean selectDefault) ;
}
