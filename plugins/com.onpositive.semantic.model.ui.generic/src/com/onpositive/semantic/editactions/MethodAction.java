package com.onpositive.semantic.editactions;

import java.util.Arrays;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.method.IMethodEvaluator;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;

public class MethodAction extends Action {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 8582005795503966665L;
	protected final Object[] baseObjects;
	protected final IMethodEvaluator evaluator;
	protected Object[] params = new Object[0];
	private IBinding bnd;
	
	public MethodAction(IBinding b, Object[] baseObjects, IMethodEvaluator evaluator) {
		this.baseObjects = baseObjects;
		this.evaluator = evaluator;
		String imageKey = DefaultMetaKeys.getImageKey(evaluator);
		if (!imageKey.isEmpty()) {
			ImageDescriptor descriptor = ImageManager.getImageDescriptorByPath(this.baseObjects[0], imageKey);
			if (descriptor != null)
				setImageDescriptor(descriptor);
		}
		this.bnd=b;
	}
	
	public MethodAction(IBinding b, Object[] baseObjects, IMethodEvaluator evaluator, Object[] params) {
		this(b,baseObjects,evaluator);
		
		this.params = params;
	}

	@Override
	public void run() {
		for (int i = 0; i < baseObjects.length; i++) {
			evaluator.evaluateCall(baseObjects[i],params); //Currently we don't support method result handling - only call is performed
		}
		((AbstractBinding)bnd).onChildChanged();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ArrayUtils.identityHashCode(baseObjects);
		result = prime * result
				+ ((evaluator == null) ? 0 : evaluator.hashCode());
		result = prime * result + ArrayUtils.identityHashCode(params);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodAction other = (MethodAction) obj;
		if (!Arrays.equals(baseObjects, other.baseObjects))
			return false;
		if (evaluator == null) {
			if (other.evaluator != null)
				return false;
		} else if (!evaluator.equals(other.evaluator))
			return false;
		if (!Arrays.equals(params, other.params))
			return false;
		return true;
	}

}
