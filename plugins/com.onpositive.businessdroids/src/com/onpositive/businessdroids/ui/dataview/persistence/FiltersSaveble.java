package com.onpositive.businessdroids.ui.dataview.persistence;

import java.util.ArrayList;

import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.filters.IFilter;


/**
 * Stores filters configuration
 * 
 * @author 32kda
 * 
 */
public class FiltersSaveble implements ISaveable {

	// protected StructuredDataView view;
	protected TableModel tableModel;
	protected boolean setFiltersImmediately = false;
	protected IFilter[] filters;

	public FiltersSaveble(TableModel tableModel) {
		super();
		this.tableModel = tableModel;
	}

	@Override
	public void save(IStore store) {
		IFilter[] registeredFilters = this.tableModel.getRegisteredFilters();
		store.putInt("count", registeredFilters.length);
		int a = 0;
		for (IFilter f : registeredFilters) {
			String name = f.getClass().getName();
			IStore subStore = store.getOrCreateSubStore("filter" + a);
			subStore.putString("className", name);
			f.save(subStore);
			a++;
		}
	}

	@Override
	public void load(IStore store) {
		int count = store.getInt("count", 0);
		this.filters = new IFilter[count];
		ArrayList<IFilter> filtersA = new ArrayList<IFilter>();
		for (int a = 0; a < count; a++) {
			IStore subStore = store.getSubStore("filter" + a);
			String className = subStore.getString("className", null);
			IFilter filter = this.createFilter(subStore, className);
			try {
				filter.load(subStore);
			} catch (NoSuchElement e) {
				continue;
			}
			filtersA.add(filter);
			// tableModel.addFilter(filter);
		}
		this.filters = filtersA.toArray(new IFilter[filtersA.size()]);
		if (this.setFiltersImmediately) {
			this.tableModel.setFilters(this.filters);
		}
	}

	protected IFilter createFilter(IStore subStore, String className) {
		return FilterLoadFactory.createFilter(className, subStore,
				this.tableModel);
	}

	public boolean isSetFiltersImmediately() {
		return this.setFiltersImmediately;
	}

	public void setFiltersImmediately(boolean setFiltersImmediately) {
		this.setFiltersImmediately = setFiltersImmediately;
	}

	public IFilter[] getFilters() {
		return this.filters;
	}

}
