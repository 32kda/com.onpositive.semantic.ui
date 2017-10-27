package com.onpositive.commons.elements;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Control;

import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.IEnablementListener;

public class StackElement extends Container {

	public StackElement() {
		setLayout(new StackLayout());
	}

	@Override
	public void create() {
		super.create();
		StackLayout m = (StackLayout) getLayout();
		m.topControl = calcTop();
		getContentParent().layout();
	}

	IEnablementListener listener = new IEnablementListener() {

		public void enablementChanged(IUIElement<?> element,
				boolean enabled) {
			if (isCreated()) {
				StackLayout m = (StackLayout) getLayout();
				Object q = m.topControl;
				Control calcTop = calcTop();
				if (calcTop != q) {
					m.topControl = calcTop;
					getContentParent().layout(true);
				}
			}
		}
	};

	@Override
	public void adapt(AbstractUIElement<?> element) {
		element.addEnablementListener(listener);
		super.adapt(element);
	}

	@Override
	protected void unudapt(AbstractUIElement<?> element) {
		element.removeEnablementListener(listener);
		super.unudapt(element);
	}

	private Control calcTop() {
		for (AbstractUIElement<?>m:getChildren()){
			if (m.isEnabled()&&m.isCreated()){
				return m.getFirst();
			}
		}
		return null;
	}
}
