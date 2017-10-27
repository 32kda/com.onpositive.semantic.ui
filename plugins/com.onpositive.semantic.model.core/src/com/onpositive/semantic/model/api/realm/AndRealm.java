package com.onpositive.semantic.model.api.realm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class AndRealm<T> extends OrRealm<T> {

	
	@Override
	protected boolean internalContains(Object o) {
		for (IRealm<?> r : realms) {
			if (!r.contains(r)) {
				return false;
			}
		}
		return !realms.isEmpty();
	}

	
	@Override
	public Collection<T> internalGet() {
		final HashSet<T> el = new HashSet<T>();
		Iterator<IRealm<T>> iterator = this.realms.iterator();
		if (iterator.hasNext()) {
			IRealm<T> t = iterator.next();
			el.addAll(t.getContents());
			while (iterator.hasNext()) {
				final IRealm<T> e = iterator.next();
				el.retainAll(e.getContents());
			}
		}
		return el;
	}
}
