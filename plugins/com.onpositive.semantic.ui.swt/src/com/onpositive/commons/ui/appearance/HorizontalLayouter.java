package com.onpositive.commons.ui.appearance;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;

public class HorizontalLayouter extends AbstractLayouter {

	public HorizontalLayouter(){
		
	}
	
	public void elementAdding(Container cnt, AbstractUIElement<?> element) {
		if (cnt.isCreated()) {
			final Container parent = cnt.getParent();
			if (parent != null) {
				final IContainerLayoutManager layoutManager = parent
						.getLayoutManager();
				if (layoutManager instanceof OneElementOnLineLayouter) {

				}
			}
		}
	}

	public void elementRemoved(Container cnt, AbstractUIElement<?> element) {
		if (cnt.isCreated()) {
			final Container parent = cnt.getParent();
			if (parent != null) {
				final IContainerLayoutManager layoutManager = parent
						.getLayoutManager();
				if (layoutManager instanceof OneElementOnLineLayouter) {

				}
			}
		}
		super.elementRemoved(cnt, element);
	}

	public void doLayout(Container cm) {
		GridLayout layout = new GridLayout(1,false);

		final Container parent = cm.getParent();
		boolean isPropogated = false;
		if (parent != null) {
			final IContainerLayoutManager layoutManager = parent
					.getLayoutManager();
			if (layoutManager instanceof OneElementOnLineLayouter) {
				isPropogated = true;
			}
		}
		if (isPropogated) {
			layout.marginRight = 0;
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.marginTop = 0;
			layout.marginLeft = 2;
			layout.marginBottom = 0;
			layout.horizontalSpacing = 8;
		} else {
			layout.marginRight = 0;
			layout.marginLeft = 0;
			layout.horizontalSpacing = 8;
		}
		layout = super.adaptLayout(cm, layout);
		int mn = 0;
		for (final AbstractUIElement<?> w : cm.getChildren()) {
			final Control[] allControls = w.getAllControls();
			mn += allControls.length;
		}
		layout.numColumns = mn;
		cm.setLayout(layout);
		this.calcLayout(cm, -1);
	}

	public void childCreating(Container container, AbstractUIElement<?> e) {

	}
}
