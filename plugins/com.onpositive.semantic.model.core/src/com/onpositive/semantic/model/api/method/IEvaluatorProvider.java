package com.onpositive.semantic.model.api.method;



public interface IEvaluatorProvider {
	
	/**
	 * @param obj - Base object
	 * @param name - Method name
	 * @return method evaluator for method with given name & base object obj , or null if no method with a given name exists
	 * for a given object
	 */
	public abstract IMethodEvaluator getMethodEvaluator(
			Object obj, String name);
	
	/**
	 * Returns collection of known method evaluators for a given object
	 * system properties are not returned inside of this collection
	 * 
	 * 
	 */
	Iterable<IMethodEvaluator> getAllMethodEvaluators(Object obj);

}
