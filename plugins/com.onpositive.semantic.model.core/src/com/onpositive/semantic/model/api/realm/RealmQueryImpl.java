package com.onpositive.semantic.model.api.realm;

import java.util.ArrayList;
import java.util.Collections;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class RealmQueryImpl {

	public static void cancel(IRealm<?>r,IResultUpdate update){
		while (r.getParent() != null) {
			r = r.getParent();
		}		
		IQueryExecutor service = DefaultMetaKeys.getService(r,
				IQueryExecutor.class);
		if (service!=null){
			service.cancel(update);
		}
	}
	public static QueryResult query(IRealm<?> r, IResultUpdate r2) {
		return query(r, r2,null,null,null);
	}
	
	public static interface IQueryTransformer{
		
	}
	
	@SuppressWarnings("unchecked")
	public static QueryResult query(IRealm<?> r, IResultUpdate r2,Object offset,Integer limit,IFunction query) {
		IRealm<?>rr=r;
		ArrayList<IRealm<?>> rs = new ArrayList<IRealm<?>>();
		while (r.getParent() != null) {
			rs.add(r);
			r = r.getParent();
		}
		
		Collections.reverse(rs);
		IQueryExecutor service = DefaultMetaKeys.getService(r,
				IQueryExecutor.class);		
		Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(r);
		if (service != null) {
			Query qm = new Query(subjectClass!=null? subjectClass.getName():null);
			for (IRealm<?> p : rs) {
				if (!adapt(p, qm)) {
					return null;
				}
			}
			if (qm.isNoResults()){
				return new QueryResult();
			}
			Integer value = DefaultMetaKeys.getValue(rr, DefaultMetaKeys.LIMIT,Integer.class,null);
			if (value!=null){
				qm.setLimit(value);
			}
			if (offset!=null){
				qm.setCursorToStart(offset);
				
			}
			if (limit!=null){
				qm.setLimit(limit);
			}
			if (query!=null){
				qm=(Query) query.getValue(qm);
			}
			QueryResult execute = service.execute(qm,r2);
			if (execute==null){
				execute=new QueryResult();
				execute.setStatus(CodeAndMessage.errorMessage("Service returned null"));
			}
			execute.setOriginal(qm);
			return execute;
		}
		return null;
	}

	

	private static boolean adapt(IRealm<?> p, Query qm) {
		if (p instanceof IDescribableToQuery) {
			IDescribableToQuery m = (IDescribableToQuery) p;
			return m.adapt(qm);
		}
		return false;
	}

}
