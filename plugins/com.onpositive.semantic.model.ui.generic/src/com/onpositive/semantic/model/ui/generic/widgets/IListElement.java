package com.onpositive.semantic.model.ui.generic.widgets;


import java.util.Collection;
import java.util.HashSet;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.decoration.IObjectDecorator;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.order.IOrderMaintainer;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.tree.IClusterizationPointProvider;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.IMayHaveDecorators;
import com.onpositive.semantic.model.ui.generic.IRefreshable;
import com.onpositive.semantic.model.ui.generic.IRowStyleProvider;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.IOpenListener;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;


public interface IListElement<T>extends ISelectorElement<T>,IMayHaveDecorators<T>,IRefreshable {

	public boolean isAllowCellEditing();

	@HandlesAttributeDirectly("enableDirectEdit")
	public void setAllowCellEditing(boolean allowCellEditing) ;
	
	@HandlesAttributeDirectly("asCheckBox")
	public void setAsCheckBox(boolean isAsCheckBox);
	
	public boolean isAsCheckBox();
	
	public boolean isNoScrollBar();
	
	@HandlesAttributeDirectly("fitHorizontal")
	public void setNoScrollBar(boolean isNoScrollBar);
	
	@HandlesAttributeDirectly("asTree")
	public void setAsTree(boolean selection);
	
	
	public boolean isAsTree();
	
	@HandlesAttributeDirectly("openOnDoubleClick")
	public void setOpenOnDoubleClick(boolean openOnDoubleClick);
	
	public void setSelectionBinding(IBinding binding);

	public IBinding getSelectionBinding();
	
	public IRowStyleProvider getRowStyleProvider();

	@HandlesAttributeDirectly("rowStyleProvider")
	public void setRowStyleProvider(IRowStyleProvider rowStyleProvider);

	
	public void addInterceptor(String kind, IActionInterceptor newInstance);
	
	
	public int getColumnCount();

	public boolean isValueAsSelection();

	public Collection<Object> getCurrentValue();

	public void removeValues(Collection<Object> lsa);

	public void addValues(Collection<Object> lsa);

	public void setSelection(IStructuredSelection structuredSelection);

	

	public void setClusterizationPointProviders(String lock,
			@SuppressWarnings("rawtypes") IClusterizationPointProvider[] ns);

	public void setClusterizationPointProviders(@SuppressWarnings("rawtypes") IClusterizationPointProvider[] object);


	public IOrderMaintainer getOrderMaintainer();

	public void addSelectionListener(ISelectionListener selectionHandler);
	public void removeSelectionListener(ISelectionListener selectionHandler);

	public void removeOpenListener(IOpenListener iOpenListener);

	public void addOpenListener(IOpenListener iOpenListener);

	public IContributionItem createRemoveSelectedContributionItem();

	public IContributionItem createOpenContributionItem();
	
	public IContributionItem createAddNewContributionItem();
	
	public IContributionItem createCopyContributionItem();
	
	public IContributionItem createAddFromContributionItem();
	
	public IContributionItem createRefreshContributionItem();

	public void addValue(Object value);

	public IContributionItem createSettingsContributionItem();

	public void move(Collection<Object> firstElement, boolean direction);
	
	public void addDropSupportParticipant(ICanDrop dropper);
	public void removeDropSupportParticipant(ICanDrop dropper);

	public Object getParentObject();

	public Collection<IObjectDecorator<?>> getDecorators();

	public IHasMeta getMeta();

	
	void move(boolean direction);

	boolean canMove(boolean direction);

	public ImageDescriptor getDefaultImageDescriptor();

	public HashSet<IActionInterceptor> getInterceptors(String interceptorKind);

	

	public Object getUndoContext();

	public void editValue(Object object, int pos);

	public void editElement(Object firstElement, int i);

	public String getProperty();
	
	
		
}