package com.onpositive.semantic.model.api.query.memimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.onpositive.semantic.model.api.property.ComputedProperty;
import com.onpositive.semantic.model.api.property.DynamicProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyComparator;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.realm.IResultUpdate;
import com.onpositive.semantic.model.groups.BasicGroupingOperators.Group;

public class InMemoryExecutor implements IQueryExecutor {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 4144715186155946887L;
	Iterable<Object> space;

	public InMemoryExecutor(Iterable<Object> space) {
		super();
		this.space = space;
	}

	@Override
	public QueryResult execute(Query query, IResultUpdate async) {
		if (query.getNormalizedOrQueries().length>1){
			return OrMerger.merge(this, query);
		}
		InMemoryFilter flt = new InMemoryFilter(query.getFilters());
		ArrayList<Object> r = new ArrayList<Object>();
		boolean canFinishEarly = query.getSorting() == null
				&& query.getGroupBy() == null;
		int limit = canFinishEarly ? query.getLimit() : Integer.MAX_VALUE;
		Object offest = query.getCursorToStart();
		int amount = 0;
		int offsetI = (offest instanceof Number) ? ((Number) offest).intValue()
				: 0;
		if(canFinishEarly){
			offsetI=0;
		}
		boolean all=true;
		int sm = limit!=Integer.MAX_VALUE?(limit + offsetI):Integer.MAX_VALUE;
		for (Object q : space) {
			if (flt.accept(q)) {
				amount++;
				if (canFinishEarly&& amount < offsetI) {
					continue;
				}
				r.add(q);
			}
			if (amount >= sm) {
				all=false;
				break;
			}
		}
		if (!canFinishEarly) {
			IProperty relation = new DynamicProperty(query.getSorting());
			if(query.getGroupBy()!=null){
				LinkedHashMap<Object,ArrayList<Object>>rs=new LinkedHashMap<Object,ArrayList<Object>>();
				DynamicProperty dynamicProperty = new DynamicProperty(query.getGroupBy());
				for(Object q:r){
					Object value = dynamicProperty.getValue(q);
					Collection<Object> collectionIfCollection = ValueUtils.toCollectionIfCollection(value);
					if(collectionIfCollection!=null){
						for(Object o:collectionIfCollection){
							ArrayList<Object> lst = rs.get(o) ;
							if( lst == null ){
								lst = new ArrayList<Object>() ;
								rs.put(o, lst) ;
							}
							lst.add(q);
						}
					}
					else{
						ArrayList<Object> lst = rs.get(value) ;
						if( lst == null ){
							lst = new ArrayList<Object>() ;
							rs.put(value, lst) ;
						}
						lst.add(q);
					}
				}
				ArrayList<Object> gList = new ArrayList<Object>() ;
				for( Map.Entry<Object, ArrayList<Object>> entry : rs.entrySet() ){
					Object obj = entry.getKey() ;
					ArrayList<Object> lst = entry.getValue() ;
					Group gr = new Group(obj, dynamicProperty, lst.toArray() ) ;
					gList.add(gr) ;
				}
				//TODO CHECK ME
				r=gList;//new ArrayList<Object>(rs);
				if (query.getSorting() != null) {
					relation=new ComputedProperty() {
						
						/**
						 * 
						 */
						private static final long serialVersionUID = 5625164944208425526L;

						@Override
						public Object getValue(Object obj) {
							return obj;
						}
					};
					if (query.getSorting().equals(query.getGroupBy())){
						
					}
					
					PropertyComparator propertyComparator = new PropertyComparator(
							relation);
					Collections.sort(r, propertyComparator);
					if (query.isAscendingSort()) {
						Collections.reverse(r);
					}
				}	
				QueryResult queryResult = new QueryResult(r.subList(Math.min(offsetI, r.size()),
						Math.min(query.getLimit()+offsetI, r.size())).toArray());
				queryResult.setCursor(offsetI+queryResult.getResult().length);
				queryResult.setTotalcount((long) r.size());
			}
			if (query.getSorting() != null) {
				PropertyComparator propertyComparator = new PropertyComparator(
						relation);
				Collections.sort(r, propertyComparator);
				if (query.isAscendingSort()) {
					Collections.reverse(r);
				}
			}
			QueryResult queryResult = new QueryResult(r.subList(Math.min(offsetI, r.size()),
					Math.min(query.getLimit()+offsetI, r.size())).toArray());
			queryResult.setCursor(offsetI+queryResult.getResult().length);
			queryResult.setTotalcount((long) r.size());
			return queryResult;
		}
		QueryResult queryResult = new QueryResult(r.toArray());
		queryResult.setCursor(offsetI+r.size());
		if(all){
			queryResult.setTotalcount((long) (offsetI+r.size()));
		}
		return queryResult;
	}

	@Override
	public void cancel(IResultUpdate async) {

	}

	public Iterable<Object> getSpace() {
		return space;
	}

	public void setSpace(Iterable<Object> space) {
		this.space = space;
	}

}
