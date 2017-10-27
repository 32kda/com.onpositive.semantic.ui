package com.onpositive.semantic.model.api.property.java;

import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.api.globals.IKey;
import com.onpositive.semantic.model.api.globals.IKeyResolver;
import com.onpositive.semantic.model.api.globals.Key;
import com.onpositive.semantic.model.api.id.IIdentifierProvider;
import com.onpositive.semantic.model.api.id.IdAccess;
import com.onpositive.semantic.model.api.labels.ILabelLookup;
import com.onpositive.semantic.model.api.labels.NotFoundException;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IProperty;

final class DefaultKeyResolver implements IKeyResolver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final IHasMeta meta;
	IKey key;

	DefaultKeyResolver(IHasMeta meta) {
		this.meta = meta;
	}

	@SuppressWarnings("rawtypes")
	public Object resolveKey(String key) {
		IIdentifierProvider service = DefaultMetaKeys.getService(meta,
				IIdentifierProvider.class);
		Object vl = key;
		if (key.equals("@meta")){
			return meta;
		}
		IProperty pr = DefaultMetaKeys.getValue(meta,
				DefaultMetaKeys.PROP_ID_KEY, IProperty.class);
		if (pr != null) {
			Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(pr);
			if (subjectClass != String.class) {
				ILabelLookup service2 = DefaultMetaKeys.getService(meta,
						ILabelLookup.class);
				if (service2 != null) {
					try {
						vl = service2.lookUpByLabel(meta, null, key);
					} catch (NotFoundException e) {
						return null;
					}
				}
			}
		}
		if (service != null) {
			return service.getObject(meta, null, vl);
		}
		return null;
	}

	public IKey getKey(Object obj) {
		Object id = IdAccess.getId(obj);
		IHasMeta meta2 = MetaAccess.getMeta(obj);

		if (id != null) {
			if (key == null) {
				Object value2 = DefaultMetaKeys.getValue(meta2,
						DefaultMetaKeys.OBJECT_KEY, Object.class, null);
				if (value2 instanceof IKey) {
					return (IKey) value2;
				}
				if (value2 instanceof String) {
					String value = (String) value2;
					if (value != null) {
						return new Key(GlobalAccess.stringToKey(value),
								id.toString());
					}
				}
				Class<?> z = DefaultMetaKeys.getSubjectClass(meta);
				key = new Key(BeanMetaProvider.CLASS_KEY, z.getName());
			}
			return new Key(key, id.toString());
		}
		return null;
	}
}