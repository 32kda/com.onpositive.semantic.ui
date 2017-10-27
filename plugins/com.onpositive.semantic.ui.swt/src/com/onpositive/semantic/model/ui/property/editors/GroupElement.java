package com.onpositive.semantic.model.ui.property.editors;

import com.onpositive.commons.elements.Container;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;

public class GroupElement extends Container{

	public GroupElement() {
		super(Container.GROUP);
		setLayoutManager(new OneElementOnLineLayouter());
	}
}
