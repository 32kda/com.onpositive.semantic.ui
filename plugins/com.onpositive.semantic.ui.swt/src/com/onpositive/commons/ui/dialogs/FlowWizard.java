package com.onpositive.commons.ui.dialogs;

import org.eclipse.jface.wizard.IWizardPage;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;

public class FlowWizard extends BindedWizard{

	
	private INextPageProvider nextPageProvider;

	public INextPageProvider getNextPageProvider() {
		return nextPageProvider;
	}
	public void setNextPageProvider(INextPageProvider nextPageProvider) {
		this.nextPageProvider = nextPageProvider;
		for (IWizardPage p:getPages()){
			if (p instanceof FlowWizardPage){
				FlowWizardPage fp=(FlowWizardPage) p;
				fp.provider=nextPageProvider;
			}
		}
	}
	public FlowWizard(Binding bnd, String title,INextPageProvider provider) {
		super(bnd, title);
		this.nextPageProvider=provider;
	}
	public FlowWizard(Binding bnd, String title) {
		super(bnd, title);
	}
	
	
	public void addPage(CompositeEditor editor) {
		throw new UnsupportedOperationException();
	}
	
	
	public IWizardPage addPage(String name,AbstractUIElement editor) {
		FlowWizardPage page = new FlowWizardPage(name, getBinding(), editor, nextPageProvider);
		addPage(page);
		page.provider=this.nextPageProvider;
		return page;
	}


	
	public boolean needsPreviousAndNextButtons() {
		return true;
	}
}	
