package com.onpositive.semantic.model.api.status;

import java.util.LinkedHashSet;

public class SimpleHasStatus implements IHasStatus, IStatusChangeListener {

	private CodeAndMessage status = null;
	protected IHasStatus parent;

	public IHasStatus getParent() {
		return parent;
	}

	public void setParent(IHasStatus parent) {
		if (this.parent!=null){
			this.parent.removeStatusChangeListener(this);
		}
		this.parent = parent;
		if (this.parent!=null){
			this.parent.addStatusChangeListener(this);
		}
		if (this.status==null){
			fireChange(getStatus());
		}
	}

	@Override
	public CodeAndMessage getStatus() {
		if (status == null) {
			if (parent != null) {
				return parent.getStatus();
			}
			return CodeAndMessage.OK_MESSAGE;
		}
		return status;
	}

	public void setStatus(CodeAndMessage status) {
		if (this.status != null && !this.status.equals(status)) {
			this.status = status;
			fireChange(getStatus());
		}
		else{
			this.status=status;
			fireChange(getStatus());
		}
	}

	protected void fireChange(CodeAndMessage status) {
		if (sListeners != null) {
			for (IStatusChangeListener s : sListeners
					.toArray(new IStatusChangeListener[sListeners.size()])) {
				s.statusChanged(this, status);
			}
		}
	}

	protected LinkedHashSet<IStatusChangeListener> sListeners = null;

	@Override
	public void addStatusChangeListener(IStatusChangeListener listener) {
		if (sListeners == null) {
			sListeners = new LinkedHashSet<IStatusChangeListener>();
		}
		sListeners.add(listener);
	}

	@Override
	public void removeStatusChangeListener(IStatusChangeListener listener) {
		if (sListeners != null) {
			sListeners.remove(listener);
		}
		if (sListeners!=null&&sListeners.isEmpty()) {
			sListeners = null;
		}
	}

	@Override
	public void statusChanged(IHasStatus bnd, CodeAndMessage cm) {
		if (this.status == null) {
			fireChange(cm);
		}
	}
}
