package com.onpositive.semantic.editactions;

import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectorElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.StructuredSelection;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class AddFromRealmAction extends OwnedAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7017234945253585942L;

	@SuppressWarnings("rawtypes")
	public AddFromRealmAction(
			int asPushButton,
			IListElement abstractEnumeratedValueSelector) {
		super(ISelectorElement.ADD_ACTION, asPushButton,(IListElement) abstractEnumeratedValueSelector);
	}

	private String themeId;

	public void internalRun() {
		if (this.owner.isValueAsSelection()) {
			throw new IllegalStateException();
		} else {
			WidgetRegistry.getInstance().addFromRealm(
					(ISelectorElement<?>) this.owner,
					this.themeId,
					new CommmitListener((StructuredSelection) owner
							.getViewerSelection()),
					owner.getUndoContext());
		}
	}

	public String getThemeId() {
		return this.themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

	public boolean isActuallyEnabled() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((themeId == null) ? 0 : themeId.hashCode());
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
		AddFromRealmAction other = (AddFromRealmAction) obj;
		if (themeId == null) {
			if (other.themeId != null)
				return false;
		} else if (!themeId.equals(other.themeId))
			return false;
		return true;
	}

}