package com.onpositive.semantic.ui.xml;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Display;

import com.onpositive.commons.xml.language.CustomAttributeHandler;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.property.IObjectRealm;
import com.onpositive.semantic.model.realm.IIdentifiableObject;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionBinding;
import com.onpositive.semantic.model.ui.property.editors.structured.AbstractEnumeratedValueSelector;

public class RemoveElementsActionBinding extends ActionBinding implements
		ISelectionChangedListener {

	private String confirmTitle;
	private String confirmDescription;
	private boolean doConfirm;

	protected AbstractEnumeratedValueSelector<Object> selector;
	private ElementListenerAdapter disposeBindingListener;

	@SuppressWarnings("unchecked")
	public void doAction() {
		if (this.doConfirm) {
			final boolean openConfirm = MessageDialog.openConfirm(Display
					.getCurrent().getActiveShell(), this.confirmTitle,
					this.confirmDescription);
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

	@HandlesAttributeDirectly("targetId")
	public void setSelector(final String object) {
		final IUIElement<?> adapter = getRoot().getAdapter(IUIElement.class);
		if (adapter != null) {
			adapter.addElementListener(new ElementListenerAdapter() {
				@SuppressWarnings("unchecked")
				@Override
				public void hierarchyChanged(IUIElement<?> element) {
					IUIElement<?> element2 = ((ICompositeElement<?, ?>) adapter)
							.getElement(object);
					if (element2 instanceof AbstractEnumeratedValueSelector<?>) {
						RemoveElementsActionBinding.this
								.setSelector((AbstractEnumeratedValueSelector<Object>) element2);
					}
				}
			});
		}
	}

	public void setSelector(final AbstractEnumeratedValueSelector<Object> object) {
		if (object != this.selector&&object!=null) {
			if (this.selector!=null){
				this.selector.removeElementListener(disposeBindingListener);
				dispose();
			}
	
			this.selector = object;

			disposeBindingListener = new ElementListenerAdapter() {

				public void elementCreated(IUIElement<?> element) {
					RemoveElementsActionBinding.this.proceedInstall(object);
				}

				public void elementDisposed(IUIElement<?> element) {
					object.getViewer().removeSelectionChangedListener(
							RemoveElementsActionBinding.this);
					RemoveElementsActionBinding.this.setReadOnly(true);
				}

			};
			object.addElementListener(disposeBindingListener);
			if (object.isCreated()) {
				this.proceedInstall(object);
			}
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

	@HandlesAttributeDirectly("confirmTitle")
	public void setConfirmTitle(String title) {
		this.confirmTitle = title;
		if (title != null && title.length() > 0) {
			doConfirm = true;
			if (confirmDescription.length() == 0)
				this.confirmDescription = title;
		}
	}

	@HandlesAttributeDirectly("confirmDescription")
	public void setConfirmDescription(String description) {
		this.confirmDescription = description;
		if (description != null && description.length() == 0
				&& this.confirmTitle != null)
			this.confirmDescription = this.confirmTitle;
	}
	//	final String attribute = element.getAttribute("targetId"); //$NON-NLS-1$
}
