package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import java.io.Serializable;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingSetListener;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IContributionManager;
import com.onpositive.semantic.model.ui.generic.EditorBindingController;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.generic.IProvidesUI;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.BindedActionHandler.ManagerHolder;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;

public abstract class AbstractActionElementHandler implements IElementHandler,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class BasicAction extends BindedAction {

		/**
		 * Serial version UID
		 */
		private static final long serialVersionUID = -3132468877267895393L;

		public BasicAction(int style) {
			super(style);
		}

		public void run() {
		}
	}

	public Object handleElement(final Element element, Object parentContext,
			final Context context) {
		
		if (parentContext instanceof ManagerHolder) {
			final ManagerHolder mn = (ManagerHolder) parentContext;
			BasicUIElement<?> el = (BasicUIElement<?>) mn.gui;
			String key = "" + element.hashCode();// System.identityHashCode(
													// element ) ;
			Object data = null;// el.getData( key );
			if (data == null) {

				final HierarchyController hc = new HierarchyController(element,
						mn.gui, context, this) {
					@Override
					protected void doInternal(IUIElement<?> parentUI,
							IProvidesToolbarManager newToolbarManager, IContributionManager newPopupMenuManager) {
						if (action==null){
						IContributionItem newAction = contribute(new ActionsSetting(
								parentUI, true), context, this.element);
						action=newAction;
						}
						if (action!=null){
							mn.addAction(action, element);
						}
					}

				};
				el.setData(key, hc);
				el.addElementListener(hc);
				hc.doHandle();
			}
			return null;
		}
		if (parentContext instanceof ActionsSetting) {
			this.contribute((ActionsSetting) parentContext, context, element);
			return null;
		}
		else {// if (parentContext instanceof IProvidesToolbarManager){
			if (parentContext instanceof BasicUIElement) {
				this.contribute(parentContext, context, element);
			}
			return null;
		}
		// return null;
	}

	protected abstract IContributionItem contribute(
			ActionsSetting parentContext, Context context, Element element);

	protected void contribute(Object parentContext, Context context,
			Element element) {
		BasicUIElement<?> el = (BasicUIElement<?>) parentContext;
		String key = "" + element.hashCode();// System.identityHashCode( element
												// ) ;
		Object data = null;// el.getData( key );
		if (data == null) {

			HierarchyController hc = createHierarcyController(parentContext, context, element);
			el.setData(key, hc);
			el.addElementListener(hc);
			hc.doHandle();
		}
	}

	protected HierarchyController createHierarcyController(
			Object parentContext, Context context, Element element) {
		return new HierarchyController(element,
				parentContext, context, this);
	}

	protected void attachToParent(Element element, Object parentContext,
			IContributionItem action) {

	}

	public static void handleAction(Element element, Object parent,
			final Action action, IUIElement<?> parentsControl) {
		
		final String attrDefinitionId = element.getAttribute("definitionId");
		if (attrDefinitionId.length() > 0)
			action.setId(attrDefinitionId);

		final String attrExportAs = element.getAttribute("exportAs");
		if (attrExportAs.length() > 0) {
			if (parentsControl instanceof IPropertyEditor) {
				IPropertyEditor<?> parentsProperyEditor = (IPropertyEditor<?>) parentsControl;
				Binding binding = (Binding) parentsProperyEditor.getBinding();
				final Binding actionBinding = new Binding(action);
				actionBinding.setReadOnly(false);

				if (binding != null)
					binding.setBinding(attrExportAs, actionBinding);

				parentsProperyEditor
						.addBindingSetListener(new IBindingSetListener() {

							public void bindingChanged(IBindable element,
									IBinding newBinding, IBinding oldBinding) {
								((Binding) newBinding).setBinding(attrExportAs,
										actionBinding);
							}
						});
			}
		}
		final String attrCaption = element.getAttribute("caption");
		if (attrCaption.length() > 0)
			action.setText(attrCaption);
		String enablementExpression = element.getAttribute("enablement");
		String visibilityExpression = element.getAttribute("visibility");

		String attribute = element.getAttribute("image");
		if (attribute.length()>0){
			action.setImageId(attribute); //$NON-NLS-1$
		}
		action.setDisabledImageId(element.getAttribute("disabled-image")); //$NON-NLS-1$
		action.setHoverImageId(element.getAttribute("hover-image")); //$NON-NLS-1$
		if (enablementExpression.length() > 0) {
			action.setEnablementExpression(enablementExpression, parentsControl);
		}
		if (visibilityExpression.length() > 0) {
			action.setVisibilityExpression(visibilityExpression, parentsControl);
		}
		if (action instanceof BindedAction) {
			BindedAction bindedAction = (BindedAction) action;
			bindedAction.setThemeId(element.getAttribute("widgetId"));
			final IUIElement<?> parentUI = getUI(parent);

			String attrBindTo = element.getAttribute("bindTo"); //$NON-NLS-1$
			if ((attrBindTo != null) && (attrBindTo.length() > 0)) {

				final EditorBindingController editorBindingController = new EditorBindingController(
						(IBindable) action, attrBindTo);

				if (parentUI != null) {
					parentUI.addElementListener(editorBindingController);
					editorBindingController
							.hierarchyChanged((IUIElement<?>) parentUI);
				}
			}
		}
		// if (parent instanceof IProvidesToolbarManager) {
		// final IProvidesToolbarManager cm = (IProvidesToolbarManager) parent ;
		// cm.addToToolbar(action) ;
		// }
	}

	public static IUIElement<?> getUI(Object parent) {
		if (parent instanceof IProvidesUI) {
			final IProvidesUI ps = (IProvidesUI) parent;
			return (IUIElement<?>) ps.getUI();
		}
		if (parent instanceof IUIElement) {
			return (IUIElement<?>) parent;
		}
		return null;
	}

}
