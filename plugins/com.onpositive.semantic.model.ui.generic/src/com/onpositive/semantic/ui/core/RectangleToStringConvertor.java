package com.onpositive.semantic.ui.core;

import java.text.NumberFormat;
import java.util.StringTokenizer;

import com.onpositive.commons.xml.language.HandlingTypeConvertor;

public class RectangleToStringConvertor  extends HandlingTypeConvertor.AbstractUnitConvertor {

	
	protected Object convertToTargetClass(Object obj) throws IllegalArgumentException {
		return convert( (String)obj );
	}

	
	protected String convertToString(Object obj) {
		return convert((Rectangle)obj);
	}
	
	private String convert( Rectangle r )
	{
		return ( "" + NumberFormat.getInstance().format(r.x) + ',' + NumberFormat.getInstance().format(r.y) 
					+ NumberFormat.getInstance().format(r.height) + ',' + NumberFormat.getInstance().format(r.width) ) ;
	}
	
	private Rectangle convert( String str )
	{
		StringTokenizer ts=new StringTokenizer(str,",;");
		try{
		String nextToken0= ts.nextToken();
		String nextToken1 = ts.nextToken();
		String nextToken2 = ts.nextToken();
		String nextToken3 = ts.nextToken();
		
		Double parseDouble0 = Double.parseDouble(nextToken0.trim());
		Double parseDouble1 = Double.parseDouble(nextToken1.trim());
		Double parseDouble2 = Double.parseDouble(nextToken2.trim());
		Double parseDouble3 = Double.parseDouble(nextToken3.trim());
		return new Rectangle( parseDouble0.intValue(),parseDouble1.intValue(),parseDouble2.intValue(),parseDouble3.intValue()) ;
		}catch (Exception e) {
			throw new IllegalArgumentException(str +" do not looks as valid rectangle. Four comma separated numbers are expected" );
		}
	}

}
