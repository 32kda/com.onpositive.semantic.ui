package com.onpositive.commons.xml.language;

import java.io.InputStream;

public class EvaluationContext {

	String name;
	InputStream stream;
	Context context;
	
	public EvaluationContext(String name, InputStream stream, Context context) {
		super();
		this.name = name;
		this.stream = stream;
		this.context = context;
	}
		
}
