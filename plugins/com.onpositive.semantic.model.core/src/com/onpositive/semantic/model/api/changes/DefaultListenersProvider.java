package com.onpositive.semantic.model.api.changes;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class DefaultListenersProvider implements IObjectListenersProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<IObjectListener> listeners = new ArrayList<IObjectListener>();
	protected ArrayList<IObjectListenersProvider> provider = new ArrayList<IObjectListenersProvider>();

	@Override
	public IObjectListener[] getListeners(Object obj, String role) {
		LinkedHashSet<IObjectListener> rr = null;
		if (!listeners.isEmpty()) {
			rr = new LinkedHashSet<IObjectListener>();
			for (IObjectListener m:listeners){
				process(role, rr, m);
			}
		}
		for (IObjectListenersProvider p : provider) {
			IObjectListener[] listeners2 = p.getListeners(obj, null);
			for (IObjectListener z : listeners2) {
				if (rr == null) {
					rr = new LinkedHashSet<IObjectListener>();
				}
				process(role, rr, z);
			}
		}
		if (rr == null) {
			return null;
		}
		return rr.toArray(new IObjectListener[rr.size()]);
	}

	private void process(String role, LinkedHashSet<IObjectListener> rr,
			IObjectListener m) {
		if (m instanceof IRoledListener){
			IRoledListener l=(IRoledListener) m;
			if (l.getRole()==null||l.getRole().equals(role)){
				rr.add(m);	
			}
		}
		else{
			rr.add(m);
		}
	}

	public boolean add(IObjectListener e) {
		return listeners.add(e);
	}

	public boolean remove(IObjectListener o) {
		return listeners.remove(o);
	}

	public boolean add(IObjectListenersProvider e) {
		return provider.add(e);
	}

	public boolean remove(IObjectListenersProvider o) {
		return provider.remove(o);
	}
}
