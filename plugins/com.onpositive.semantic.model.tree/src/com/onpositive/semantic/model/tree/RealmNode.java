package com.onpositive.semantic.model.tree;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmChangeListener;

public class RealmNode<T> extends BaseMeta implements IRealmChangeListener<T>, IRealmBasedTreeNode<T>,
		IRealm<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final IClusterizationPoint<T> point;
	private final IClusterizationPointProvider<T> clusterizationPointProvider;
	private final RealmNode<T> parent;
	private IRealm<T> realm;
	private boolean visible;

	private final ArrayList<IClusterizationPoint<T>> clusterizationPoints = new ArrayList<IClusterizationPoint<T>>();
	private final ArrayList<RealmNode<T>> realmOwners = new ArrayList<RealmNode<T>>();

	private final HashSet<T> allElements = new HashSet<T>();
	private final HashSet<T> rootElements = new HashSet<T>();

	private final HashMap<IClusterizationPoint<T>, RealmNode<T>> map = new HashMap<IClusterizationPoint<T>, RealmNode<T>>();
	private HashSet<ITreeChangeListener<T>> listeners;
	private HashSet<IRealmChangeListener<T>> realmListeners;

	private ITreeNode<T>[] rrs;

	HashDelta<T> dlt = new HashDelta<T>();

	private final IClusterNodeCallback<T> cb = new IClusterNodeCallback<T>() {

		public Collection<T> getParentClusterElements() {
			return RealmNode.this.parent.allElements;
		}

		public ISetDelta<T> getDelta() {
			return RealmNode.this.dlt;
		}

		public IClusterizationPoint<T> getOwner() {
			return RealmNode.this.point;
		}

		public boolean isVisible() {
			return RealmNode.this.visible;
		}

		public void add(T element) {
			RealmNode.this.parent.addedToGlobal.remove(element);
			RealmNode.this.parent.addedToLocal.add(element);
			RealmNode.this.dlt.markAdded(element);
			if (RealmNode.this.parent.rootElements.contains(element)) {
				RealmNode.this.parent.rootElements.remove(element);
				RealmNode.this.parent.rrs = null;
			}
		}

		public void remove(T element) {
			RealmNode.this.parent.removedFromGlobal.remove(element);
			RealmNode.this.parent.removedFromLocal.add(element);
			RealmNode.this.dlt.markRemoved(element);
		}

		public void removeChanged(T element) {
			RealmNode.this.parent.removedFromGlobal.remove(element);
			if (!RealmNode.this.parent.addedToLocal.contains(element)) {
				RealmNode.this.parent.addedToGlobal.add(element);
			}
			RealmNode.this.dlt.markRemoved(element);
		}

		public void setVisible(boolean value) {
			RealmNode.this.visible = value;
		}

		public Collection<T> getPointElements() {
			return RealmNode.this.allElements;
		}

	};
	private HashSet<T> addedToGlobal;
	private HashSet<T> addedToLocal;
	private HashSet<T> removedFromGlobal;
	private HashSet<T> removedFromLocal;
	private HashSet<ITreeNode<T>> newContent;
	private HashDelta<ITreeNode<T>> treeDelta;
	
	@SuppressWarnings("rawtypes")
	static HashDelta noinstance=new HashDelta();

	public RealmNode(IClusterizationPointProvider<T> clusterizationPointProvider) {
		this.clusterizationPointProvider = clusterizationPointProvider;
		this.point = null;
		this.parent = null;
	}

	protected RealmNode(RealmNode<T> parent, IClusterizationPoint<T> point) {
		super();
		this.point = point;
		this.parent = parent;
		this.clusterizationPointProvider = point.getSubClusterizationProvider();
	}

	private RealmNode<T> getRealmOwner(IClusterizationPoint<T> point2) {
		return this.map.get(point2);
	}

	public void contentChanged(IRealm<T> realmn, ISetDelta<T> delta) {

		this.dlt = new HashDelta<T>(new HashSet<T>(),
				delta.getChangedElements(), new HashSet<T>(
						delta.getRemovedElements()));
		this.point.processDelta(delta, this.cb);
		final ISetDelta<T> clusterDelta = this.cb.getDelta();
		this.realmChanged(realmn, clusterDelta);
	}

	@SuppressWarnings("unchecked")
	public synchronized void realmChanged(IRealm<T> realmn, ISetDelta<T> delta) {

		if (delta.isEmpty()) {
			return;
		}
		HashSet<ITreeNode<T>> oldContent = null;
		if ((this.parent != null)
				|| ((this.listeners != null) && !this.listeners.isEmpty())) {
			oldContent = new HashSet<ITreeNode<T>>();
			this.fillChildren(oldContent);
			this.treeDelta = new HashDelta<ITreeNode<T>>();
		}
		this.allElements.addAll(delta.getAddedElements());
		this.allElements.removeAll(delta.getRemovedElements());
		this.fireRealmListeners(delta);
		final ISetDelta<IClusterizationPoint<T>> processDelta = this.clusterizationPointProvider != null ? 
				this.clusterizationPointProvider
				.processDelta(delta, this.map.keySet(), this.allElements)
				: noinstance;

		this.addedToGlobal = new HashSet<T>(delta.getAddedElements());
		this.removedFromGlobal = new HashSet<T>();
		this.removedFromLocal = new HashSet<T>();
		this.addedToLocal = new HashSet<T>();
		final Collection<IClusterizationPoint<T>> removedElements = processDelta
				.getRemovedElements();

		if (!removedElements.isEmpty()) {
			final HashSet<T> toReprocess = new HashSet<T>();
			for (final IClusterizationPoint<T> ps : removedElements) {
				final RealmNode<T> realmOwner = this.map.get(ps);
				if (realmOwner != null) {
					toReprocess.addAll(realmOwner.allElements);
					this.clusterizationPoints.remove(ps);

					this.realmOwners.remove(realmOwner);
					this.map.remove(ps);
				}
			}
			delta = new HashDelta<T>(delta.getAddedElements(),
					delta.getChangedElements(), delta.getRemovedElements());
			this.clusterizationPoints.removeAll(removedElements);
			toReprocess.removeAll(delta.getRemovedElements());
			final HashDelta<T> dlta = new HashDelta<T>(toReprocess,
					Collections.EMPTY_SET, Collections.EMPTY_SET);
			this.processImmutableDelta(realmn, dlta, oldContent, processDelta);
			this.rrs = null;
			// delta.getAddedElements().addAll(delta.getAddedElements());
		}

		l2: for (final T e : delta.getRemovedElements()) {
			for (final RealmNode<T> ea : this.realmOwners) {
				if (ea.allElements.contains(e)) {
					continue l2;
				}
			}
			this.removedFromGlobal.add(e);
		}
		this.processImmutableDelta(realmn, delta, oldContent, processDelta);

		if (!processDelta.isEmpty()) {
			final Collection<IClusterizationPoint<T>> addedElements = processDelta
					.getAddedElements();
			if (!addedElements.isEmpty()) {
				for (final IClusterizationPoint<T> ps : addedElements) {
					this.clusterizationPoints.add(ps);
					if (ps.equals(this.point)){
						continue;
					}
					final RealmNode<T> realmOwner = new RealmNode<T>(this, ps);
					this.realmOwners.add(realmOwner);
					realmOwner.contentChanged(realmn,
							new HashDelta<T>(this.allElements,
									(Collection<T>) Collections.emptySet(),
									(Collection<T>) Collections.emptySet()));
					this.map.put(ps, realmOwner);
					final HashSet<T> toRemove = new HashSet<T>();
					for (final T e : this.rootElements) {
						if (realmOwner.allElements.contains(e)) {
							toRemove.add(e);
						}
					}

					this.rootElements.removeAll(toRemove);
					this.rrs = null;
					// populate content of given node;
				}
			}
			// set of clusterization points was changed;
		}

		final int ka = this.rootElements.size();
		if (!this.addedToGlobal.isEmpty() || !this.removedFromGlobal.isEmpty()) {
			this.rrs = null;

			if ((this.parent != null) && !this.isVisible()
					&& ((this.rootElements.size() > 0) || (ka > 0))) {
				this.parent.rrs = null;
			}
		}
		this.addedToGlobal = null;
		this.removedFromGlobal = null;
		this.removedFromLocal = null;
		this.addedToLocal = null;
		if (oldContent != null) {
			this.calcAndFireTreeDelta(delta, oldContent, processDelta,
					delta.getChangedElements());
		}
		oldContent = null;
	}

	private void fireRealmListeners(ISetDelta<T> delta) {
		if (this.realmListeners != null) {
			for (final IRealmChangeListener<T> a : this.realmListeners) {
				a.realmChanged(this, delta);
			}
		}
	}

	private void processImmutableDelta(IRealm<T> realmn, ISetDelta<T> delta,
			HashSet<ITreeNode<T>> oldContent,
			ISetDelta<IClusterizationPoint<T>> processDelta) {
		for (final IClusterizationPoint<T> point : this.clusterizationPoints) {
			final RealmNode<T> realmCallback = this.getRealmOwner(point);
			realmCallback.contentChanged(realmn, delta);
		}
		this.rootElements.addAll(this.addedToGlobal);
		this.rootElements.removeAll(this.removedFromGlobal);
	}

	@SuppressWarnings("unchecked")
	private void calcAndFireTreeDelta(ISetDelta<T> delta,
			HashSet<ITreeNode<T>> oldContent,
			ISetDelta<IClusterizationPoint<T>> processDelta,
			Collection<T> collection) {
		this.newContent = new HashSet<ITreeNode<T>>();
		this.fillChildren(this.newContent);
		if (!collection.isEmpty()) {
			for (Object o : newContent) {
				ITreeNode<T> n = (ITreeNode<T>) o;
				Object element = n.getElement();
				if (element instanceof IClusterizationPoint<?>) {
					IClusterizationPoint<?> p = (IClusterizationPoint<?>) element;
					Object primaryValue = p.getPrimaryValue();
					if (collection.contains(primaryValue)) {
						treeDelta.markChanged(n);
					}
				} else {
					if (collection.contains(element)) {
						treeDelta.markChanged(n);
					}
				}
			}
		}
		for (final ITreeNode<T> e : oldContent) {
			if (!this.newContent.contains(e)) {
				this.treeDelta.markRemoved(e);
			}
			final Object element = e.getElement();
			if (element != null) {
				if (delta.getChangedElements().contains(element)) {
					this.treeDelta.markChanged(e);
				}
			}
			if (e instanceof RealmNode) {
				final RealmNode<T> r = (RealmNode<T>) e;
				if (r.point != null) {
					if (processDelta.getChangedElements().contains(r.point)) {
						this.treeDelta.markChanged(e);
					}
				}
			}
			this.newContent.remove(e);
		}

		for (final ITreeNode<T> r : this.newContent) {
			this.treeDelta.markAdded(r);
		}
		if (!this.treeDelta.isEmpty()) {
			if (this.listeners != null) {
				for (final ITreeChangeListener<T> l : this.listeners) {
					l.processTreeChange(this, this.treeDelta);
				}
			}
			if (this.parent != null) {

				this.parent.markChangeDelta(this, this.treeDelta);
			}
		}
		this.treeDelta = null;
	}

	private void markChangeDelta(RealmNode<T> realmNode,
			HashDelta<ITreeNode<T>> dlt2) {
		if (this.treeDelta != null) {
			this.treeDelta.markChanged(realmNode, dlt2);
		}
	}

	@SuppressWarnings("unchecked")
	public ITreeNode<T>[] getChildren() {
		if (this.rrs == null) {
			final ArrayList<ITreeNode<T>> result = new ArrayList<ITreeNode<T>>();
			this.fillChildren(result);
			// ITreeNode[] rrs = new ITreeNode[result.size()];
			this.rrs = new ITreeNode[result.size()];
			result.toArray(this.rrs);
			return this.rrs;
		}
		return this.rrs;
	}

	public String toString() {
		final StringWriter wr = new StringWriter();
		if (this.getElement() != null) {
			wr.append(this.getElement().toString());

		} else {
			wr.append("NULL"); //$NON-NLS-1$
		}
		wr.append("{"); //$NON-NLS-1$
		for (final ITreeNode<T> e : this.getChildren()) {
			wr.append(e.toString());
			wr.append(',');
		}
		wr.append("}"); //$NON-NLS-1$
		return wr.toString();
	}

	private synchronized void fillChildren(ArrayList<ITreeNode<T>> result) {
		HashSet<Object> notRemove = new HashSet<Object>();
		for (final RealmNode<T> owner : this.realmOwners) {
			if (owner.isVisible()) {
				if (owner.point != null) {
					Object primaryValue = owner.point.getPrimaryValue();
					if (primaryValue != null) {
						notRemove.add(primaryValue);
					}
				}
				result.add(owner);
			} else {
				owner.fillChildren(result);
			}
		}
		for (final T el : this.rootElements) {
			if (!notRemove.contains(el)) {
				result.add(new LeafNode<T>(this, el));
			}
		}

	}

	private void fillChildren(HashSet<ITreeNode<T>> result) {
		HashSet<Object> notRemove = new HashSet<Object>();
		for (final RealmNode<T> owner : this.realmOwners) {

			if (owner.isVisible()) {
				result.add(owner);
				if (owner.point != null) {
					Object primaryValue = owner.point.getPrimaryValue();
					if (primaryValue != null) {
						notRemove.add(primaryValue);
					}
				}
			} else {
				owner.fillChildren(result);
			}
		}
		for (final T el : this.rootElements) {
			if (!notRemove.contains(el)) {
				result.add(new LeafNode<T>(this, el));
			}
		}
	}

	public boolean isVisible() {
		return this.visible;
	}

	void setVisible(boolean visible) {
		this.visible = visible;
	}

	public ITreeNode<T> getParentNode() {
		return this.parent;
	}

	public boolean hasChildren() {
		return this.getChildren().length > 0;
	}

	public IClusterizationPoint<T> getElement() {
		return this.point;
	}

	@SuppressWarnings("unchecked")
	public void addChangeListener(ITreeChangeListener<?> listener) {
		if (this.listeners == null) {
			this.listeners = new HashSet<ITreeChangeListener<T>>();
		}
		this.listeners.add((ITreeChangeListener<T>) listener);
	}

	public void removeChangeListener(ITreeChangeListener<?> listener) {
		if (this.listeners != null) {
			this.listeners.remove(listener);
		}
	}

	public Comparator<ITreeNode<T>> getComparator() {

		return new Comparator<ITreeNode<T>>() {

			Comparator<T> rootElementComparator = RealmNode.this.clusterizationPointProvider != null ? RealmNode.this.clusterizationPointProvider
					.getRootElementComparator() : null;

			Comparator<IClusterizationPoint<T>> clusterComparator = RealmNode.this.clusterizationPointProvider != null ? RealmNode.this.clusterizationPointProvider
					.getComparator() : null;

			@SuppressWarnings("unchecked")
			public int compare(ITreeNode<T> o1, ITreeNode<T> o2) {
				final boolean b = o1 instanceof RealmNode;
				final boolean c = o2 instanceof RealmNode;
				if (b && c) {
					final RealmNode<T> r1 = (RealmNode<T>) o1;
					final RealmNode<T> r2 = (RealmNode<T>) o2;
					if (this.clusterComparator != null) {
						return this.clusterComparator.compare(r1.point,
								r2.point);
					}
					return r1.point.compareTo(r2.point);
				} else {
					if (b && !c) {
						return -1;
					}
					if (c && !b) {
						return 1;
					}
					if (this.rootElementComparator != null) {
						return this.rootElementComparator.compare(
								(T) o1.getElement(), (T) o2.getElement());
					}
				}
				if ((o1.getElement() instanceof Comparable)
						&& (o2.getElement() instanceof Comparable)) {
					final Comparable<Object> o1c = (Comparable<Object>) o1
							.getElement();
					return o1c.compareTo(o2.getElement());
				}
				return 0;
			}

		};
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.point == null) ? 0 : this.point.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final RealmNode other = (RealmNode) obj;
		if (this.point == null) {
			if (other.point != null) {
				return false;
			}
		} else if (!this.point.equals(other.point)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public synchronized void setRealm(IRealm fi) {
		final IRealm<T> rm = this.realm;
		if (rm != null) {
			this.realm.removeRealmChangeListener(this);
		}
		if (fi != null) {
			this.realm = fi;
			this.realm.addRealmChangeListener(this);
			
			Collection<? extends Object> old = rm != null ? rm.getContents() : Collections.emptySet();
			
			
			this.realmChanged(this.realm, HashDelta.buildFrom(
					old,fi.getContents()));
		}
	}

	public void addRealmChangeListener(IRealmChangeListener<T> listener) {
		if (this.realmListeners == null) {
			this.realmListeners = new HashSet<IRealmChangeListener<T>>();
		}
		this.realmListeners.add(listener);
	}

	@SuppressWarnings("unchecked")
	public Collection<T> getContents() {
		return (Collection<T>) this.allElements.clone();
	}

	public IRealm<T> getParent() {
		return null;
	}

	public void removeRealmChangeListener(IRealmChangeListener<T> listener) {
		if (this.realmListeners != null) {
			this.realmListeners.remove(listener);
			if (this.realmListeners.isEmpty()) {
				this.realmListeners = null;
			}
		}
	}

	public int size() {
		return this.allElements.size();
	}

	@SuppressWarnings("hiding")
	public <T> T getAdapter(Class<T> adapter) {
		if (this.point!=null){
		return this.point.getAdapter(adapter);
		}
		return null;
	}

	public boolean isOrdered() {
		return false;
	}

	public boolean contains(Object o) {
		if (this.point != null) {
			Object primaryValue = point.getPrimaryValue();
			if (primaryValue != null) {
				if (primaryValue.equals(o)) {
					return true;
				}
			}
		}
		return this.allElements.contains(o);
	}

	public boolean represents(Object o) {
		return false;
	}

	public Iterator<T> iterator() {
		return this.allElements.iterator();
	}

	@SuppressWarnings("unchecked")
	public List<? extends ITreeNode<T>> findPath(
			List<? extends ITreeNode<T>> current, Object o) {
		if (this.point != null) {
			Object primaryValue = this.point.getPrimaryValue();
			if (this.point.equals(o)
					|| (primaryValue != null && primaryValue.equals(o))) {
				if (current == null) {
					current = new ArrayList<ITreeNode<T>>();
					((List) current).add(0, this);
					return current;
				} else {
					((List) current).add(0, this);
					return current;
				}
			}
		}
		ITreeNode<T>[] children = this.getChildren();
		for (ITreeNode n : children) {

			if (n.contains(o)) {
				List findPath = n.findPath((List<?>) current, o);
				if (findPath != null) {
					findPath.add(0, this);
					return findPath;
				}
			}

		}
		return null;
	}

	@Override
	public boolean mayHaveDublicates() {
		return parent.mayHaveDublicates();
	}

	

	@Override
	public IRealm<?> getChildRealm() {
		return this;
	}

}