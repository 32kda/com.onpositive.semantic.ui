package com.onpositive.semantic.ui.xml;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IInitializer;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.property.IObjectRealm;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.realm.IIdentifiableObject;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionBinding;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.model.ui.property.editors.structured.AbstractEnumeratedValueSelector;

public class DeleteElementsUIFactory extends UIElementHandler {

	private static final class RemoveElementsActionBinding extends
			ActionBinding implements ISelectionChangedListener {

		private String title;
		private String description;
		private boolean doConfirm;

		protected AbstractEnumeratedValueSelector<Object> selector;

		@SuppressWarnings("unchecked")
		public void doAction() {
			if (this.doConfirm) {
				final boolean openConfirm = MessageDialog.openConfirm(Display
						.getCurrent().getActiveShell(), this.title,
						this.description);
				if (!openConfirm) {
					return;
				}
			}
			final List list = ((StructuredSelection) this.selector.getViewer()
					.getSelection()).toList();
			if (!this.selector.isValueAsSelection()) {
				this.selector.removeValues(list);
			} else {
				final IRealm<IIdentifiableObject> realm2 = (IRealm) this.selector
						.getRealm();
				if (realm2 instanceof IObjectRealm) {
					final IObjectRealm<IIdentifiableObject> er = (IObjectRealm<IIdentifiableObject>) realm2;
					final CompositeCommand cm = new CompositeCommand();
					for (final Object o : list) {
						final ICommand objectDeletionCommand = er
								.getObjectDeletionCommand((IIdentifiableObject) o);
						cm.addCommand(objectDeletionCommand);
					}
					er.execute(cm);
				}
			}
		}

		public void setSelector(
				final AbstractEnumeratedValueSelector<Object> object) {
			this.selector = object;
			object.addElementListener(new ElementListenerAdapter<Control>() {

				public void elementCreated(IUIElement element) {
					RemoveElementsActionBinding.this.proceedInstall(object);
				}

				public void elementDisposed(IUIElement element) {
					object.getViewer().removeSelectionChangedListener(
							RemoveElementsActionBinding.this);
					RemoveElementsActionBinding.this.setReadOnly(true);
				}

			});
			if (object.isCreated()) {
				this.proceedInstall(object);
			}
		}

		public void dispose() {
			if ((this.selector != null) && this.selector.isCreated()) {
				this.selector.getViewer().removeSelectionChangedListener(this);
			}
		}

		public void selectionChanged(SelectionChangedEvent event) {
			this.setReadOnly(event.getSelection().isEmpty());
		}

		private void proceedInstall(
				final AbstractEnumeratedValueSelector<Object> object) {
			object.getViewer().addSelectionChangedListener(
					RemoveElementsActionBinding.this);
			final StructuredViewer viewer = this.selector.getViewer();
			this.setReadOnly(viewer.getSelection().isEmpty());
		}
	}

	public DeleteElementsUIFactory() {
	}

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		final RemoveElementsActionBinding handleElement = new RemoveElementsActionBinding();
		((Binding) parentContext).setBinding(
				element.getAttribute("id"), handleElement); //$NON-NLS-1$
		final String attribute = element.getAttribute("targetId"); //$NON-NLS-1$
		handleElement.title = element.getAttribute("confirmTitle"); //$NON-NLS-1$
		handleElement.description = element.getAttribute("confirmDescription"); //$NON-NLS-1$
		if (handleElement.title.length() > 0) {
			handleElement.doConfirm = true;
			if (handleElement.description.length() == 0) {
				handleElement.description = handleElement.title;
			}
		}
		if ((attribute != null) && (attribute.length() > 0)) {
			context.addInitializer(new IInitializer() {

				@SuppressWarnings("unchecked")
				public void init(Context context) {
					handleElement
							.setSelector((AbstractEnumeratedValueSelector<Object>) context
									.getObject(attribute));
				}

			});
		}
		super.configProxy(handleElement, element, context);
		return handleElement;
	}
}