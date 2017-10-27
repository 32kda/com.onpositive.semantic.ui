package com.onpositive.semantic.model.api.id;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IProperty;

public class IdAccess {

	public static Object getId(Object object) {
		return getId(MetaAccess.getMeta(object), null, object);
	}

	public static Object getId(IHasMeta meta, Object object2) {
		return getId(meta, null, object2);
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getId(IHasMeta meta, Object parent, Object object2) {
		if (object2 instanceof IIdentifiableObject) {
			IIdentifiableObject obj = (IIdentifiableObject) object2;
			return obj.getId();
		}
		IIdentifierProvider service = DefaultMetaKeys.getService(meta,
				IIdentifierProvider.class);
		if (service != null) {
			return service.getId(meta,parent,object2);
		}
		IProperty value = DefaultMetaKeys.getValue(meta,
				DefaultMetaKeys.PROP_ID_KEY, IProperty.class);
		if (value != null) {
			return value.getValue(object2);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes" })
	public static Object getObject(IHasMeta meta, Object parent,Object id) {
		IIdentifierProvider service = DefaultMetaKeys.getService(meta,
				IIdentifierProvider.class);
		if (service!=null){
			return service.getObject(meta,parent,id);
		}
		return null;
	}

	public static Object getObject(Object metaContext, Object id) {
		return getObject(MetaAccess.getMeta(metaContext),null, id);
	}
	
	
}