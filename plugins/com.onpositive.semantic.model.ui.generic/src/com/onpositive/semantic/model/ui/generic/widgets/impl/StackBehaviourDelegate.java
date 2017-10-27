package com.onpositive.semantic.model.ui.generic.widgets.impl;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.IStackComposite;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class StackBehaviourDelegate implements ICompositeDelegate {
	
	private BasicUIElement<?> uiElement;
	
	IEnablementListener listener = new IEnablementListener() {

		public void enablementChanged(IUIElement<?> element,
				boolean enabled) {
			onEnablement();
		}
	};

	public StackBehaviourDelegate(BasicUIElement<?> uiElement) {
		this.uiElement = uiElement;
	}

	@Override
	public void onCreateStart(BasicUIElement<?> element) {
		// Do nothing
	}

	@Override
	public void onCreateEnd(BasicUIElement<?> element) {
		((IStackComposite) uiElement).setTopControl(calcTop());
	}

	@Override
	public void onDispose(BasicUIElement<?> element) {
		// Do nothing
	}

	@Override
	public void processValueChange(ISetDelta<?> valueElements) {
		// Do nothing
	}

	@Override
	public void internalSetBinding(IBinding binding2) {
		// Do nothing
	}

	@Override
	public void setValue(Object value) {
		// Do nothing

	}

	@Override
	public void handleChange(IUIElement<?> b, Object value) {
		// Do nothing
	}

	@Override
	public void adapt(BasicUIElement<?> element) {
		element.addEnablementListener(listener);
	}
	
	@Override
	public void unadapt(BasicUIElement<?> element) {
		element.removeEnablementListener(listener);
	}

	protected BasicUIElement<?> calcTop() {
		for (BasicUIElement<?>element:((IStackComposite)uiElement).getChildren()){
			if (element.isEnabled()){
				return element;
			}
		}
		return null;
	}

	protected void onEnablement() {
		if (uiElement.isCreated()) {
			recalc();
		}
	}

	public void recalc() {
		IStackComposite stackComposite = (IStackComposite) uiElement;
		Object oldTop = stackComposite.getTopControl();
		BasicUIElement<?> newTop = calcTop();
		if (newTop != oldTop) {
			stackComposite.setTopControl(newTop);
		}
	}

}
