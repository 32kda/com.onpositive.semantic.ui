package com.onpositive.semantic.model.ui.property.editors.structured;

import java.util.Collection;
import java.util.Comparator;

import org.eclipse.jface.viewers.StructuredSelection;

import com.onpositive.semantic.model.realm.IFilter;
import com.onpositive.semantic.model.realm.ISimpleChangeListener;

public interface IRealmContentProvider {

	public void addFilter(IFilter flt);

	public void removeFilter(IFilter flt);

	public void setComparator(Comparator<? extends Object> cmp, boolean inverse);

	public void addListener(
			ISimpleChangeListener<IRealmContentProvider> listener);

	public void removeContentListener(
			ISimpleChangeListener<IRealmContentProvider> listener);

	public Collection<Object> getContents();
	
	public void restoreSelection(StructuredSelection sel);
	
	public void refresh();
}
