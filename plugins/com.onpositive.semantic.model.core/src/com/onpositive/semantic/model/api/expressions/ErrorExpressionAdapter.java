package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.status.IStatusChangeListener;

public class ErrorExpressionAdapter extends
		AbstractListenableExpression<Boolean> implements IStatusChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final IHasStatus status;

	public ErrorExpressionAdapter(IHasStatus status) {
		this.status = status;
		status.addStatusChangeListener(this);
		this.statusChanged(null, status.getStatus());
	}

	@Override
	public void dispose() {
		this.status.removeStatusChangeListener(this);
		super.dispose();
	}

	@Override
	public void statusChanged(IHasStatus bnd, CodeAndMessage cm) {
		final boolean mm = cm.getCode() == CodeAndMessage.ERROR;
		this.setNewValue(mm);
	}
}
