package com.onpositive.semantic.model.api.id;

import com.onpositive.semantic.model.api.expressions.LyfecycleUtils;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.RealmAccess;
import com.onpositive.semantic.model.api.validation.IFindAllWithSimilarValue;

public class SimpleRealmBasedIdentifierProvider implements
		IIdentifierProvider<Object> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public Object getObject(IHasMeta meta, Object parent, Object id) {
		IRealm<Object> realm = RealmAccess.getRealm(meta, parent, null);
		try{
		if (realm != null) {
			IIdentifierProvider<?>az=DefaultMetaKeys.getService(realm,IIdentifierProvider.class);
			if (az!=null){
				return az.getObject(meta, parent, id);
			}
			IFindAllWithSimilarValue service = DefaultMetaKeys.getService(
					realm, IFindAllWithSimilarValue.class);
			if (service != null) {
				Iterable<Object> find = service.find(IdProperty.INSTANCE, parent, id, IdProperty.INSTANCE);
				if (find != null && find.iterator().hasNext()) {
					return find.iterator().next();
				}
			}
			for (Object o : realm) {
				Object id2 = IdAccess.getId(meta, parent, o);
				if (id2 != null && id2.equals(id)) {
					return o;
				}
			}
		}
		}finally{
			LyfecycleUtils.disposeIfShortLyfecycle(realm);
		}
		return null;
	}

	
	@Override
	public Object getId(IHasMeta meta, Object parent, Object object2) {
		if (object2 instanceof IIdentifiableObject) {
			IIdentifiableObject obj = (IIdentifiableObject) object2;
			return obj.getId();
		}
		IProperty value = DefaultMetaKeys.getValue(meta,
				DefaultMetaKeys.PROP_ID_KEY, IProperty.class);
		if (value != null) {
			return value.getValue(object2);
		}
		return null;
	}

}
