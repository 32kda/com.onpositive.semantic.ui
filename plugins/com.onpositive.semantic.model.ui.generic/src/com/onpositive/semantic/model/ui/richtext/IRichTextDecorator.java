package com.onpositive.semantic.model.ui.richtext;

import com.onpositive.semantic.model.api.decoration.DecorationContext;
import com.onpositive.semantic.model.api.decoration.IObjectDecorator;


public interface IRichTextDecorator extends IObjectDecorator<StyledString> {

	/**
	 * 
	 * @param object
	 * @param text
	 * @param role
	 * @param theme
	 * @return
	 */
	StyledString decorate(DecorationContext parameterObject,
			StyledString text);
}
