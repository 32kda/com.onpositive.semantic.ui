package com.onpositive.semantic.model.tree2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.WeakHashMap;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.realm.FilteringRealm;
import com.onpositive.semantic.model.api.realm.IDescribableToQuery;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmChangeListener;
import com.onpositive.semantic.model.api.realm.ParentedRealm;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.status.SimpleHasStatus;
import com.onpositive.semantic.model.tree.AbstractClusterizationPointProvider;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.tree.IClusterizationPointProvider;
import com.onpositive.semantic.model.tree.IRealmBasedTreeNode;
import com.onpositive.semantic.model.tree.ITreeChangeListener;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.tree.LeafNode;
import com.onpositive.semantic.model.tree.RelationClusterizationPoint;

@SuppressWarnings("unchecked")
public class RealmTreeNode extends RemoteTreeNode implements
		IRealmBasedTreeNode<Object>, IRealm {

	SimpleHasStatus gStatus;

	private final class TreeRealm extends ParentedRealm<Object> implements
			IDescribableToQuery {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private TreeRealm(IRealm owner) {
			super(owner);
			status = new SimpleHasStatus() {
				@Override
				public CodeAndMessage getStatus() {
					CodeAndMessage status = super.getStatus();
					if (filteredContents != null) {
						for (ITreeNode<?> z : getChildren()) {
							IHasStatus adapter = z.getAdapter(IHasStatus.class);
							if (adapter != null) {
								if (adapter.getStatus().isError()) {
									return adapter.getStatus();
								}
								if (adapter.getStatus().isInInitialProgress()
										|| adapter.getStatus().isInProgress()) {
									return CodeAndMessage.IN_PROGRESS_MESSAGE;
								}
							}
						}
					}
					return status;
				}
			};
			registerService(IHasStatus.class, status);
			gStatus = status;
		}

		@Override
		public boolean mayHaveDublicates() {
			return false;
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected Collection<Object> getContentsInternal() {
			Collection contents = owner.getContents();
			ISetDelta<?> processDelta = provider.processDelta(
					(ISetDelta) HashDelta.buildAdd(contents),
					(Collection) Collections.emptySet(), contents);
			Collection<?> addedElements = processDelta.getAddedElements();
			ArrayList<Object> r = new ArrayList<Object>();
			for (Object o : addedElements) {
				RelationClusterizationPoint<?> o2 = (RelationClusterizationPoint) o;
				BasicNode basicNode = new BasicNode(o2, RealmTreeNode.this);
				for (Object oa : contents) {
					if (o2.getRelation().accept(oa)) {
						basicNode.obj.add(oa);
					}
				}
				r.add(basicNode);
			}
			return r;
		}

		@Override
		public boolean adapt(Query query) {
			if (provider instanceof IDescribableToQuery) {
				return ((IDescribableToQuery) provider).adapt(query);
			}
			return false;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected IRealm<Object> r;
	protected IClusterizationPointProvider<?> provider;

	public RealmTreeNode(IClusterizationPointProvider<?> p) {
		super(null, null);
		this.provider = p;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void setRealm(IRealm r) {
		this.r = r;
		if (this.r!=null&&r!=null&&cRealm!=null){
			((TreeRealm)cRealm).refresh();			
		}
	}

	@Override
	public RealmTreeNode getParent() {
		return null;
	}

	protected IRealm<?> cRealm;

	@Override
	public IRealm<?> getChildRealm() {
		return this;
	}

	public IRealm<?> getChildRealm2() {
		if (cRealm == null) {
			cRealm = new TreeRealm(r);
		}
		return cRealm;
	}

	WeakHashMap<Object, BasicNode> ms = new WeakHashMap<Object, BasicNode>();

	@SuppressWarnings({ "rawtypes", "serial" })
	@Override
	public ITreeNode<Object>[] getChildren() {
		Collection<?> contents = getChildRealm().getContents();
		ITreeNode[] iTreeNodes = new ITreeNode[contents.size()];
		int a = 0;
		for (Object o : contents) {
			if (!(o instanceof ITreeNode)) {
				BasicNode basicNode = ms.get(o);
				if (basicNode != null) {
					o = basicNode;
				} else {
					AbstractClusterizationPointProvider pm = (AbstractClusterizationPointProvider) provider;
					final RelationClusterizationPoint createPoint = (RelationClusterizationPoint) pm
							.createPoint(o);
					BasicNode bm = new BasicNode(createPoint, this) {

						FilteringRealm<?> r = null;

						@Override
						public ITreeNode<Object>[] getChildren() {
							if (r == null) {
								
								r = new FilteringRealm(cRealm.getParent(),
										createPoint.getRelation()) {

									{
										status = new SimpleHasStatus() {
											public void setStatus(
													CodeAndMessage status) {
												super.setStatus(status);
												gStatus.setStatus(status);
											};
										};
										registerService(IHasStatus.class, status);
									}

									public synchronized void realmChanged(
											IRealm realmn, ISetDelta delta) {
										super.realmChanged(realmn, delta);
										d();
									};

									protected void fireDelta(ISetDelta dlt) {
										super.fireDelta(dlt);
										d();
									}

									public boolean isListening() {
										return true;
									};
								};

							}
							Collection<?> l = r.getContents();
							ITreeNode[] r = new ITreeNode[l.size()];
							int a = 0;
							for (Object o : l) {
								r[a++] = new LeafNode(this, o);

							}
							return r;
						}

						protected void d() {
							for (ITreeChangeListener<?> m : rz) {
								m.processUnknownTreeChange((ITreeNode) this);
							}
						}

						@Override
						public <T> T getAdapter(Class<T> adapter) {
							T adapter2 = super.getAdapter(adapter);
							if (adapter2 == null&&r!=null) {
								return DefaultMetaKeys.getService(r, adapter);
							}
							return adapter2;
						}
					};
					ms.put(o, bm);
					o = bm;
				}
			}
			iTreeNodes[a++] = (ITreeNode) o;

		}
		return iTreeNodes;
	}

	@Override
	public int size() {
		return getChildRealm2().size();
	}

	@Override
	public boolean contains(Object o) {
		return getChildRealm2().contains(o);
	}

	@Override
	public Iterator iterator() {
		return getChildRealm2().iterator();
	}

	@Override
	public IMeta getMeta() {
		return getChildRealm2().getMeta();
	}

	@Override
	public Collection getContents() {
		return getChildRealm2().getContents();
	}

	@Override
	public void addRealmChangeListener(IRealmChangeListener listener) {
		getChildRealm2().addRealmChangeListener(listener);
	}

	@Override
	public void removeRealmChangeListener(IRealmChangeListener listener) {
		getChildRealm2().removeRealmChangeListener(listener);
	}

	protected class M implements IRealmChangeListener {

		ITreeChangeListener<?> r;

		public M(ITreeChangeListener<?> r) {
			super();
			this.r = r;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((r == null) ? 0 : r.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			M other = (M) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (r == null) {
				if (other.r != null)
					return false;
			} else if (!r.equals(other.r))
				return false;
			return true;
		}

		@Override
		public void realmChanged(IRealm realmn, ISetDelta delta) {
			r.processUnknownTreeChange((ITreeNode) RealmTreeNode.this);
		}

		private RealmTreeNode getOuterType() {
			return RealmTreeNode.this;
		}
	}

	LinkedHashSet<ITreeChangeListener<?>> rz = new LinkedHashSet<ITreeChangeListener<?>>();

	@Override
	public void addChangeListener(ITreeChangeListener listener) {
		rz.add(listener);
		addRealmChangeListener(new M(listener));
	}

	@Override
	public void removeChangeListener(ITreeChangeListener listener) {
		rz.remove(listener);
		removeRealmChangeListener(new M(listener));
	}

	@Override
	public boolean isOrdered() {
		return false;
	}

	@Override
	public boolean mayHaveDublicates() {
		return false;
	}

}
