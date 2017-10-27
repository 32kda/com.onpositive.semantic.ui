package com.onpositive.semantic.model.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;

public class CompositeClusterizationPointProvider extends
		AbstractClusterizationPointProvider<Object> 
		{

	private HashSet<IClusterizationPointProvider<Object>> ds = new HashSet<IClusterizationPointProvider<Object>>();

	@SuppressWarnings("unchecked")
	public boolean add(IClusterizationPointProvider e) {
		e.addChangeListener(this);
		final boolean add = this.ds.add(e);
		if (add) {
			this.fireChange();
		}
		return add;
	}

	public void dispose() {
		for (final IClusterizationPointProvider<Object> p : this.ds) {
			p.removeChangeListener(this);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean remove(IClusterizationPointProvider o) {
		o.removeChangeListener(this);
		final boolean remove = this.ds.remove(o);
		if (remove) {
			this.fireChange();
		}
		return remove;
	}

	public ISetDelta<IClusterizationPoint<Object>> processDelta(
			ISetDelta<Object> delta,
			Collection<IClusterizationPoint<Object>> currentPoints,
			Collection<Object> currentElements) {
		final ArrayList<IClusterizationPoint<Object>> result_add = new ArrayList<IClusterizationPoint<Object>>();
		final ArrayList<IClusterizationPoint<Object>> result_change = new ArrayList<IClusterizationPoint<Object>>();
		final ArrayList<IClusterizationPoint<Object>> result_delete = new ArrayList<IClusterizationPoint<Object>>();
		for (final IClusterizationPointProvider<Object> a : this.ds) {
			final ISetDelta<IClusterizationPoint<Object>> processDelta = a
					.processDelta(delta, currentPoints, currentElements);
			if (processDelta!=null){
			result_add.addAll(processDelta.getAddedElements());
			result_change.addAll(processDelta.getChangedElements());
			result_delete.addAll(processDelta.getRemovedElements());
			}
		}
		return new HashDelta<IClusterizationPoint<Object>>(result_add,
				result_change, result_delete);
	}

	public void changed(IClusterizationPointProvider<Object> provider,
			Object extraData) {
		this.fireChange();
	}

	@SuppressWarnings("unchecked")
	public void set(Collection<IClusterizationPointProvider<?>> providers) {
		final boolean b = !new HashSet(providers).equals(this.ds);
		if (b) {
			this.ds = new HashSet<IClusterizationPointProvider<Object>>(
					(Collection) providers);
			this.fireChange();
		}
	}

	@Override
	public IClusterizationPoint<Object> createPoint(Object o) {
		return null;
	}
}
