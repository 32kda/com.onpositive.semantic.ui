package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;

public class SelectionBindingController extends EditorBindingController {

	private final IListElement<?> selector;

	
	protected void install(IBinding binding) {
		this.selector.setSelectionBinding(binding);
	}

	public SelectionBindingController(IListElement<?> editor,
			String path) {
		super(editor, path);
		this.selector = editor;
	}

	
	protected IBinding getBinding() {
		return this.selector.getSelectionBinding();
	}

}
