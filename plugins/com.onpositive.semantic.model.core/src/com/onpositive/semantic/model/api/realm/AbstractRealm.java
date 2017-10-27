package com.onpositive.semantic.model.api.realm;

import java.util.HashSet;
import java.util.LinkedHashSet;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.meta.BaseMeta;

public abstract class AbstractRealm<T> extends BaseMeta implements IRealm<T> {

	private static final long serialVersionUID = 1L;
	private final LinkedHashSet<IRealmChangeListener<T>> listeners = new LinkedHashSet<IRealmChangeListener<T>>();

	public AbstractRealm() {
		super();
	}

	int dl = 0;

	protected boolean hasDetailedListeners() {
		return dl>0;
	}

	@Override
	public void addRealmChangeListener(IRealmChangeListener<T> listener) {
		boolean isEmpty = false;
		if (listeners.isEmpty()) {
			isEmpty = true;
		}

		boolean add = this.listeners.add(listener);
		if (add) {
			if (!(listener instanceof INotDetailedListener)) {
				dl++;
			}
		}
		if (isEmpty) {
			startListening();
		}
	}

	protected void startListening() {

	}

	protected void stopListening() {

	}

	public boolean isListening() {
		return !listeners.isEmpty();
	}

	@Override
	public void removeRealmChangeListener(IRealmChangeListener<T> listener) {

		boolean remove = this.listeners.remove(listener);
		if (remove) {
			if (!(listener instanceof INotDetailedListener)) {
				dl--;
			}
		}
		if (listeners.isEmpty()) {
			stopListening();
		}
	}

	protected void fireDelta(ISetDelta<T> dlt) {
		for (final IRealmChangeListener<T> l : new LinkedHashSet<IRealmChangeListener<T>>(
				this.listeners)) {
			l.realmChanged(this, dlt);
		}
	}

	public void dispose() {
		stopListening();
	}

}