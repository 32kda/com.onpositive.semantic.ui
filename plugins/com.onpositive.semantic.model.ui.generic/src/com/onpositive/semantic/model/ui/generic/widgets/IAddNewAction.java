package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.semantic.model.ui.actions.IAction;

public interface IAddNewAction extends IAction{

	public void setWidgetId(String themeId);

	public String getTypeId();

	public void setTypeId(String typeId);

	public String getWidgetId();
	
	public Class<?> getObjectClass();

	public void setObjectClass(Class<?> objectClass);

	public void setCreateChild(boolean b);
}
