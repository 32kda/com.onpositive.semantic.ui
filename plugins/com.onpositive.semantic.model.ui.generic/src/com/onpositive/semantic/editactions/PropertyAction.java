package com.onpositive.semantic.editactions;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;

public abstract class PropertyAction extends ObjectAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5328838280658002532L;
	protected final IProperty property;
	protected IBinding binding;
	public PropertyAction(IBinding c, IProperty property, Object[] baseObjects, int style) {
		super(baseObjects, style);
		this.property = property;
		this.binding=c;
		setActionLook(property);
	}

	protected void setActionLook(IProperty property) {
		String name = property.getMeta().getSingleValue(DefaultMetaKeys.CAPTION_KEY,String.class,null);
		if (name != null)
			setText(name);
		String imageKey = DefaultMetaKeys.getImageKey(property);
		if (!imageKey.isEmpty()) {
			ImageDescriptor descriptor = ImageManager.getImageDescriptorByPath(this.baseObjects[0], imageKey);
			if (descriptor != null)
				setImageDescriptor(descriptor);
		}
	}

	protected void editProperty() {

	}
	
	protected Object getCommonValue() {
		if (baseObjects == null || baseObjects.length == 0)
			return null;
		Object firstValue = property.getValue(baseObjects[0]);
		if (firstValue == null)
			return null;
		for (int i = 1; i < baseObjects.length; i++) {
			if (!firstValue.equals(property.getValue(baseObjects[i])))
				return null;
		}
		return firstValue;
	}
	
	protected void setCommonValue(Object value) {
		PropertyAccess.setValueToAll(property,baseObjects,value);
		if (binding!=null){
			((AbstractBinding)binding).onChildChanged();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyAction other = (PropertyAction) obj;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}

	

}
