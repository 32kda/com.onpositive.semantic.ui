package com.onpositive.semantic.model.platform.registry;

import java.util.ArrayList;

import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.semantic.model.api.changes.DefaultListenersProvider;
import com.onpositive.semantic.model.api.changes.IObjectListener;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;

public class ListenerRegistry extends
		AbstractPlatformServiceProvider<ServiceListenerObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ListenerRegistry() {
		super("com.onpositive.semantic.model.objectListener",
				ServiceListenerObject.class);
	}

	public Object doResolve(IHasMeta original, Class<?> subjectClass,
			Object genericRegistryObject) {
		if (genericRegistryObject instanceof ArrayList) {
			ArrayList<?> r = (ArrayList<?>) genericRegistryObject;
			DefaultListenersProvider lp = new DefaultListenersProvider();
			for (Object q : r) {
				ServiceListenerObject m = (ServiceListenerObject) q;
				lp.add((IObjectListener) m.getProvider());
			}
			map.put(subjectClass, lp);
			return lp;
		} else {
			Object object = null;
			DefaultListenersProvider lp = new DefaultListenersProvider();
			lp.add((IObjectListener) ((ServiceListenerObject) genericRegistryObject)
					.getProvider());
			map.put(subjectClass, lp);
			return lp;
		}
	}
}
