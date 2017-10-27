package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.changes.IValueListener;

/**
 * "Negative" filter wrapper
 * This class can be used for creating filter, which would accept all elements, not accepted 
 * by baseProperty filter and vice versa
 * @author 32kda
 *
 */
public class NotFilter implements IFilter
{
	protected IFilter baseFilter;

	/**
	 * Basic constructor
	 * @param baseFilter {@link IFilter} to create inverse filter for
	 */
	public NotFilter(IFilter baseFilter)
	{
		super();
		this.baseFilter = baseFilter;
	}

	@Override
	public boolean accept(Object element)
	{
		return !baseFilter.accept(element);
	}

	@Override
	public void addValueListener(IValueListener<?> listener) {
		baseFilter.addValueListener(listener);
	}

	@Override
	public void removeValueListener(IValueListener<?> listener) {
		baseFilter.removeValueListener(listener);
	}
}
