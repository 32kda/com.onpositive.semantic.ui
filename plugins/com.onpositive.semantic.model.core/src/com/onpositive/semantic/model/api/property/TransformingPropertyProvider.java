package com.onpositive.semantic.model.api.property;

import java.util.ArrayList;

public class TransformingPropertyProvider implements IPropertyProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransformingPropertyProvider(IPropertyConstructor cs,
			IPropertyProvider provider, IPropertyProvider extender) {
		super();
		this.cs = cs;
		this.provider = provider;
		this.extender=extender;
	}

	protected final IPropertyConstructor cs;
	protected final IPropertyProvider provider;
	protected IPropertyProvider extender;

	@Override
	public IProperty getProperty(Object obj, String name) {
		IProperty property = provider.getProperty(obj, name);
		if (extender != null && property == null) {
			property = extender.getProperty(obj, name);
		}
		if (property != null) {
			IPropertyConstructor constructor = getConstructor();
			if (constructor==null){
				return property;
			}
			return constructor.create(property, obj);
		}

		return null;
	}

	protected IPropertyConstructor getConstructor() {
		return cs;
	}

	@Override
	public Iterable<IProperty> getProperties(Object obj) {
		ArrayList<IProperty> ps = new ArrayList<IProperty>();
		IPropertyConstructor constructor = getConstructor();
		combineProps(obj, ps, constructor, provider);
		combineProps(obj, ps, constructor, extender);
		return ps;
	}

	private void combineProps(Object obj, ArrayList<IProperty> ps,
			IPropertyConstructor constructor, IPropertyProvider provider2) {
		if (provider2 != null) {
			Iterable<IProperty> properties = provider2.getProperties(obj);
			for (IProperty p : properties) {
				IProperty create =constructor==null?p: constructor.create(p, obj);
				if (create != null) {
					ps.add(create);
				}
			}
		}
	}

	

}
