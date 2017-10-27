package com.onpositive.semantic.model.api.query.memimpl;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.query.memimpl.OrMerger.OffsetCursor;
import com.onpositive.semantic.model.api.realm.IResultUpdate;

public abstract class PartialInMemoryExecutor implements IQueryExecutor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected IQueryExecutor executor;

	public PartialInMemoryExecutor(IQueryExecutor executor) {
		super();
		this.executor = executor;
	}

	@Override
	public QueryResult execute(Query query, IResultUpdate async) {
		if (query.getNormalizedOrQueries().length>1){
			return OrMerger.merge(this, query);
		}
		Query clone = query.clone();
		QueryFilter[] filters = clone.getFilters();
		LinkedHashSet<QueryFilter> inStoreFilters = getInStoreFilters(clone);
		clone.setFilters(inStoreFilters.toArray(new QueryFilter[inStoreFilters
				.size()]));
		ArrayList<QueryFilter> im = new ArrayList<QueryFilter>();
		for (QueryFilter f : filters) {
			if (!inStoreFilters.contains(f)) {
				im.add(f);
			}
		}
		//FIXME - support count
		InMemoryFilter flt = new InMemoryFilter(im.toArray(new QueryFilter[im
				.size()]));
		ArrayList<Object> rs = new ArrayList<Object>();
		int sz = 0;
		int limit = query.getLimit();
		Object cs = null;
		int i = 0;
		Object startCursor = query.getCursorToStart();
		int iof = 0;
		boolean hO=startCursor==null||(startCursor instanceof Integer);
		if (startCursor instanceof OffsetCursor) {
			OffsetCursor oc = (OffsetCursor) startCursor;
			iof = oc.offset;
			hO=true;
		}		
		l2: while (true) {
			clone.setLimit(limit + iof);
			QueryResult execute = executor.execute(clone, null);
			Object[] result = execute.getResult();
			int a = 0;
			for (Object o : result) {
				if (a < iof) {
					a++;
					continue;
				}
				if (sz == limit) {
					// we have all that we need , let set cursor to something
					// sensible

					if (i > 0) {
						Object cursorToStart = execute.getCursor();
						if (cursorToStart instanceof Integer) {
							Integer in = (Integer) cursorToStart;
							cs = in.intValue() - (limit - a);
						} else {
							OffsetCursor of = new OffsetCursor();
							of.original = clone.getCursorToStart();
							of.offset = a;
						}
					} else {
						cs = execute.getCursor();
					}
					break l2;
				}
				if (flt.accept(o)) {
					rs.add(o);
					sz++;
				}
				a++;
			}
			i++;
			iof = 0;
			if (sz == limit) {
				// we have all that we need , let set cursor to something
				// sensible
				Object cursorToStart = execute.getCursor();
				if (cursorToStart instanceof Integer) {
					Integer in = (Integer) cursorToStart;
					cs = in.intValue();
				} else {
					OffsetCursor of = new OffsetCursor();
					of.original = clone.getCursorToStart();
					of.offset = a;
				}
				break l2;
			}
			if (result.length == limit) {
				clone.setCursorToStart(execute.getCursor());
			}
			if (result.length<clone.getLimit()){
				break;
			}
		}
		Object[] array = rs.toArray();
		QueryResult queryResult = new QueryResult(array);
		queryResult.setCursor(cs);
		if (hO&&array.length<limit){
			queryResult.setTotalcount((long) (array.length+iof));
		}
		return queryResult;
	}

	protected abstract LinkedHashSet<QueryFilter> getInStoreFilters(Query clone);

	@Override
	public void cancel(IResultUpdate async) {

	}

}
