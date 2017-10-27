package com.onpositive.commons.xml.language;


public interface IAttributeHandler {

	/**
	 * 
	 * @param elementObject
	 * @param value
	 * @param context TODO
	 * @return error message if unable to handle by some reason
	 */
	String handleAttribute( Object elementObject, Object value, Context context);
	String validate( String elementName, String attributeName );
}
