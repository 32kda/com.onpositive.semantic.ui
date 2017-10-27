package com.onpositive.semantic.model.ui.generic.widgets;

import java.util.Collection;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;

public interface ISelectorElement<T>extends IUIElement<T>,IBindable {

	
	public String getElementRole() ;
	
	@HandlesAttributeDirectly("elementRole")
	public void setElementRole(String role);
	
	public IRealm<Object> getRealm();
	public IStructuredSelection getViewerSelection();

	public void addValue(Object value);

	public void addValues(Collection<Object> value);

	public Collection<Object> getCurrentValue();
	
	@HandlesAttributeDirectly("persist-selection")
	public void setPersistValueInSettings(boolean parseBoolean) ;
	
	public IListenableExpression<?>getSelectionBinding();
	
	public final static String EDIT_ACTION = "edit";

	public final static String ADD_ACTION = "add";

	public final static String REMOVE_ACTION = "remove";
	

}
