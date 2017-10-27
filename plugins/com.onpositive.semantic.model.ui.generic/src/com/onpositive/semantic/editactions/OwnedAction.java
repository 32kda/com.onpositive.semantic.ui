package com.onpositive.semantic.editactions;

import java.util.HashMap;
import java.util.HashSet;


import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.realm.IDisposable;
import com.onpositive.semantic.model.api.wc.WorkingCopyAccess;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.BindingStack;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.ICommitListener;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.generic.widgets.IActionInterceptor;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectionListener;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectorElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.StructuredSelection;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;

@SuppressWarnings("serial")
public abstract class OwnedAction extends Action
		implements
		com.onpositive.semantic.model.ui.generic.widgets.impl.IEnablementListener {

	// private static final long serialVersionUID = 5109903689056601566L;
	static HashMap<String, String> interceptorToImage = new HashMap<String, String>();
	static HashMap<String, String> interceptorToDisabledImage = new HashMap<String, String>();

	static {
		interceptorToImage
				.put(ISelectorElement.ADD_ACTION, "com.onpositive.semantic.ui.add");
		interceptorToImage.put(ISelectorElement.EDIT_ACTION,
				"com.onpositive.semantic.ui.edit");
		interceptorToImage.put(ISelectorElement.REMOVE_ACTION,
				"com.onpositive.semantic.ui.delete");
		interceptorToDisabledImage.put(ISelectorElement.REMOVE_ACTION,
				"com.onpositive.semantic.ui.deleted");
	}

	protected final IListElement<?> owner;
	protected final String interceptorKind;
	private ISelectionListener selectionChangedListener;

	public final class CommmitListener implements ICommitListener, IDisposable {
		private final IStructuredSelection sel;
		public Object wc;

		public CommmitListener(IStructuredSelection sel) {
			this.sel = sel;
		}

		public void commitPerformed(ICommand command) {
			
			HashSet<IActionInterceptor> hashSet = owner.getInterceptors(interceptorKind);
			if (hashSet != null) {
				for (IActionInterceptor i : hashSet) {
					i.postAction((IListElement<?>) owner,
							convertSelection(sel));
				}
			}
			if (wc!=null && owner.getBinding().isWorkingCopiesEnabled()) {
				Object firstElement = sel.getFirstElement();
				firstElement=LabelAccess.getPresentationObject(firstElement);
				IBinding caller = BindingStack.getCaller();
				Object undoCtx = caller.getMeta().getSingleValue(DefaultMetaKeys.ROOT_UNDO_CONTEXT,Object.class,null);
				if (undoCtx != null) {
					WorkingCopyAccess.applyWorkingCopyWithUndo(wc,firstElement, undoCtx);
				} else {
					WorkingCopyAccess.applyWorkingCopy(wc,firstElement);
				}
				//WorkingCopyAccess.disposeWorkingCopy(wc);
			}
			((AbstractBinding)owner.getBinding()).onChildChanged();
		}

		public void dispose() {
			if (wc!=null) {
				WorkingCopyAccess.disposeWorkingCopy(wc);
			}
		}
	}
	ElementListenerAdapter disposeBindingListener = new ElementListenerAdapter() {

		public void elementCreated(IUIElement<?> element) {
			owner.addSelectionListener(
					selectionChangedListener);
			setEnabled(shouldBeEnabled((IStructuredSelection) owner
					.getViewerSelection()));
		}

		public void elementDisposed(IUIElement<?> element) {
			owner.removeSelectionListener(
					selectionChangedListener);
		}

	};

	public OwnedAction(String interceptorKind, int style,
			IListElement<?> sl) {
		super(style);
		this.interceptorKind = interceptorKind;
		this.owner = sl;
		selectionChangedListener = new ISelectionListener() {

			

			public void selectionChanged(IStructuredSelection selection) {
				setEnabled(shouldBeEnabled((IStructuredSelection) selection));
			}

		};

		sl.addElementListener(disposeBindingListener);

		this.owner.addEnablementListener(this);
		//if (owner.isCreated()) {
			IStructuredSelection viewerSelection = owner.getViewerSelection();
			setEnabled(shouldBeEnabled((IStructuredSelection) viewerSelection));
			owner.addSelectionListener(
					selectionChangedListener);
		//}
	}

	public ImageDescriptor getDisabledImageDescriptor() {

		ImageDescriptor disabledImageDescriptor = super
				.getDisabledImageDescriptor();
		if (disabledImageDescriptor == null
				&& super.getImageDescriptor() == null) {
			String id = interceptorToDisabledImage.get(interceptorKind);
			if (id != null) {
				return ImageManager.getImageDescriptorByPath(this,id);
			}
		}
		return disabledImageDescriptor;
	}

	public com.onpositive.semantic.model.ui.generic.IStructuredSelection convertSelection(
			final IStructuredSelection sel) {
		return sel;
	}

	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor imageDescriptor = super.getImageDescriptor();
		if (imageDescriptor == null) {
			String id = interceptorToImage.get(interceptorKind);
			if (id != null) {
				return ImageManager.getImageDescriptorByPath(this,id);
			}
		}
		return imageDescriptor;
	}
	
	@Override
	public String getImageId() {
		if (super.getImageId() == null || super.getImageId().length() == 0) {
			return interceptorToImage.get(interceptorKind);
		}
		return super.getImageId();
	}

	public void dipose() {
		this.owner.removeEnablementListener(this);
		if (owner!= null) {
			owner.
					removeSelectionListener(selectionChangedListener);
		}
		owner.removeElementListener(disposeBindingListener);
	}

	public final IListElement<?> getOwner() {
		return this.owner;
	}

	public void run() {
		HashSet<IActionInterceptor> hashSet = owner.getInterceptors(interceptorKind);
		if (hashSet != null) {
			StructuredSelection selection = (StructuredSelection) owner
					.getViewerSelection();
			for (IActionInterceptor i : hashSet) {
				if (!i.preAction((IListElement<?>) owner,
						convertSelection((StructuredSelection) owner
								.getViewerSelection()))) {
					return;
				}

			}
			internalRun();
			for (IActionInterceptor i : hashSet) {
				if (!i.postAction((IListElement<?>) owner,
						convertSelection(selection))) {
					return;
				}

			}
			return;
		} else {
			internalRun();
		}
	}

	public abstract void internalRun();

	public void enablementChanged(IUIElement<?> element, boolean enabled) {
		this.setEnabled(enabled && this.isActuallyEnabled());
	}

	protected boolean shouldBeEnabled(IStructuredSelection selection) {
		HashSet<IActionInterceptor> hashSet = owner.getInterceptors(interceptorKind);
		if (hashSet != null) {
			for (IActionInterceptor i : hashSet) {
				if (!i.isEnabled((IListElement<?>) owner,
						convertSelection(selection))) {
					return false;
				}
			}
		}
		return owner.isEnabled() && isActuallyEnabled();
	}

	public abstract boolean isActuallyEnabled();

}