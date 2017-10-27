package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

@SuppressWarnings("unchecked")
public class DisposeBindingListener extends ElementListenerAdapter {

	private final IBinding binding;

	public static boolean DEBUG;

	public DisposeBindingListener(IBinding binding) {
		super();
		this.binding = binding;
	}

	public void elementDisposed(IUIElement element) {
		if (this.binding != null) {
			this.binding.dispose();
		}
	}

	public static void linkBindingLifeCycle(IBinding bs,
			IUIElement element) {
		element.addElementListener(new DisposeBindingListener(bs));
	}
}
