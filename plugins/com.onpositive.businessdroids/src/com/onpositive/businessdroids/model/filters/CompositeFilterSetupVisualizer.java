package com.onpositive.businessdroids.model.filters;

import java.util.ArrayList;
import java.util.HashMap;

import com.onpositive.businessdroids.ui.dataview.StructuredDataView;


public class CompositeFilterSetupVisualizer implements IFilterSetupVisualizer {
	HashMap<Class<? extends IFilter>, IFilterSetupVisualizer> visualizers = new HashMap<Class<? extends IFilter>, IFilterSetupVisualizer>();
	ArrayList<IFilterSetupVisualizer> commons = new ArrayList<IFilterSetupVisualizer>();

	public boolean add(IFilterSetupVisualizer object) {
		return this.commons.add(object);
	}

	public boolean remove(IFilterSetupVisualizer object) {
		return this.commons.remove(object);
	}

	@Override
	public boolean setupFilter(IFilter filter, StructuredDataView dataView) {
		IFilterSetupVisualizer setupVisualizer = this.visualizers.get(filter
				.getClass());
		if (setupVisualizer != null) {
			return setupVisualizer.setupFilter(filter, dataView);
		}
		for (IFilterSetupVisualizer f : this.commons) {
			if (f.setupFilter(filter, dataView)) {
				return true;
			}
		}
		return false;
	}

	public void registerSetupVisualizer(Class<? extends IFilter> type,
			IFilterSetupVisualizer visualizer) {
		this.visualizers.put(type, visualizer);
	}

}
