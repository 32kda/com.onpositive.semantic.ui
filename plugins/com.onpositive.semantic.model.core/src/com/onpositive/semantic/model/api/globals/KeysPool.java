package com.onpositive.semantic.model.api.globals;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class KeysPool {

	protected LinkedHashMap<IKey, IKey> normalizer = new LinkedHashMap<IKey, IKey>();
	protected HashMap<String, String> strs = new HashMap<String, String>();
	
	public Key add(IKey k) {
		IKey iKey = normalizer.get(k);
		if (iKey != null) {
			return (Key) iKey;
		}
		IKey parent = k.getParent();
		String localId = k.getLocalId();
		Key normalize = null;
		if (parent != null) {
			normalize = add(k.getParent());
		}
		String string = strs.get(localId);
		if (string != null) {
			localId = string;
		} else {
			strs.put(localId, localId);
		}
		normalize=new Key(normalize,string);
		normalizer.put(normalize, normalize);
		return normalize;
	}
	
	
}
