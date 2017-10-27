package com.onpositive.semantic.ui.businessdroids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.filters.AbstractColumnFilter;
import com.onpositive.businessdroids.model.filters.BasicStringFilter;
import com.onpositive.businessdroids.model.filters.BooleanFilter;
import com.onpositive.businessdroids.model.filters.ComparableFilter;
import com.onpositive.businessdroids.model.filters.ExplicitValueFilter;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.model.groups.IFieldGroupingCalculator;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.model.groups.IHasId;
import com.onpositive.businessdroids.ui.dataview.IGroupAwareTableModel;
import com.onpositive.semantic.model.api.labels.LabelProperty;
import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.realm.IDescribableToQuery;
import com.onpositive.semantic.model.groups.BasicGroupingOperators.Group;

public class QueryBasedTableModel extends TableModel implements IGroupAwareTableModel{

	protected final IQueryExecutor dataCollection;
	protected QueryResult queryResult;
	protected Query query;

	public QueryBasedTableModel(IColumn[] columns, IQueryExecutor dataCollection) {
		super(columns);
		this.dataCollection = dataCollection;
		query = new Query("query");
		query.setLimit(Integer.MAX_VALUE);
	}
	
	protected HashMap<String, IAggregator>agMap=new HashMap<String, IAggregator>();
	
	
	@Override
	protected void onAggregatorChanged(IAggregator oldAggregator,
			IAggregator newAggregator, IColumn column) {
		String id = column.getId();
		agMap.put(id,newAggregator);
		if (newAggregator==null){
			query.setAggregator(id,null);
		}
		else{
			query.setAggregator(id, aggregatorId(newAggregator));
		}
		updateResult();
		super.onAggregatorChanged(oldAggregator, newAggregator, column);
	}

	private String aggregatorId(IAggregator newAggregator) {
		return newAggregator.getId();
	}

	@Override
	public Iterator<Object> iterator() {
		Object[] result = queryResult.getResult();
		return Arrays.asList(result).iterator();
	}

	@Override
	protected void onFiltersChanged(List<IFilter> newFilters) {
		
		//it is important that query.setFilters() was always called with the forgoing cycle.  
		query.setFilters(createFilters(newFilters));
		
		for( IFilter f : newFilters ){
			if( !(f instanceof SemanticUIFilterWrapper) )
				continue ;
				
			SemanticUIFilterWrapper wrapper = (SemanticUIFilterWrapper) f ;
			com.onpositive.semantic.model.api.realm.IFilter semanticFilter = wrapper.getSemanticFilter();
			if( !(semanticFilter instanceof IDescribableToQuery) )
				continue ;
			
			((IDescribableToQuery)semanticFilter).adapt(query) ;
		}
		updateResult();
	}
	
	@Override
	public Object getAggregatedValue(IAggregator agr, IField fld) {
		if (queryResult!=null){
			return queryResult.getAggregatorValue(fld.getId());
		}
		return super.getAggregatedValue(agr, fld);
	}

	@SuppressLint("DefaultLocale")
	@SuppressWarnings("rawtypes")
	protected QueryFilter[] createFilters(List<IFilter> newFilters) {
		List<QueryFilter> result = new ArrayList<QueryFilter>();
		for (IFilter filter : newFilters) {
			if (filter instanceof BasicStringFilter){
				BasicStringFilter s=(BasicStringFilter) filter;
				result.add(new QueryFilter(LabelProperty.INSTANCE.getId(), s.getString().toLowerCase(), QueryFilter.FILTER_CONTAINS));				
			}
			else if (filter instanceof AbstractColumnFilter) {
				if (filter instanceof BooleanFilter) {
					BooleanFilter fl = (BooleanFilter) filter;
					result.add(new QueryFilter(fl.getColumn().getId(), fl
							.getValue(), QueryFilter.FILTER_EQUALS));
					continue;
				}
				if (filter instanceof ComparableFilter) {
					ComparableFilter fl=(ComparableFilter) filter;
					Comparable max = fl.getMax();
					Comparable min = fl.getMin();
					if (filter instanceof CompareUnitFilter){
						CompareUnitFilter m=(CompareUnitFilter) filter;
						max=m.getMaxActual();
						min=m.getMinActual();
					}
					if (max!=null){
					result.add(new QueryFilter(fl.getColumn().getId(), max, QueryFilter.FILTER_LE));
					}
					if (min!=null){
					result.add(new QueryFilter(fl.getColumn().getId(), min, QueryFilter.FILTER_GE));
					}
				}
				if (filter instanceof ExplicitValueFilter){
					ExplicitValueFilter fl=(ExplicitValueFilter) filter;
					result.add(new QueryFilter(fl.getColumn().getId(), fl.getValues(), QueryFilter.FILTER_ONE_OF));
				}				
			}
		}
		return result.toArray(new QueryFilter[0]);
	}

	@Override
	public Long getUnfilteredItemCount() {
		QueryResult queryResult2 = getQueryResult();
		return queryResult2.getTotalKindcount();
	}

	@Override
	public int getItemCount() {
		return getQueryResult().getTotalcount().intValue();
	}

	public QueryResult getQueryResult() {
		if (queryResult == null)
			queryResult = dataCollection.execute(query, null);
		return queryResult;
	}

	@Override
	public Object getItem(int i) {
		Object result = queryResult.getResult()[i];
		if (result instanceof Group) {
			result = createDroidsGroup((Group) result);
		}
		return result;
	}

	protected Object createDroidsGroup(Group result) {
		com.onpositive.businessdroids.ui.dataview.Group group = new com.onpositive.businessdroids.ui.dataview.Group(
				this,
				((IFieldGroupingCalculator) currentGroupingCalculator)
						.getGroupField(), result.getElement(),
				getChildren(result));
		return group;
	}

	protected Object[] getChildren(Group result) {
		Object[] children = new Object[result.getChildrenCount()];
		for (int i = 0; i < result.getChildrenCount(); i++) {
			children[i] = result.getChild(i);
		}
		return children;
	}

	@Override
	protected void internalSort(IField sortField2, boolean ascending) {
		query.setSorting(sortField2.getId());
		query.setAscendingSort(ascending);
		updateResult();
	}

	@Override
	public void setCurrentGrouping(IGroupingCalculator currentGroupingCalculator) {
		
		if (currentGroupingCalculator instanceof IHasId) {
			query.setGroupBy(((IHasId) currentGroupingCalculator).getId());
			updateResult();
		} else if (currentGroupingCalculator instanceof IFieldGroupingCalculator) {
			IField groupField = ((IFieldGroupingCalculator) currentGroupingCalculator)
					.getGroupField();
			query.setGroupBy(groupField.getId());
			updateResult();
		} else if (currentGroupingCalculator == null) {
			query.setGroupBy(null);
			updateResult();
		}
		super.setCurrentGrouping(currentGroupingCalculator);
	}

	protected void updateResult() {
		queryResult = dataCollection.execute(query, null);

	}

	protected void setAggregator(IColumn column, IAggregator aggregator) {
		if (aggregator instanceof IHasId) {
			query.setAggregator(column.getId(), ((IHasId) aggregator).getId());
			updateResult();
		}
	}

	public void setVisibleColumns(Collection<IColumn> columns) {
		String[] ids=new String[columns.size()];
		int a=0;
		for (IColumn m:columns){
			
			ids[a++]=m.getId();
		}
		query.setInterestingColumns(ids);
	}

}