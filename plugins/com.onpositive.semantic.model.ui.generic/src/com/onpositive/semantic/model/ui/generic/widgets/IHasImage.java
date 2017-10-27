package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public interface IHasImage {

	@HandlesAttributeDirectly("image")
	public void setImage(String image);
	
	public String getImage();
}
