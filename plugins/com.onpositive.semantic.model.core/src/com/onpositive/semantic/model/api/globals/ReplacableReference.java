package com.onpositive.semantic.model.api.globals;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class ReplacableReference implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String keyString;

	public ReplacableReference(String keyString) {
		super();
		this.keyString = keyString;
	}

	Object readResolve() throws ObjectStreamException{
		return GlobalAccess.resolve(keyString);		
	}

}
