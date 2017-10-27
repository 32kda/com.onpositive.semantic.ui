package com.onpositive.semantic.model.api.status;


public interface IHasStatus {

	public CodeAndMessage getStatus();

	void addStatusChangeListener(IStatusChangeListener listener);

	void removeStatusChangeListener(IStatusChangeListener listener);
}
