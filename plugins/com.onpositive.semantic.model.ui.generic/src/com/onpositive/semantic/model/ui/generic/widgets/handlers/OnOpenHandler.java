package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import com.onpositive.commons.xml.language.AttributeHandler;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.EditorBindingController;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;

public final class OnOpenHandler extends
		AttributeHandler<IListElement> {
	public OnOpenHandler() {
		super(IListElement.class);
	}

	
	public
	void handle(final IListElement listEnumeratedValueSelector, Object pContext,
			String value, Context ctx) {
		String onOpen = value; //$NON-NLS-1$
		if (onOpen!=null&&onOpen.length()>0){
			listEnumeratedValueSelector.addElementListener(new EditorBindingController(listEnumeratedValueSelector, onOpen){
				IBinding bnd;
				
				IOpenListener iOpenListener = new IOpenListener() {
					
					public void open(IStructuredSelection event) {
						IStructuredSelection selection = (IStructuredSelection) event;
						Object firstElement = selection.getFirstElement();
						bnd.actionPerformed(firstElement, listEnumeratedValueSelector);
					}
				};
				
				protected void install(final IBinding binding) {
					//super.install(binding);
					listEnumeratedValueSelector.removeOpenListener(iOpenListener);
					bnd=binding;
					listEnumeratedValueSelector.addOpenListener(iOpenListener);
				}
			});
		}
	}
}