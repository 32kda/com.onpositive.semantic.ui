package com.onpositive.semantic.model.ui.property.editors;

public class PasswordElement extends OneLineTextElement<String>{

	public PasswordElement() {
		super.setIsPassword(true);
	}
}
