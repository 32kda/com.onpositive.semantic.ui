package com.onpositive.semantic.model.ui.property.editors.structured;



import com.onpositive.semantic.model.realm.IFactory;
import com.onpositive.semantic.model.ui.actions.IAction;

public interface IHasEditingActions {

	public IAction createAddNewAction();

	public IAction createAddFromRealmAction();

	public IAction createRemoveElementsAction();

	public IAction createOpenAction();

	public void setAddNewActionFactory(IFactory factory);

	public void setAddFromRealmActionFactory(IFactory factory);

	public void setRemoveElementsActionFactory(IFactory factory);

	public void setOpenElementsActionFactory(IFactory factory);
}
