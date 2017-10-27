package com.onpositive.semantic.ui.core;

import java.util.HashMap;

import com.onpositive.commons.xml.language.AbstractContextDependentAttributeHandler;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IAttributeHandler;

public class AlignmentAttributeHandler extends AbstractContextDependentAttributeHandler {
	
	private static HashMap<String,Integer> strToIntMap ;
	private static String[] intToStrMap ;
	
	static{
		strToIntMap = new HashMap<String, Integer>() ;
		
		strToIntMap.put( "fill"  , Alignment.FILL   ) ;
		strToIntMap.put( "top"   , Alignment.TOP    ) ;
		strToIntMap.put( "bottom", Alignment.BOTTOM ) ;
		strToIntMap.put( "left"  , Alignment.LEFT   ) ;
		strToIntMap.put( "right" , Alignment.RIGHT  ) ;
		strToIntMap.put( "center", Alignment.CENTER ) ;
		
		intToStrMap = new String[]{ "fill", "top", "bottom", "left", "right" ,"center" } ;
	}	       

	public AlignmentAttributeHandler(IAttributeHandler defaultHandler) {
		super( defaultHandler );
	}

	public String handleAttribute(Object elementObject, Object value, Context context) {
		
		Integer val = strToIntMap.get( (String)value ) ;
		if ( val == null )
			return errorMessage( ((String)value).toLowerCase() ) ;
		
		return defaultHandler.handleAttribute( elementObject, val, context ) ;
	}

	private String errorMessage(String str) {
		return "Error, " + str + " is invalid alignment style. Valid alignment styles are: " + intToStrMap.toString() + ".\n";
	}

	public String validate(String elementName, String attributeName) {
		return null;
	}

}
