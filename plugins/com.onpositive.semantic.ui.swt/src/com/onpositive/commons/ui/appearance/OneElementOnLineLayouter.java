package com.onpositive.commons.ui.appearance;

import java.util.HashSet;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;

public class OneElementOnLineLayouter extends AbstractLayouter {

	public void childCreating(Container container, AbstractUIElement<?> element) {
		if (element instanceof Container) {
			this.propogateLabel(container, element);
		}
	}

	HashSet<Container> propogated = new HashSet<Container>();

	private void propogateLabel(Container container,
			AbstractUIElement<?> element) {
		final Container cm = (Container) element;
		if (cm.getLayoutManager() instanceof HorizontalLayouter) {
			if (!cm.isGroup()) {
				final AbstractUIElement<?> firstChild = cm.getFirstChild();
				if (firstChild != null) {
					if (firstChild.needsLabel()) {
						firstChild.createLabel(container);
						this.propogated.add(cm);
					}
				}
			}
		}
	}

	public void elementDisposed(Container element) {
		this.propogated.clear();
		super.elementDisposed(element);
	}

	public void elementRemoved(Container cnt, AbstractUIElement<?> element) {
		if (element instanceof Container) {
			this.propogated.remove(element);
		}
	}

	public void elementAdding(Container container, AbstractUIElement<?> element) {
		if (container.isCreated() && (element instanceof Container)) {
			this.propogateLabel(container, element);
		}
		super.elementAdding(container, element);
	}

	protected int getSpan(AbstractUIElement<?> element, int mn,
			Control[] allControls) {
		return mn - allControls.length
				+ (!this.propogated.contains(element) ? 1 : 0);
	}

	public void doLayout(Container cm) {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 8;
		layout.marginRight = 4;
		layout.marginLeft = 0;
		layout = super.adaptLayout(cm, layout);
		int mn = 0;
		for (final AbstractUIElement<?> w : cm.getChildren()) {
			final Control[] allControls = w.getAllControls();
			mn = Math.max(allControls.length, mn);
			if (propogated.contains(w)) {
				mn+=1;
			}
		}
		layout.numColumns = mn;
		cm.setLayout(layout);
		this.calcLayout(cm, mn);
	}

	public boolean isPropogated(AbstractUIElement<?> cm) {
		return this.propogated.contains(cm);
	}

}
