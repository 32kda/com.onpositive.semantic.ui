package com.onpositive.semantic.editactions;


import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.actions.IAction;

public class BooleanPropAction extends PropertyAction {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 9033838486928678172L;

	public BooleanPropAction(IBinding c, IProperty property, Object[] baseObject) {
		super(c,property, baseObject, IAction.AS_CHECK_BOX);
		setChecked(Boolean.TRUE.equals(getCommonValue()));
		if (getText().isEmpty())
			setText(property.getId());
	}
	
	@Override
	public void run() {
		boolean oldValue = Boolean.TRUE.equals(getCommonValue());
		setCommonValue(!oldValue);
	}

}
