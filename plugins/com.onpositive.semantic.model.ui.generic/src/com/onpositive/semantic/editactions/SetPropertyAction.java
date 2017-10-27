package com.onpositive.semantic.editactions;

import java.util.Collection;

import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.actions.IAction;

public class SetPropertyAction extends PropertyAction {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 8886144801456380062L;
	private final Object value;

	public SetPropertyAction(IBinding c, IProperty property, Object[] baseObjects, Object value, int style) {
		super(c,property, baseObjects, style);
		this.value = value;
		setText(LabelAccess.getLabel(value));
		Object currentValue = getCommonValue();
		if (style == IAction.AS_CHECK_BOX) 
			currentValue = getCommonCollection();
		if (currentValue == null)
			return;
		if (value.equals(currentValue))
			setChecked(true);
		else if (currentValue instanceof Collection) {
			setChecked(((Collection<?>) currentValue).contains(value));
		} else if (currentValue.getClass().isArray()) {
			setChecked(ArrayUtils.contains((Object[]) currentValue, value));
		}
	}
	
	@Override
	protected void setActionLook(IProperty property) {
		// Do nothing; Parent action is styled
	}
	
	private Object getCommonCollection() {
		if (baseObjects == null || baseObjects.length == 0)
			return null;
		Collection<?> collection = (Collection<?>) property.getValue(baseObjects[0]);
		for (int i = 1; i < baseObjects.length; i++) {
			collection.retainAll((Collection<?>) property.getValue(baseObjects[0]));
		}
		return collection;
	}

	@Override
	public void run() {
		if (getStyle() == AS_CHECK_BOX) {
			if (isChecked())
				addCommonValue(value);
			else
				removeCommonValue(value);
		} else 
			setCommonValue(value);
	}

	private void addCommonValue(Object value) {
		PropertyAccess.addValueToAll(property,baseObjects,value);
	}
	
	private void removeCommonValue(Object value) {
		PropertyAccess.removeValueFromAll(property,baseObjects,value);	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SetPropertyAction other = (SetPropertyAction) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}


}
