package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.binding.IBindingSetListener;
import com.onpositive.semantic.model.ui.actions.Action;

public class BindedAction extends Action implements IBindable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected IBinding binding;
	protected String oldTooltip;

	public void addBindingSetListener(IBindingSetListener listener) {
		throw new UnsupportedOperationException();
	}

	public void removeBindingSetListener(IBindingSetListener listener) {
		throw new UnsupportedOperationException();
	}

	private final IBindingChangeListener<Object> chListener = new IBindingChangeListener<Object>() {

		public void changed() {
			BindedAction.this.update();
		}

		public void enablementChanged(boolean isEnabled) {
			BindedAction.this.setEnabledFromBinding(isEnabled);
		}

		public void valueChanged(ISetDelta<Object> valueElements) {
			BindedAction.this.update();
		}

	};

	public BindedAction(int style) {
		super(style); //$NON-NLS-1$
	}

	public BindedAction(IBinding bnd, int style) {
		super(style); //$NON-NLS-1$
		this.setBinding(bnd);
	}

	public BindedAction(Binding binding2) {
		this.setBinding(binding2);
	}

	public void setThemeId(String attribute) {
		((Binding) this.binding).setTheme(attribute);
	}

	public IBinding getBinding() {
		return this.binding;
	}

	private void setEnabledFromBinding(boolean isEnabled) {
		if (ec!=null){
			return;
		}
		if (!isEnabled) {
			final String whyBindingIsDisabled = this.binding
					.getWhyBindingIsDisabled();
			if ((whyBindingIsDisabled != null)
					&& (whyBindingIsDisabled.length() > 0)) {
				if (this.isEnabled()) {
					this.oldTooltip = this.getToolTipText();
				}
				super.setToolTipText(whyBindingIsDisabled);
			}
		} else {
			if (this.oldTooltip != null) {
				super.setToolTipText(this.oldTooltip);
				this.oldTooltip = null;
			}
		}
		this.setEnabled(isEnabled);
	}

	public void run() {
		if (this.binding != null) {
			if (Action.class.isAssignableFrom(this.binding.getSubjectClass())) {
				this.binding.actionPerformed(null, this);
			} else if (IFunction.class.isAssignableFrom(this.binding
					.getSubjectClass())) {
				this.binding.actionPerformed(null, this);
			} else if (Runnable.class.isAssignableFrom(this.binding
					.getSubjectClass())) {
				this.binding.actionPerformed(null, this);
			} else {

				this.binding.setValue(this.isChecked(), this.chListener);
			}
		}
	}

	public void setBinding(IBinding bnd) {
		IBinding old=this.binding;
		if ((this.binding != null) && (this.binding != bnd)) {
			this.uninstallBinding(this.binding);
		}
		this.binding = bnd;
		if (bnd != null) {
			bnd.addBindingChangeListener(this.chListener);
			this.update();
		}
		if (ec!=null){
			ec.bindingChanged(null, bnd, old);
		}
	}

	public void setToolTipText(String toolTipText) {
		if (this.oldTooltip != null) {
			this.oldTooltip = toolTipText;
		} else {
			super.setToolTipText(toolTipText);
		}
	}

	private void update() {
		String name = this.binding.getName();
		if (name == null) {
			name = "";
		}
		if (name.length() > 0) {
			this.setText(name);
		}
		final String description2 = this.binding.getDescription();
		this.setToolTipText(description2);
		this.setEnabledFromBinding(!this.binding.isReadOnly());
	}

	private void uninstallBinding(IBinding binding2) {
		binding2.removeBindingChangeListener(this.chListener);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((binding == null) ? 0 : binding.hashCode());
		result = prime * result
				+ ((chListener == null) ? 0 : chListener.hashCode());
		result = prime * result
				+ ((oldTooltip == null) ? 0 : oldTooltip.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BindedAction other = (BindedAction) obj;
		if (binding == null) {
			if (other.binding != null)
				return false;
		} else if (!binding.equals(other.binding))
			return false;
		if (chListener == null) {
			if (other.chListener != null)
				return false;
		} else if (!chListener.equals(other.chListener))
			return false;
		if (oldTooltip == null) {
			if (other.oldTooltip != null)
				return false;
		} else if (!oldTooltip.equals(other.oldTooltip))
			return false;
		return true;
	}



}
