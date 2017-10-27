package com.onpositive.commons.xml.language;

import java.io.Serializable;

public abstract class AbstractContextDependentAttributeHandler implements IAttributeHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected transient IAttributeHandler defaultHandler;//FIXME;
	
	public AbstractContextDependentAttributeHandler(
			IAttributeHandler defaultHandler) {
		super();
		this.defaultHandler = defaultHandler;
	}
	
	protected AbstractContextDependentAttributeHandler(){
		
	}
	
}
