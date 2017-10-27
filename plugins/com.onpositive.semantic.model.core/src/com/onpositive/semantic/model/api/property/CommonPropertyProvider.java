package com.onpositive.semantic.model.api.property;

import java.util.HashMap;

import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.id.IdProperty;
import com.onpositive.semantic.model.api.labels.LabelProperty;
import com.onpositive.semantic.model.api.realm.RealmProperty;
import com.onpositive.semantic.model.api.status.StatusProperty;

public class CommonPropertyProvider implements IPropertyProvider{

	private static final long serialVersionUID = 1L;
	protected HashMap<String, IProperty>commons=new HashMap<String, IProperty>();

	public void addProperty(IProperty property){
		IProperty put = commons.put(property.getId(), property);
		if (put!=null){
			PropertyAccess.firePropertyStructureListener(put);
		}
		ObjectChangeManager.markChanged(this);
	}
	public void removeProperty(IProperty property){
		IProperty put = commons.remove(property.getId());
		if (put!=null){
			PropertyAccess.firePropertyStructureListener(put);
		}
		ObjectChangeManager.markChanged(this);
	}
	
	protected CommonPropertyProvider() {
		addProperty(IdProperty.INSTANCE);
		addProperty(PropertiesProperty.INSTANCE);
		addProperty(StatusProperty.INSTANCE);
		addProperty(LabelProperty.INSTANCE);
		addProperty(RealmProperty.INSTANCE);
		//TODO REDUCE CLASSLOADING
	}
	
	public static final CommonPropertyProvider INSTANCE=new CommonPropertyProvider();
	
	
	@Override
	public IProperty getProperty(Object obj, String name) {
		return commons.get(name);
	}

	
	@Override
	public Iterable<IProperty> getProperties(Object obj) {
		return commons.values();
	}
	

}
