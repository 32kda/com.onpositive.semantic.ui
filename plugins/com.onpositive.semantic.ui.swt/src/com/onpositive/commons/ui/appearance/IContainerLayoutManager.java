package com.onpositive.commons.ui.appearance;

import org.eclipse.swt.widgets.Composite;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;

public interface IContainerLayoutManager {

	public void elementCreated(Container element);

	public void elementDisposed(Container element);

	void elementAdded(Container cnt, AbstractUIElement<?> element);

	void elementRemoved(Container cnt, AbstractUIElement<?> element);

	public void uninstall(Container container);

	public void install(Container container);

	public void elementAdding(Container container, AbstractUIElement<?> element);

	public Composite getLabelParent(Container container,
			AbstractUIElement<?> element);

	public void childCreating(Container container, AbstractUIElement<?> e);
}
