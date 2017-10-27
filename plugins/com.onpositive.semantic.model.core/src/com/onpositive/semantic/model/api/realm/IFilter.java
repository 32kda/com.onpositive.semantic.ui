package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.changes.IValueListener;

/**
 * Basic filter abstraction
 * Filter can accept/not accept some value
 * and inform some listeners about filtering settings change
 * @author kor
 *
 */
public interface IFilter {

	/**
	 * Basic method; Returns filtering result for some element
	 * @param element Element to perform filtering
	 * @return <code>true</code> if element matches this filter, <code>false</code> otherwise
	 */
	public boolean accept(Object element);

	/**
	 * Adds filter settings listener to current filter
	 * @param listener filter settings listener 
	 */
	public void addValueListener(
			IValueListener<?> listener);

	/**
	 * Removes filter settings listener from current filter
	 * @param listener filter settings listener 
	 */
	public void removeValueListener(
			IValueListener<?> listener);
}
