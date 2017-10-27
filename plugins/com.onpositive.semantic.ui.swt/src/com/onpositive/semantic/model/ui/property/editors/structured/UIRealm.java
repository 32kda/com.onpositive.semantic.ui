package com.onpositive.semantic.model.ui.property.editors.structured;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.swt.widgets.Display;

import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IRealmChangeListener;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.realm.ISupportsExternalChangeNotify;

public class UIRealm<T> implements IRealm<T>, IRealmChangeListener<T>,
		ISupportsExternalChangeNotify {

	final IRealm<T> owner;
	final HashSet<IRealmChangeListener<T>> listeners = new HashSet<IRealmChangeListener<T>>();

	public void addRealmChangeListener(IRealmChangeListener<T> listener) {

		this.listeners.add(listener);
	}

	public Collection<T> getContents() {
		return this.owner.getContents();
	}

	public void removeRealmChangeListener(IRealmChangeListener<T> listener) {
		this.listeners.remove(listener);
		if (this.listeners.isEmpty()) {
			this.dispose();
		}
	}

	public UIRealm(IRealm<T> owner) {
		super();
		this.owner = owner;
		owner.addRealmChangeListener(this);
	}

	public void dispose() {
		this.owner.removeRealmChangeListener(this);
	}

	public void realmChanged(IRealm<T> realmn, final ISetDelta<T> delta) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				for (final IRealmChangeListener<T> l : UIRealm.this.listeners) {
					l.realmChanged(UIRealm.this, delta);
				}
			}
		});
	}

	public IRealm<T> getParent() {
		return this.owner;
	}

	public static <T> UIRealm<T> toUI(IRealm<T> realm) {
		if (realm == null) {
			return null;
		}
		IRealm<T> parent = realm;
		while (parent != null) {
			if (parent instanceof UIRealm) {
				return (UIRealm<T>) realm;
			}
			parent = parent.getParent();
		}
		return new UIRealm<T>(realm);
	}

	public int size() {
		return this.owner.size();
	}

	public boolean isOrdered() {
		return this.owner.isOrdered();
	}

	public boolean contains(Object o) {
		return this.owner.contains(o);
	}

	@SuppressWarnings("unchecked")
	public void changed(ISetDelta<?> dlt) {
		if (this.owner instanceof ISupportsExternalChangeNotify) {
			final ISupportsExternalChangeNotify ss = (ISupportsExternalChangeNotify) this.owner;
			ss.changed(dlt);
		} else {
			this.realmChanged(this, (ISetDelta<T>) dlt);

		}
	}

	public Iterator<T> iterator() {
		return this.owner.iterator();
	}

}
