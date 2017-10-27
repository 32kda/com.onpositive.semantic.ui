package com.onpositive.semantic.model.api.query.memimpl;

import java.util.WeakHashMap;

import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.realm.IResultUpdate;

public class CachingExecutor implements IQueryExecutor{

	private static final long serialVersionUID = -8461502484552600327L;
	WeakHashMap<Query, QueryResult>cache=new WeakHashMap<Query, QueryResult>();
	IQueryExecutor executor;
	private long maxCache;
	
	public CachingExecutor(IQueryExecutor executor, long maxCache) {
		super();
		this.executor = executor;
		this.maxCache = maxCache;
	}

	@Override
	public QueryResult execute(Query query, IResultUpdate async) {
		Query clone = query.clone();
		QueryResult queryResult = getFromCache(clone);
		long currentTimeMillis = System.currentTimeMillis();
		if (queryResult!=null){
			if (!queryResult.getStatus().isError()){
				
				if (currentTimeMillis-queryResult.getTimeStamp()<maxCache){
					return queryResult;
				}
			}
		}
		QueryResult execute = executor.execute(query, null);
		execute.setTimeStamp(currentTimeMillis);
		cache(clone, execute);
		return execute;
	}

	protected void cache(Query clone, QueryResult execute) {
		cache.put(clone, execute);
	}

	protected QueryResult getFromCache(Query clone) {
		return cache.get(clone);
	}

	@Override
	public void cancel(IResultUpdate async) {		
	}

}
