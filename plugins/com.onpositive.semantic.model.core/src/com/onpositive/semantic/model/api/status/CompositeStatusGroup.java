package com.onpositive.semantic.model.api.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * TODO DO WE NEED IT
 * @author amst
 *
 */
public class CompositeStatusGroup implements Iterable<IHasStatus>, IHasStatus {

	private final ArrayList<IHasStatus> bindings = new ArrayList<IHasStatus>();
	private final HashSet<IStatusChangeListener> slisteners = new HashSet<IStatusChangeListener>();
	private CodeAndMessage status = CodeAndMessage.OK_MESSAGE;
	protected CodeAndMessage cstatus = CodeAndMessage.OK_MESSAGE;

	private final IStatusChangeListener bl = new IStatusChangeListener() {

		@Override
		public void statusChanged(IHasStatus bnd, CodeAndMessage cm) {
			CompositeStatusGroup.this.doValidate(bnd);
		}

	};

	protected void doValidate(IHasStatus bnd) {
		CodeAndMessage maxStatus = CodeAndMessage.OK_MESSAGE;
		for (final IHasStatus m : this.bindings) {
			final CodeAndMessage ms = m.getStatus();
			if (ms.getCode() > maxStatus.getCode()) {
				maxStatus = ms;
			}
		}
		if (this.cstatus.getCode() > maxStatus.getCode()) {
			maxStatus = this.cstatus;
		}
		if (!maxStatus.equals(this.status)) {
			this.status = maxStatus;
			for (final IStatusChangeListener s : this.slisteners) {
				s.statusChanged(bnd, this.status);
			}
		}
	}

	@Override
	public CodeAndMessage getStatus() {
		return this.status;
	}

	public void addBinding(IHasStatus bs) {
		this.bindings.add(bs);
		bs.addStatusChangeListener(this.bl);

	}

	public void removeBinding(IHasStatus bs) {
		this.bindings.remove(bs);
		bs.removeStatusChangeListener(this.bl);
	}

	@Override
	public Iterator<IHasStatus> iterator() {
		return Collections.unmodifiableList(this.bindings).iterator();
	}

	@Override
	public void addStatusChangeListener(IStatusChangeListener listener) {
		this.slisteners.add(listener);
	}

	@Override
	public void removeStatusChangeListener(IStatusChangeListener listener) {
		this.slisteners.remove(listener);
	}
}
