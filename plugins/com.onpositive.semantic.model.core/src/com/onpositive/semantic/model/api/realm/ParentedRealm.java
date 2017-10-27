package com.onpositive.semantic.model.api.realm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.INotifyableCollection;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.status.SimpleHasStatus;

@SuppressWarnings("rawtypes")
public abstract class ParentedRealm<T> extends AbstractRealm<T> implements
		IRealmChangeListener<T>, IValueListener, INotifyableCollection<T>,
		ICanRefresh, IPotentiallyIncomplete, INotDetailedListener<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected SimpleHasStatus status = new SimpleHasStatus(){
		
		@Override
		public void statusChanged(IHasStatus bnd, CodeAndMessage cm){
			super.statusChanged(bnd, cm);
			if (ParentedRealm.this.owner instanceof ParentedRealm&&isListening()){
				ParentedRealm e=(ParentedRealm) owner;
				Collection<T> contentsInternal = e.filteredContents;
				if (contentsInternal!=null){
				if (filteredContents==null){
					filteredContents=new HashSet<Object>();
				}
				HashDelta buildFrom = HashDelta.buildFrom(filteredContents, contentsInternal);
				filteredContents=contentsInternal;
				fireDelta(buildFrom);
				}
			}
			
		}
	};

	@Override
	@SuppressWarnings("unchecked")
	public void changed(ISetDelta<?> dlt) {
		fireDelta((ISetDelta<T>) dlt);
	}

	public ParentedRealm(IRealm owner) {
		super();
		this.owner = owner;
		this.parentMeta = owner != null ? owner.getMeta() : null;
		registerService(IHasStatus.class, status);
		registerService(ICanRefresh.class, this);
		registerService(IPotentiallyIncomplete.class, this);
	}

	@Override
	public boolean isIncompleteDataHere() {
		boolean b = potential == null || potential > size();
		return cursor != null && b;
	}

	@Override
	public Integer potentialSize() {
		return potential;
	}

	@Override
	public void loadMoreData() {
		int code = status.getStatus().getCode();
		if (lastQuery != null && code >= 0) {
			lastQuery.setCursorToStart(cursor);
			IQueryExecutor service = DefaultMetaKeys.getService(this,
					IQueryExecutor.class);
			if (service != null) {
				QueryResult execute = service.execute(lastQuery, r);
				// execute.setOriginal(qm);
				boolean inInitialProgress = execute.getStatus()
						.isInInitialProgress();
				if (inInitialProgress) {
					status.setStatus(new CodeAndMessage(
							CodeAndMessage.IN_PROGRESS_SOME_DATA, ""));
				}
				if (execute != null && !inInitialProgress) {
					r.fetched(execute);
					cursor=execute.getCursor();
				}

			}
		}
	}

	protected IRealm owner;

	@Override
	public Iterator<T> iterator() {
		return this.getContents().iterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	public IRealm<T> getParent() {
		return this.owner;
	}

	protected Collection filteredContents;

	@Override
	@SuppressWarnings("unchecked")
	public synchronized void realmChanged(IRealm realmn, ISetDelta delta) {
		if (isListening()) {
			if (filteredContents != null || hasDetailedListeners()) {
				Collection contentsInternal = getContentsInternal2();
				int code = status.getStatus().getCode();
				if (code == CodeAndMessage.IN_PROGRESS_NO_DATA) {
					return;
				}
				HashDelta buildFrom = HashDelta.buildFrom(
						filteredContents != null ?  filteredContents
								: Collections.emptySet(), contentsInternal);
				filteredContents = contentsInternal;
				size = filteredContents.size();
				if (delta!=null&&delta.isOrderChanged()){
					buildFrom.setOrderChanged(true);
				}
				fireDelta(buildFrom);
			}
			else{
				fireDelta(null);
			}
			
		} else {
			size = -1;
		}
	}

	@Override
	public boolean contains(Object o) {
		if (filteredContents != null) {
			return filteredContents.contains(o);
		}
		return getContents().contains(o);
	}

	Object cursor;
	Integer potential;

	private int size = -1;

	@Override
	public void refresh() {
		if (owner instanceof ICanRefresh){
			ICanRefresh rc=(ICanRefresh) owner;
			rc.refresh();
		}
		if (filteredContents != null || isListening()) {
			Collection contentsInternal = getContentsInternal2();
			
			if (status.getStatus().getCode() == CodeAndMessage.IN_PROGRESS_NO_DATA) {
				// do not do anything at this moment;
				return;
			}
			
			HashDelta buildFrom = HashDelta.buildFrom(
					filteredContents != null ? filteredContents : Collections
							.emptySet(), contentsInternal);
			filteredContents = contentsInternal;
			size = filteredContents.size();
			fireDelta(buildFrom);
			
			
		} else {
			size = -1;
		}
	}

	@Override
	public int size() {
		if (size == -1) {
			getContents();
		}
		return size;
	}

	@Override
	public synchronized Collection<T> getContents() {
		if (filteredContents != null) {
			return copy(filteredContents);
		}
		Collection<T> contentsInternal = getContentsInternal2();
		size = contentsInternal.size();
		if (isListening()) {
			this.filteredContents = contentsInternal;

		}
		return contentsInternal;
	}

	protected IResultUpdate r = new IResultUpdate() {

		@SuppressWarnings("unchecked")
		@Override
		public void fetched(QueryResult rs) {
			setPotential(rs);
			List<Object> asList = Arrays.asList(rs.getResult());
			if (filteredContents != null) {
				boolean fromNoData = status.getStatus().getCode() == CodeAndMessage.IN_PROGRESS_NO_DATA;
				HashDelta buildFrom = (HashDelta) (fromNoData ? HashDelta
						.buildFrom(filteredContents, asList) : HashDelta
						.buildAdd(asList));
				if (fromNoData) {
					filteredContents.clear();
					
				}
				filteredContents.addAll(asList);
				size = filteredContents.size();
				fireDelta(buildFrom);

			}

			ParentedRealm.this.status.setStatus(rs.getStatus());
		}
	};
	Query lastQuery;

	@SuppressWarnings("unchecked")
	private Collection<T> getContentsInternal2() {
		cancelIfNeeded();
		QueryResult query = RealmQueryImpl.query(this, r);
		if (query != null) {
			lastQuery = query.getOriginal();
			CodeAndMessage status2 = query.getStatus();
			setPotential(query);
			if (status2 != null) {
				status.setStatus(status2);
			} else {
				status.setStatus(CodeAndMessage.OK_MESSAGE);
			}
			return ((Collection<T>) new ArrayList(Arrays.asList(query
					.getResult())));
		}
		return getContentsInternal();
	}

	protected void setPotential(QueryResult rs) {
		cursor = rs.getCursor();

		if (rs.getTotalcount() != null) {
			potential = rs.getTotalcount().intValue();
		} else {
			potential = null;
		}
	}

	protected void cancelIfNeeded() {
		if (status.getStatus().isInInitialProgress()) {
			RealmQueryImpl.cancel(this, r);
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<T> copy(Collection filteredContents2) {
		if (mayHaveDublicates()) {
			return new ArrayList(filteredContents2);
		}
		if (isOrdered()) {
			return new LinkedHashSet(filteredContents2);
		}
		return new HashSet(filteredContents2);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dispose() {
		cancelIfNeeded();
		this.owner.removeRealmChangeListener(this);
		status.setParent(null);
		filteredContents = null;
		size = -1;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void startListening() {
		owner.addRealmChangeListener(this);
		if (hasDetailedListeners()) {
			filteredContents = new LinkedHashSet<T>(getContents());
		}
		IHasStatus service = DefaultMetaKeys
				.getService(owner, IHasStatus.class);
		if (service != null) {
			status.setParent(service);
		}
	}

	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		refresh();
	}

	@Override
	protected void stopListening() {
		dispose();
	}

	protected abstract Collection<T> getContentsInternal();

	@Override
	public boolean isOrdered() {
		return this.owner.isOrdered();
	}

}
