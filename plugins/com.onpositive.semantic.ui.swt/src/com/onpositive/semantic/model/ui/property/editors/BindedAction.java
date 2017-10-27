//package com.onpositive.semantic.model.ui.property.editors;
//
//import org.eclipse.jface.action.Action;
//
//import com.onpositive.commons.SWTImageManager;
//import com.onpositive.core.runtime.Platform;
//import com.onpositive.semantic.model.api.roles.ImageManager;
//import com.onpositive.semantic.model.api.roles.ImageObject;
//import com.onpositive.semantic.model.binding.Binding;
//import com.onpositive.semantic.model.binding.IBindable;
//import com.onpositive.semantic.model.binding.IBinding;
//import com.onpositive.semantic.model.binding.IBindingChangeListener;
//import com.onpositive.semantic.model.realm.IFactory;
//import com.onpositive.semantic.model.realm.ISetDelta;
//import com.onpositive.semantic.model.ui.generic.IBindingSetListener;
//
//public class BindedAction extends Action implements IBindable {
//
//	protected IBinding binding;
//	protected String imageId;
//	protected String disabledImageId;
//	protected String hoverImageId;
//	protected String oldTooltip;
//
//	private final IBindingChangeListener<Object> chListener = new IBindingChangeListener<Object>() {
//
//		public void changed() {
//			BindedAction.this.update();
//		}
//
//		public void enablementChanged(boolean isEnabled) {
//			BindedAction.this.setEnabledFromBinding(isEnabled);
//		}
//
//		public void valueChanged(ISetDelta<Object> valueElements) {
//			BindedAction.this.update();
//		}
//
//	};
//
//	private void setEnabledFromBinding(boolean isEnabled) {
//		if (!isEnabled) {
//			final String whyBindingIsDisabled = this.binding
//					.getWhyBindingIsDisabled();
//			if ((whyBindingIsDisabled != null)
//					&& (whyBindingIsDisabled.length() > 0)) {
//				if (this.isEnabled()) {
//					this.oldTooltip = this.getToolTipText();
//				}
//				super.setToolTipText(whyBindingIsDisabled);
//			}
//		} else {
//			if (this.oldTooltip != null) {
//				super.setToolTipText(this.oldTooltip);
//				this.oldTooltip = null;
//			}
//		}
//		this.setEnabled(isEnabled);
//	}
//
//	public void run() {
//		if (this.binding != null) {
//			if (Action.class.isAssignableFrom(this.binding.getSubjectClass())) {
//				this.binding.actionPerformed(null, this);
//			} else if (IFactory.class.isAssignableFrom(this.binding
//					.getSubjectClass())) {
//				this.binding.actionPerformed(null, this);
//			} else if (Runnable.class.isAssignableFrom(this.binding
//					.getSubjectClass())) {
//				this.binding.actionPerformed(null, this);
//			} else {
//
//				this.binding.setValue(this.isChecked(), this.chListener);
//			}
//		}
//	}
//
//	public BindedAction(int style) {
//		super("", style); //$NON-NLS-1$
//	}
//
//	public BindedAction(IBinding bnd, int style) {
//		super("", style); //$NON-NLS-1$
//		this.setBinding(bnd);
//	}
//
//	public BindedAction(Binding binding2) {
//		this.setBinding(binding2);
//	}
//
//	public void setBinding(IBinding bnd) {
//		if ((this.binding != null) && (this.binding != bnd)) {
//			this.uninstallBinding(this.binding);
//		}
//		this.binding = bnd;
//		bnd.addBindingChangeListener(this.chListener);
//		this.update();
//
//	}
//
//	public void setToolTipText(String toolTipText) {
//		if (this.oldTooltip != null) {
//			this.oldTooltip = toolTipText;
//		} else {
//			super.setToolTipText(toolTipText);
//		}
//	}
//
//	private void update() {
//		if (this.binding.hasName()) {
//			final String name = this.binding.getName();
//			this.setText(name);
//		}
//		if (this.binding.hasDescription()) {
//			final String description2 = this.binding.getDescription();
//			if ((description2 != null) && (description2.length() > 0)) {
//				this.setToolTipText(description2);
//			} else {
//				this.setToolTipText(description2);
//			}
//		}
//		this.setEnabledFromBinding(!this.binding.isReadOnly());
//	}
//
//	private void uninstallBinding(IBinding binding2) {
//		binding2.removeBindingChangeListener(this.chListener);
//	}
//
//	public String getImageId() {
//		return this.imageId;
//	}
//
//	public void setImageId(String imageId) {
//		this.imageId = imageId;
//		if ((imageId != null) && (imageId.length() > 0)) {
//			
//			this.setImageDescriptor(SWTImageManager.getDescriptor(imageId));
//			
//		} else {
//			this.setImageDescriptor(null);
//		}
//	}
//
//	public String getDisabledImageId() {
//		return this.disabledImageId;
//	}
//
//	public void setDisabledImageId(String disabledImageId) {
//		this.disabledImageId = disabledImageId;
//		if ((disabledImageId != null) && (disabledImageId.length() > 0)) {
//			final ImageObject imageObject = ImageManager.getInstance().get(
//					disabledImageId);
//			if (imageObject != null) {
//				this.setDisabledImageDescriptor(SWTImageManager.getDescriptor(disabledImageId));
//			} else {
//				Platform.log(new IllegalArgumentException(
//						"unable to find image with id:" + imageId));
//			}
//		} else {
//			this.setDisabledImageDescriptor(null);
//		}
//	}
//
//	public String getHoverImageId() {
//		return this.hoverImageId;
//	}
//
//	public void setHoverImageId(String hoverImageId) {
//		this.hoverImageId = hoverImageId;
//		if ((hoverImageId != null) && (hoverImageId.length() > 0)) {
//			this.setHoverImageDescriptor(SWTImageManager.getDescriptor(hoverImageId));
//		} else {
//			this.setHoverImageDescriptor(null);
//		}
//	}
//
//	public IBinding getBinding() {
//		return this.binding;
//	}
//
//	public void addBindingSetListener(IBindingSetListener listener) {
//		throw new UnsupportedOperationException();
//	}
//
//	public void removeBindingSetListener(IBindingSetListener listener) {
//		
//	}
//}
