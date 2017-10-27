package com.onpositive.semantic.ui.android;

import com.onpositive.semantic.model.api.changes.ISetDelta;

public abstract class AndroidValueEditor extends SimpleAndroidEditor {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -5472453767233170241L;

	@Override
	public void processValueChange(ISetDelta<?> valueElements) {
		if (!getBinding().allowsMultiValues()) {
			if (!valueElements.getAddedElements().isEmpty()) {
				this.setSelection(valueElements.getAddedElements()
						.iterator().next());
			} else {
				if (!valueElements.getChangedElements().isEmpty()) {
					this.setSelection(valueElements
							.getChangedElements().iterator().next());
				} else {
					if (!valueElements.getRemovedElements().isEmpty()) {
						this.setSelection(getDefaultSelection());
					}
				}
			}
		} else {
			this.setSelection(getBinding().getValue());
		}
	}

	protected abstract Object getDefaultSelection();

	protected abstract void setSelection(Object value);

}
