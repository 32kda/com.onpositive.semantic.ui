package com.onpositive.semantic.ui.core;

import java.text.NumberFormat;
import java.util.StringTokenizer;

import com.onpositive.commons.xml.language.HandlingTypeConvertor;

public class PointToStringConvertor extends HandlingTypeConvertor.AbstractUnitConvertor {

	
	protected Object convertToTargetClass(Object obj) {
		return convert( (String)obj);
	}

	
	protected String convertToString(Object obj) {
		return convert( (Point)obj );
	}
	
	private String convert( Point p )
	{
		return ( "" + NumberFormat.getInstance().format(p.horizontal.value) + ',' + NumberFormat.getInstance().format(p.vertical.value) ) ;
	}
	
	private Point convert( String str )
	{
		StringTokenizer ts=new StringTokenizer(str,",;");
		try{
		String nextToken = ts.nextToken();
		String nextToken2 = ts.nextToken();
		Double width = nextToken.equals("*") ? null : Double.parseDouble(nextToken.trim());
		Double height = nextToken2.equals("*") ? null : Double.parseDouble(nextToken2.trim());
		return new Point(width,height);
		}catch (Exception e) {
			throw new IllegalArgumentException(str +" do not looks as valid point. Two comma separated numbers are expected" );
		}
	}

}
