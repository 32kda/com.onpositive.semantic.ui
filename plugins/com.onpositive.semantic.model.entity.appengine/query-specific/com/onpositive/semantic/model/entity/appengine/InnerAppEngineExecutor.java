package com.onpositive.semantic.model.entity.appengine;

import java.util.ArrayList;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.code.twig.CommandTerminator;
import com.google.code.twig.FindCommand;
import com.google.code.twig.FindCommand.RootFindCommand;
import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider.ClassPropertyInfo;
import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.realm.IResultUpdate;

public class InnerAppEngineExecutor implements IQueryExecutor {

	public QueryResult execute(Query query) {
		// create a new light-weight stateful datastore for every request
		ObjectDatastore datastore = new AnnotationObjectDatastore();
		QueryFilter[] filters = query.getFilters();
		FindCommand find = datastore.find();
		RootFindCommand<Object> type = null;
		ClassPropertyInfo properties = null;
		try {
			Class<?> forName = Class.forName(query.getKind());
			properties = JavaPropertyProvider.instance.getProperties(forName);
			type = find.type(forName);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
		for (QueryFilter f : filters) {
			String filterKind = f.getFilterKind();
			FilterOperator operator = getOperator(filterKind);
			String propId = f.getPropId();
			if (!propId.equals(QueryFilter.LABEL_PROPERTY)) {
				IProperty property = properties.getProperty(propId);
				boolean value = DefaultMetaKeys.getValue(property,
						DefaultMetaKeys.ID_KEY);
				if (value) {
					propId = Entity.KEY_RESERVED_PROPERTY;
				}
			}
			if (operator != null) {
				type.addFilter(propId, operator, f.getFilterConstraint());
			} else {
				if (filterKind.equals(QueryFilter.FILTER_STARTS_WITH)) {
					type.addFilter(f.getPropId(),
							FilterOperator.GREATER_THAN_OR_EQUAL, f
									.getFilterConstraint().toString());
					type.addFilter(f.getPropId(),
							FilterOperator.LESS_THAN_OR_EQUAL, f
									.getFilterConstraint().toString()
									+ Character.MAX_VALUE);
				}
				if (filterKind.equals(QueryFilter.FILTER_NOT_EQUALS)) {
					// dublicate
					type.addFilter(f.getPropId(), FilterOperator.NOT_EQUAL, f
							.getFilterConstraint().toString()
							+ Character.MAX_VALUE);
				}
			}
		}
		String sorting = query.getSorting();

		if (sorting != null && !sorting.isEmpty()) {
			IProperty property = properties.getProperty(sorting);
			boolean value = DefaultMetaKeys.getValue(property,
					DefaultMetaKeys.ID_KEY);
			if (value) {
				sorting = Entity.KEY_RESERVED_PROPERTY;
			}
			type.addSort(sorting,
					query.isAscendingSort() ? SortDirection.ASCENDING
							: SortDirection.DESCENDING);
		}
		if (query.getMode() == Query.KEYS_ONLY) {
			type.unactivated();
		}
		int offset=-1;
		Object cursorToStart = query.getCursorToStart();
		if (cursorToStart != null) {
			if (cursorToStart instanceof Number) {
				Number tn = (Number) cursorToStart;
				int intValue = tn.intValue();
				offset=intValue;
				type.startFrom(intValue);
			} else {
				type.continueFrom(Cursor.fromWebSafeString(cursorToStart
						.toString()));
			}
		}
		else{
			offset=0;
		}
		int limit = query.getLimit();
		type.fetchFirst(limit).fetchMaximum(limit);
		if (query.getMode() == Query.COUNT) {
			CommandTerminator<Integer> returnCount = type.returnCount();
			Integer now = returnCount.now();
			QueryResult r = new QueryResult();
			r.setTotalcount(now.longValue());
			return r;
		}
		QueryResultIterator<Object> now = type.now();
		
		ArrayList<Object> next = new ArrayList<Object>(limit==Integer.MAX_VALUE?100:limit);
		while (now.hasNext()) {
			next.add(now.next());
		}
		Object[] array = next.toArray();
		QueryResult r = new QueryResult(array);
		if (array.length<limit){
			if (offset!=-1){
				r.setTotalcount((long) (array.length+offset));
			}
		}
		r.setCursor(now.getCursor().toWebSafeString());
		
		return r;
	}

	private FilterOperator getOperator(String filterKind) {
		if (filterKind.equals(QueryFilter.FILTER_GE)) {
			return FilterOperator.GREATER_THAN_OR_EQUAL;
		}
		if (filterKind.equals(QueryFilter.FILTER_LE)) {
			return FilterOperator.LESS_THAN_OR_EQUAL;
		}
		if (filterKind.equals(QueryFilter.FILTER_GT)) {
			return FilterOperator.GREATER_THAN;
		}
		if (filterKind.equals(QueryFilter.FILTER_LT)) {
			return FilterOperator.LESS_THAN;
		}
		if (filterKind.equals(QueryFilter.FILTER_EQUALS)) {
			return FilterOperator.EQUAL;
		}
		if (filterKind.equals(QueryFilter.FILTER_NOT_EQUALS)) {
			return FilterOperator.EQUAL;
		}
		return null;
	}

	@Override
	public QueryResult execute(Query query, IResultUpdate async) {
		return execute(query);
	}

	@Override
	public void cancel(IResultUpdate async) {

	}

}
