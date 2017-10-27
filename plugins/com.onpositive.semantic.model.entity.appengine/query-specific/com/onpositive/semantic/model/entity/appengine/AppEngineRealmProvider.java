package com.onpositive.semantic.model.entity.appengine;

	import java.util.HashMap;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.query.IPermissionManager;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.realm.FixedTypeTransportRealm;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmChangeListener;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.ObjectListeningRealm;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class AppEngineRealmProvider implements IRealmProvider<Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static HashMap<Class, IRealm<?>>rs=new HashMap<Class, IRealm<?>>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IRealm<Object> getRealm(IHasMeta model, Object parentObject,
			Object object) {
		if (object==null){
			return null;
		}
		if (object instanceof Class){
			IRealm iRealm = null;
			if (iRealm==null){
				Class object2 = (Class) object;
				if (rs.containsKey(object2)){
					return (IRealm<Object>) rs.get(object2);
				}
				final AppEngineExecutor lc = new AppEngineExecutor(new IPermissionManager() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public CodeAndMessage validateQuery(Query q) {
						return CodeAndMessage.OK_MESSAGE;
					}
					
					@Override
					public CodeAndMessage validateCommand(ICommand c) {
						return CodeAndMessage.OK_MESSAGE;
					}
					
					@Override
					public QueryResult adjustResults(QueryResult r, Query q) {
						return r;
					}
				}, new IVariableResolver() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public Object resolveObject(String key, IMeta meta) {
						return null;
					}
				});
				iRealm=new FixedTypeTransportRealm(lc,object2);
				iRealm=new ObjectListeningRealm(iRealm);
				IRealmChangeListener<Object> listener = new IRealmChangeListener<Object>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void realmChanged(IRealm<Object> realmn,
							ISetDelta<Object> delta) {
						if (delta==null){
							return;
						}
						for (Object q:delta.getChangedElements()){
							lc.store(q);
						}
						
					}
				};
				iRealm.addRealmChangeListener(listener);
				rs.put(object2, iRealm);
			}
			return (IRealm<Object>) iRealm;
		}
		else return getRealm(model, parentObject, object.getClass());
	}

}
