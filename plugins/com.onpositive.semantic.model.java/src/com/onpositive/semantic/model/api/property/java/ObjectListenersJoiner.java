package com.onpositive.semantic.model.api.property.java;

import com.onpositive.semantic.model.api.changes.DefaultListenersProvider;
import com.onpositive.semantic.model.api.changes.IObjectListenersProvider;
import com.onpositive.semantic.model.api.meta.IServiceJoiner;

public class ObjectListenersJoiner implements
		IServiceJoiner<IObjectListenersProvider> {

	@Override
	public IObjectListenersProvider joinService(IObjectListenersProvider o1,
			IObjectListenersProvider o2) {
		if (o1 instanceof DefaultListenersProvider) {
			DefaultListenersProvider c = (DefaultListenersProvider) o1;
			c.add(o2);
			return c;
		}
		// Do not add anything to probaly parent meta;
		// if (o2 instanceof CompositeValidator) {
		// CompositeValidator c = (CompositeValidator) o2;
		// c.addValidator(o1);
		// return c;
		// }
		DefaultListenersProvider cc=new DefaultListenersProvider();
		cc.add(o1);
		cc.add(o2);
		
		return cc;
	}

}
