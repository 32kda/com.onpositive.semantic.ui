package com.onpositive.semantic.model.jobs;

import java.io.Serializable;

import com.onpositive.semantic.model.api.status.CodeAndMessage;

public interface IJob extends Serializable{

	/**
	 * @return result of work
	 */
	CodeAndMessage perform();	
	
}
