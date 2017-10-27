package com.onpositive.semantic.model.api.query;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class DefaultQueryPreprocessorProvider implements IQueryPreprocessorProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<IQueryPreProcessor> listeners = new ArrayList<IQueryPreProcessor>();
	protected ArrayList<IQueryPreprocessorProvider> provider = new ArrayList<IQueryPreprocessorProvider>();

	
	public IQueryPreProcessor[] getListeners(Query q) {
		LinkedHashSet<IQueryPreProcessor> rr = null;
		if (!listeners.isEmpty()) {
			rr = new LinkedHashSet<IQueryPreProcessor>();
			for (IQueryPreProcessor m:listeners){
				process(null, rr, m);
			}
		}
		for (IQueryPreprocessorProvider p : provider) {
			IQueryPreProcessor[] listeners2 = p.getPreprocessors(q);
			for (IQueryPreProcessor z : listeners2) {
				if (rr == null) {
					rr = new LinkedHashSet<IQueryPreProcessor>();
				}
				process(null, rr, z);
			}
		}
		if (rr == null) {
			return null;
		}
		return rr.toArray(new IQueryPreProcessor[rr.size()]);
	}

	private void process(String role, LinkedHashSet<IQueryPreProcessor> rr,
			IQueryPreProcessor m) {
		
		rr.add(m);
		
	}

	public boolean add(IQueryPreProcessor e) {
		return listeners.add(e);
	}

	public boolean remove(IQueryPreProcessor o) {
		return listeners.remove(o);
	}

	public boolean add(IQueryPreprocessorProvider e) {
		return provider.add(e);
	}

	public boolean remove(IQueryPreprocessorProvider o) {
		return provider.remove(o);
	}

	@Override
	public IQueryPreProcessor[] getPreprocessors(Query q) {
		return getListeners(q);
	}
}
