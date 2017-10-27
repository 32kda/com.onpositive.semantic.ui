package com.onpositive.core.runtime;

import java.util.HashMap;

import com.onpositive.commons.platform.registry.ServiceMap;
import com.onpositive.commons.utils.Pair;

public class AdapterRegistry extends ServiceMap<AdapterDefinition> {

	public AdapterRegistry(String extPoint) {
		super(extPoint,
				AdapterDefinition.class);
	}
	@SuppressWarnings("rawtypes")
	HashMap<Pair<Class,Class>, IAdapterFactory>factories=new HashMap<Pair<Class,Class>, IAdapterFactory>();

	@SuppressWarnings("rawtypes")
	public IAdapterFactory get(Class<?> cl, Class<?> to) {
		Pair<Class,Class> p=new Pair<Class,Class>(cl,to);
		IAdapterFactory iAdapterFactory = factories.get(p);
		if (iAdapterFactory!=null){
			return iAdapterFactory;
		}		
		return internalGet(p);
	}

	@SuppressWarnings("rawtypes")
	private IAdapterFactory internalGet(Pair<Class, Class> p) {
		String id=p.first.getName()+'.'+p.second.getName();
		AdapterDefinition adapterDefinition = get(id);
		if (adapterDefinition!=null){
			IAdapterFactory service = adapterDefinition.getService();
			factories.put(p, service);
			return service;
		}
		Class superclass = p.first.getSuperclass();
		if (superclass!=null){
			IAdapterFactory internalGet = internalGet(new Pair<Class, Class>(superclass, p.second));
			if (internalGet!=null){
				factories.put(p, internalGet);
				return internalGet;
			}
		}
		Class[] interfaces = p.first.getInterfaces();
		for (Class<?>m:interfaces){
			IAdapterFactory internalGet = internalGet(new Pair<Class, Class>(m, p.second));
			if (internalGet!=null){
				factories.put(p, internalGet);
				return internalGet;
			}
		}
		return null;
	}
}
