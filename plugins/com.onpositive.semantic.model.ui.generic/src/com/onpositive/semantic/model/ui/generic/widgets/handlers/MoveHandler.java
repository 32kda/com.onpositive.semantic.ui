package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import java.util.Collection;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.api.order.IOrderListener;
import com.onpositive.semantic.model.api.order.IOrderMaintainer;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectionListener;
import com.onpositive.semantic.model.ui.generic.widgets.ITreeElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class MoveHandler extends AbstractActionElementHandler {

	private final static class SelectionHandler extends ElementListenerAdapter implements IOrderListener, ISelectionListener {
		private final IListElement<?> control;
		private final boolean direction;
		private final ActionBinding binding;
		
		private SelectionHandler(
				IListElement<?> control,
				boolean direction, ActionBinding binding) {
			this.control = control;
			this.direction = direction;
			this.binding = binding;
		}

	

		private void process() {			
				IStructuredSelection selection = (IStructuredSelection) control.getViewerSelection();
				if (control instanceof ITreeElement){
					binding.setReadOnly(!((ITreeElement)control).canMove(direction));
					return;
				}
				IOrderMaintainer orderMaintainer = control.getOrderMaintainer();
				if (orderMaintainer==null){
					return;
				}
				
				
				if (selection.isEmpty()
						|| !orderMaintainer.canMove(
								selection.toList(), direction)) {
					binding.setReadOnly(true);
				} else {
					binding.setReadOnly(false);
				}			
		}
		
		public void elementDisposed(IUIElement element) {					
		}
		
		
		public void elementCreated(IUIElement element) {
			super.elementCreated(element);
		}

		public void orderChanged() {
			process();
		}

		public void selectionChanged(IStructuredSelection selection) {
			process();
		}
	}

	public MoveHandler() {
	}

	@SuppressWarnings("unchecked")
	
	protected Action contribute(ActionsSetting parentContext, Context context, Element element) {
		final boolean direction = element.getNodeName().equals("moveUp");
		final IListElement<?> control = (IListElement<Object>) parentContext.getControl();
		final IPropertyEditor<?> c = control.getService(IPropertyEditor.class);
		final Binding bnd = (Binding) c.getBinding();
		
		if( bnd == null )
			return null ;
		
		final ActionBinding binding = new ActionBinding() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void doAction() {
				final IListElement<Object> ls = (IListElement<Object>) control;
				IOrderMaintainer orderMaintainer = ls.getOrderMaintainer();
				if (ls instanceof ITreeElement){
					ITreeElement  m=(ITreeElement) ls;
					m.move(direction);
					return;
				}
				com.onpositive.semantic.model.ui.generic.IStructuredSelection selection = ls.getViewerSelection();
				if (selection.isEmpty()) {
					return;
				}
				control.move((Collection<Object>) selection.toList(),direction);				
			}
		};
		control.addSelectionListener(new SelectionHandler(control, direction, binding));
		final BindedAction bindedAction = new BindedAction(binding);
		
		binding.setReadOnly(true);
		bnd.setBinding(element.getAttribute("id"), binding);

		
		if (direction){
			binding.setName("Up");
			bindedAction.setImageId("com.onpositive.semantic.ui.swt.upe");
			bindedAction.setDisabledImageId("com.onpositive.semantic.ui.swt.upd");
		}
		else{
			binding.setName("Down");
			bindedAction.setImageId("com.onpositive.semantic.ui.swt.downe");
			bindedAction.setDisabledImageId("com.onpositive.semantic.ui.swt.downd");
		}
		//handleAction(element, context, bindedAction, parentContext.getControl() );
		//parentContext.addAction( bindedAction, element );
		return bindedAction ;
	}
	
	
//	protected void contribute(IProvidesToolbarManager parentContext,
//			Context context, Element element) {
//		throw new UnsupportedOperationException("Works only with actions tag");
//	}

}
