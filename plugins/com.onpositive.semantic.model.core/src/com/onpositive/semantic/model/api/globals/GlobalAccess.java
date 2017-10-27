package com.onpositive.semantic.model.api.globals;

import java.util.LinkedHashMap;

import com.onpositive.semantic.model.api.convert.ConvertAccess;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

public class GlobalAccess {

	public static Object getGlobal(IKey key) {
		Object internalGet = internalGet(key, key);
		if (internalGet instanceof MNHolder) {
			return ((MNHolder) internalGet).result;
		}
		return internalGet;
	}

	static class MNHolder {
		public MNHolder(Object resolveKey) {
			this.result = resolveKey;
		}

		Object result;
	}

	protected static Object internalGet(IKey key, IKey orig) {
		IKey parent = key.getParent();
		if (parent != null) {
			Object global = internalGet(parent, orig);
			if (global instanceof MNHolder) {
				return global;
			}
			if (global == null) {
				return null;
			}
			return resolveChild(global, key.getLocalId());
		}
		Object resolveRoot = resolveRoot(key);
		if (resolveRoot instanceof IFullKeyResolver) {
			IFullKeyResolver m = (IFullKeyResolver) resolveRoot;
			if (m.isReallyFullKey()) {
				Object resolveKey = m.resolveKey(orig);
				if (resolveKey != null) {
					return new MNHolder(resolveKey);
				}

				return resolveKey;
			}
		}
		return resolveRoot;
	}

	private static Object resolveRoot(IKey localId) {
		String localId2 = localId.getLocalId();
		return rs.get(localId2);
	}

	protected static LinkedHashMap<String, IKeyResolver> rs;
	static {
		rs = new LinkedHashMap<String, IKeyResolver>();
		MetaAccess.getMeta(GlobalAccess.class);
	}

	public static void addResolver(String string, IKeyResolver r) {
		rs.put(string, r);
	}

	public static void removeResolver(String rm, IKeyResolver r) {
		rs.remove(rm);
	}

	private static Object resolveChild(Object global, String localId) {
		if (localId.length() == 0) {
			return global;
		}
		if (global instanceof IKeyResolver) {
			IKeyResolver r = (IKeyResolver) global;
			return r.resolveKey(localId);
		}
		IHasMeta meta = MetaAccess.getMeta(global);
		IKeyResolver service = DefaultMetaKeys.getService(meta,
				IKeyResolver.class);
		if (service != null) {
			return service.resolveKey(localId);
		}
		return null;
	}

	public static String keyToString(IKey key) {
		StringBuilder bld = new StringBuilder();
		append(key, bld);
		return bld.toString();
	}

	private static void append(IKey key, StringBuilder bld) {
		IKey parent = key.getParent();
		if (parent != null) {
			append(parent, bld);
			if (parent.getParent() == null) {
				bld.append("://");
			} else {
				bld.append('/');
			}
			bld.append(key.getLocalId().replace('/', (char) 0).toString());
			return;
		}

		Object localId = key.getLocalId();
		String str = localId.toString();

		bld.append(str);
	}

	public static IKey stringToKey(String string) {
		int indexOf = string.indexOf("://");
		if (indexOf != -1) {
			String substring = string.substring(0, indexOf);
			Key p = new Key(null, substring);
			indexOf += 3;
			int indexOf2 = string.indexOf('/', indexOf);
			while (indexOf2 != -1) {
				p = new Key(p, string.substring(indexOf, indexOf2));
				indexOf = indexOf2 + 1;
				indexOf2 = string.indexOf('/', indexOf);
			}
			if (indexOf < string.length()) {
				p = new Key(p, string.substring(indexOf));
			}
			return p;
		}
		throw new IllegalArgumentException();
	}

	public static IKey getKey(Object e) {
		if (e != null) {
			if (e instanceof IKey) {
				return (IKey) e;
			}
			IHasMeta meta = MetaAccess.getMeta(e);
			IKeyResolver service = DefaultMetaKeys.getService(meta,
					IKeyResolver.class);
			if (service != null) {
				return service.getKey(e);
			}
		}
		return null;
	}

	public static String keyString(Object e) {
		IKey key = getKey(e);
		if (key != null) {
			return keyToString(key);
		}
		return null;
	}

	public static Object resolve(String keyString) {
		return getGlobal(stringToKey(keyString));
	}

	public static <T> T resolve(String attrDataUrl, Class<T> class1) {
		Object resolve = resolve(attrDataUrl);
		return ConvertAccess.convert(resolve, class1);
	}
}
