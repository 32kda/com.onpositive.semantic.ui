package com.onpositive.semantic.model.api.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

public final class Query implements Serializable, IHasMeta, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String kind;

	public static final int OBJECT = 0;
	public static final int KEYS_ONLY = 1;
	public static final int COUNT = 2;
	protected String[] interestingColumns;

	public String[] getInterestingColumns() {
		return interestingColumns;
	}

	public void setInterestingColumns(String[] interestingColumns) {
		this.interestingColumns = interestingColumns;
	}

	public Query(Class<?> kind) {
		this.kind = kind.getName();
	}

	public Query(String kind) {
		super();
		this.kind = kind;
	}

	protected ArrayList<QueryFilter> filters = new ArrayList<QueryFilter>();

	protected String sorting;
	
	protected Map<String,String> aggregators = new HashMap<String, String>();

	protected int mode;

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public QueryFilter[] getFilters() {
		return filters.toArray(new QueryFilter[filters.size()]);
	}

	public void setFilters(QueryFilter[] filters) {
		this.filters = new ArrayList<QueryFilter>(Arrays.asList(filters));
	}

	ArrayList<Query> childOrQueries;

	public void addOr(Query e, Query e1) {
		if (childOrQueries == null) {
			childOrQueries = new ArrayList<Query>();
		}
		childOrQueries.add(e);
		childOrQueries.add(e1);
	}

	transient Query[] normalized;

	public Query[] getNormalizedOrQueries() {
		if (normalized != null) {
			return normalized;
		}
		if (childOrQueries == null) {
			Query[] queries = new Query[] { this };
			normalized = queries;
			return queries;
		}
		ArrayList<Query> rr = new ArrayList<Query>();
		for (Query q : childOrQueries) {
			Query[] normalizedOrQueries = q.getNormalizedOrQueries();
			for (Query qw : normalizedOrQueries) {
				qw.filters.addAll(this.filters);
				qw.setSorting(this.getSorting());
				qw.setAscendingSort(this.isAscendingSort());
				qw.setLimit(this.limit);
				qw.setMode(this.mode);
				qw.setKind(this.kind);
				qw.setGroupBy(this.groupBy);
				rr.add(qw);
			}
		}
		Query[] array = rr.toArray(new Query[rr.size()]);
		normalized = array;
		return array;
	}

	public String getSorting() {
		return sorting;
	}

	public void setSorting(String sorting) {
		this.sorting = sorting;
	}

	protected boolean ascendingSort;

	protected Object cursorToStart;

	public Object getCursorToStart() {
		return cursorToStart;
	}

	public void setCursorToStart(Object cursorToStart) {
		this.cursorToStart = cursorToStart;
	}

	protected int limit = 100;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void addFilter(QueryFilter fl) {
		filters.add(fl);
		if (this.cursorToStart!=null){
			this.cursorToStart=0;
		}
	}
	
	public void removeFilter(QueryFilter fl) {
		filters.remove(fl);
	}
	
	public void addColumn(String q){
		LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>(Arrays.asList(this.interestingColumns));
		linkedHashSet.add(q);
		this.interestingColumns=linkedHashSet.toArray(new String[linkedHashSet.size()]);
	}
	public void removeColumn(String q){
		LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>(Arrays.asList(this.interestingColumns));
		linkedHashSet.remove(q);
		this.interestingColumns=linkedHashSet.toArray(new String[linkedHashSet.size()]);
	}


	boolean noResults;

	public void setNoResults(boolean noResults) {
		this.noResults = noResults;
	}

	public void setAscendingSort(boolean inverse) {
		this.ascendingSort = inverse;
	}

	public boolean isAscendingSort() {
		return ascendingSort;
	}

	protected String groupBy;

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String id) {
		this.groupBy = id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ascendingSort ? 1231 : 1237);
		result = prime * result
				+ ((cursorToStart == null) ? 0 : cursorToStart.hashCode());
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
		result = prime * result + ((groupBy == null) ? 0 : groupBy.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + limit;
		result = prime * result + mode;
		result = prime * result + (noResults ? 1231 : 1237);
		result = prime * result + ((sorting == null) ? 0 : sorting.hashCode());
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
		Query other = (Query) obj;
		if (ascendingSort != other.ascendingSort)
			return false;
		if (cursorToStart == null) {
			if (other.cursorToStart != null)
				return false;
		} else if (!cursorToStart.equals(other.cursorToStart))
			return false;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!filters.equals(other.filters))
			return false;
		if (groupBy == null) {
			if (other.groupBy != null)
				return false;
		} else if (!groupBy.equals(other.groupBy))
			return false;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		if (limit != other.limit)
			return false;
		if (mode != other.mode)
			return false;
		if (noResults != other.noResults)
			return false;
		if (sorting == null) {
			if (other.sorting != null)
				return false;
		} else if (!sorting.equals(other.sorting))
			return false;
		return true;
	}

	BaseMeta meta = new BaseMeta();

	@Override
	public IMeta getMeta() {
		return meta;
	}

	public Query preprocess() {
		try {
			IQueryPreprocessorProvider service = DefaultMetaKeys.getService(
					(IMeta) MetaAccess.getMeta(Class.forName(kind)),
					IQueryPreprocessorProvider.class);
			if (service==null){
				return this;
			}
			IQueryPreProcessor[] preprocessors = service.getPreprocessors(this);
			Query c = this;
			if (preprocessors != null) {
				for (IQueryPreProcessor p : preprocessors) {
					c=p.preProcess(c);
				}
			}
			return c;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
	}

	public Query clone() {
		try {
			Query clone = (Query) super.clone();
			clone.meta = new BaseMeta(this.meta);
			clone.filters = new ArrayList<QueryFilter>(this.filters);
			clone.normalized = null;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}

	public boolean isNoResults() {
		return noResults;
	}
	
	public void setAggregator(String fieldId, String aggregatorId) {
		if (aggregatorId == null || aggregatorId.length() == 0)
			aggregators.remove(fieldId);
		else
			aggregators.put(fieldId,aggregatorId);
	}
	
	public String getAggregator(String fieldId) {
		return aggregators.get(fieldId);
	}
	
	public Map<String, String> getAggregators()
	{
		return aggregators;
	}
	
	
}
