package com.onpositive.semantic.editactions;


import com.onpositive.semantic.model.api.wc.WorkingCopyAccess;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectorElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.StructuredSelection;
import com.onpositive.semantic.model.ui.roles.WidgetObject;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class OpenAction extends OwnedAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -226554713710914769L;
	boolean isDirectEdit;
	String widgetId;
	private String theme;
	private String role;

	public String getWidgetId() {
		return widgetId;
	}
	
	@Override
	protected boolean shouldBeEnabled(IStructuredSelection selection) {
		
		boolean shouldBeEnabled = super.shouldBeEnabled(selection);
		return shouldBeEnabled;
	}

	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	public boolean isDirectEdit() {
		return isDirectEdit;
	}

	public String getTheme() {
		return theme;
	}

	public void setDirectEdit(boolean isDiectEdit) {
		this.isDirectEdit = isDiectEdit;
	}

	public OpenAction(int style, final IListElement<?> sl) {
		super(ISelectorElement.EDIT_ACTION, style, sl);
		this.setActionDefinitionId("com.onpositive.semantic.ui.workbench.openCommand");

	}

	public boolean isActuallyEnabled() {
		if (this.owner != null) {
			
				final IStructuredSelection sel = (IStructuredSelection) owner
						.getViewerSelection();
				if (sel.size()==1){
				Object firstElement = sel.getFirstElement();
				
				WidgetObject widgetObject = WidgetRegistry.getInstance().getWidgetObject(firstElement, role == null ? owner.getRole() : role,
						theme == null ? owner.getTheme() : theme);
				if (widgetObject==null){
					return false;
				}
				}
				return sel.size() == 1;
			
			
		} else {
			return false;
		}
	}

	public void internalRun() {

		final IStructuredSelection sel = (IStructuredSelection) owner
				.getViewerSelection();
		
		boolean empty = sel.isEmpty();
		if (!empty) {
			if (isDirectEdit) {
				owner.editElement(sel.getFirstElement(), 0);
			} else {
				Object firstElement = sel.getFirstElement();
				if (firstElement instanceof ITreeNode<?>) {
					firstElement = ((ITreeNode<?>) firstElement)
							.getElement();
				}
				boolean rem = owner.getBinding().isWorkingCopiesEnabled();
				CommmitListener doOnCommit = new CommmitListener(sel);
				if (owner.getBinding() != null) {
					IBinding binding = owner.getBinding();
					if (binding.isWorkingCopiesEnabled()){
					firstElement = WorkingCopyAccess.getWorkingCopy(firstElement);
					doOnCommit.wc=firstElement;
					}
				}					
				WidgetRegistry.getInstance().showEditObjectWidget(
						firstElement,
						role == null ? owner.getRole() : role,
						theme == null ? owner.getTheme() : theme, widgetId,
						doOnCommit,
						owner.getUndoContext(), rem);
			}
		}
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isDirectEdit ? 1231 : 1237);
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((theme == null) ? 0 : theme.hashCode());
		result = prime * result
				+ ((widgetId == null) ? 0 : widgetId.hashCode());
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
		OpenAction other = (OpenAction) obj;
		if (isDirectEdit != other.isDirectEdit)
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (theme == null) {
			if (other.theme != null)
				return false;
		} else if (!theme.equals(other.theme))
			return false;
		if (widgetId == null) {
			if (other.widgetId != null)
				return false;
		} else if (!widgetId.equals(other.widgetId))
			return false;
		return true;
	}

}