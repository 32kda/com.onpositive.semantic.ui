package com.onpositive.status.adapters;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IStatusLineManager;

import com.onpositive.semantic.model.api.property.IHasStatus;
import com.onpositive.semantic.model.api.property.IStatusChangeListener;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;

public class StatusLineAdapter implements IStatusChangeListener {

	private IHasStatus current;
	private final IStatusLineManager lineManager;

	public StatusLineAdapter(IStatusLineManager statusLineManager) {
		this.lineManager = statusLineManager;
	}

	public void setStatusProvider(IHasStatus status) {
		if (status != this.current) {
			if (this.current != null) {
				this.current.removeStatusChangeListener(this);
			}
		}

		status.addStatusChangeListener(this);
		this.statusChanged(null, status.getStatus());
	}

	public void statusChanged(IBinding bnd, CodeAndMessage cm) {
		final String message = cm.getMessage();
		if (cm.getCode() == IStatus.ERROR) {
			this.lineManager.setErrorMessage(message);
		} else {
			this.lineManager.setErrorMessage(null);
			if ((message != null) && (message.length() > 0)) {
				this.lineManager.setMessage(message);
			} else {
				this.lineManager.setMessage(null);
			}
		}
	}
}
