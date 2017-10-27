package com.onpositive.semantic.editactions;

import java.util.Collection;
import java.util.List;

import com.onpositive.semantic.model.ui.actions.IRemoveAction;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectorElement;

public abstract class DefaultRemoveAction extends OwnedAction implements IRemoveAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DefaultRemoveAction(int style, IListElement<?> vl) {
		super(ISelectorElement.REMOVE_ACTION,style,vl);
		this.setActionDefinitionId("org.eclipse.ui.edit.delete");
		this.setId("delete");
	}
	public boolean isActuallyEnabled() {
		if (this.owner != null) {
			return !owner.getViewerSelection().isEmpty();
		} else {
			return false;
		}
	}
	
	public void internalRun() {
		Object expandedTreePaths = null;
		expandedTreePaths = preDelete(expandedTreePaths);
		try {
			if (this.title != null && this.title.length() > 0) {
				final boolean openConfirm = doConfirm();
				if (!openConfirm) {
					return;
				}
			}
			final List<?> list = (owner.getViewerSelection()).toList();
			doDelete(list); 
		} finally {
			postDelete(expandedTreePaths);
		}
	}
	protected abstract boolean doConfirm();
	
	@SuppressWarnings("unchecked")
	protected void doDelete(List<?> list) {
		owner.removeValues((Collection<Object>) list);
	}
	protected Object preDelete(Object expandedTreePaths) {
		return null;
	}
	protected void postDelete(Object expandedTreePaths) {
		return;
	}

	protected boolean doConfirm;
	protected String title = "";
	protected String description;

	public boolean isDoConfirm() {
		return this.doConfirm;
	}

	public void setDoConfirm(boolean doConfirm) {
		this.doConfirm = doConfirm;
	}

	public String getConfirmTitle() {
		return this.title;
	}

	public void setConfirmTitle(String title) {
		this.title = title;
	}

	public String getConfirmDescription() {
		if (this.title.length() > 0) {
			if (this.description.length() == 0) {
				return this.title;
			}
		}
		return this.description;
	}

	public void setConfirmDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (doConfirm ? 1231 : 1237);
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		DefaultRemoveAction other = (DefaultRemoveAction) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (doConfirm != other.doConfirm)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}
