package com.onpositive.commons.ui.dialogs;

import org.eclipse.jface.wizard.IWizardPage;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;

public class FlowWizardPage extends BindedWizardPage{

	protected INextPageProvider provider;
	private boolean isLast;

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	public FlowWizardPage(String name,Binding bnd, AbstractUIElement<?> element,INextPageProvider provider) {
		super(name,bnd, element);
		this.provider=provider;
	}	

	@SuppressWarnings("unchecked")
	public FlowWizardPage(IPropertyEditor<? extends AbstractUIElement<?>> editor) {
		super((IPropertyEditor<AbstractUIElement>) editor);
	}
	
	public boolean canFlipToNextPage(){
		return isPageComplete()&&hasNextPage();		
	}

	protected boolean hasNextPage() {
		return !isLast;
	}
	
	
	public void setNextPage(String next){
		
	}
	
	
	public IWizardPage getNextPage() {
		return provider.getNextPage(this);		
	}		

	
	public IWizardPage getPreviousPage() {
		return super.getPreviousPage();
	}
}
