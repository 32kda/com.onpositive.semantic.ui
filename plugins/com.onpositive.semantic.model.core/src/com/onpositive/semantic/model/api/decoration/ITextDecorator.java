
package com.onpositive.semantic.model.api.decoration;


public interface ITextDecorator extends IObjectDecorator<String> {

	/**
	 * 
	 * @param parameterObject
	 *            TODO
	 * @param text
	 * @return
	 */
	@Override
	String decorate(DecorationContext parameterObject, String text);

}
