package com.onpositive.semantic.model.api.query.memimpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import com.onpositive.semantic.model.api.property.DynamicProperty;
import com.onpositive.semantic.model.api.property.PropertyComparator;
import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryResult;

public class OrMerger {

	static class OffsetCursor implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int offset;
		Object original;
	}
	
	@SuppressWarnings("rawtypes")
	public static QueryResult merge(IQueryExecutor d, Query r) {
		Query[] normalizedOrQueries = r.getNormalizedOrQueries();
		if (normalizedOrQueries.length == 1) {
			return d.execute(normalizedOrQueries[0], null);
		}
		Object cursorToStart = r.getCursorToStart();
		Object[] oldCursors = (Object[]) cursorToStart;
		int a = 0;
		LinkedHashSet<Object> mm = new LinkedHashSet<Object>();
		Object[] cursor = new Object[normalizedOrQueries.length];
		LinkedHashSet[] e = new LinkedHashSet[normalizedOrQueries.length];
		for (Query c : normalizedOrQueries) {
			int dlt=0;
			if (oldCursors != null) {
				Object object = oldCursors[a];
				if (object == null) {
					a++;
					continue;
				}
				if (object instanceof OffsetCursor){
					OffsetCursor m=(OffsetCursor) object;
					c.setCursorToStart(m.original);
					c.setLimit(m.offset+r.getLimit());
					dlt=m.offset;
				}
				else{
					c.setCursorToStart(object);
				}
			}
			a++;
			QueryResult execute = d.execute(c, null);
			Object cursor2 = execute.getCursor();
			cursor[a - 1] = cursor2;
			Object[] result = execute.getResult();
			List<Object> asList = Arrays.asList(result);
			if (dlt!=0){
				int size = asList.size();
				asList=asList.subList(Math.min(dlt,size), size);
			}
			mm.addAll(asList);
			e[a - 1] = new LinkedHashSet<Object>(asList);
		}
		ArrayList<Object> arrayList = new ArrayList<Object>(mm);
		if (r.getSorting() != null) {
			Collections.sort(arrayList, new PropertyComparator(
					new DynamicProperty(r.getSorting())));
			if (r.isAscendingSort()) {
				Collections.reverse(arrayList);
			}
		}
		int limit = r.getLimit();
		Object[] array = arrayList.subList(0,
				Math.min(arrayList.size(), limit)).toArray();

		for (int b = 0; b < e.length; b++) {
			if (e[b] == null) {
				continue;
			}
			int count = 0;
			for (Object c : array) {				
				if (e[b].contains(c)) {
					count++;
				}
			}
			if (count!=limit){
				if (count==0){
					cursor[b]=null;
					continue;
				}
				Object object = cursor[b];
				
				if (object instanceof Integer){
					Integer i=(Integer) object;
					int j = i-(limit-count);
					cursor[b]=j;
				}
				else{
					OffsetCursor of=new OffsetCursor();
					of.original=oldCursors[b];
					of.offset=count;
				}
			}
			// okey now we know how much we have consumed from each item;
		}
		// now we can determine how to move offsets
		QueryResult queryResult = new QueryResult(array);
		for (Object m : cursor) {
			if (m != null && queryResult.getResult().length == r.getLimit()) {
				queryResult.setCursor(cursor);
				break;
			}
		}
		return queryResult;
	}
}
