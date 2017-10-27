package com.onpositive.semantic.editactions;

import com.onpositive.semantic.model.ui.actions.Action;

/**
 * Helper class for contributing some actions with {@link Runnable} instances
 * @author 32kda
 *
 */
public class RunnableAction extends Action {
	
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -5846450112234831323L;
	protected Runnable runnable;
	
	public RunnableAction(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		runnable.run();
	}

}
