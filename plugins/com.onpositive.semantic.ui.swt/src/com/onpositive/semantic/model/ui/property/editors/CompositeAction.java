package com.onpositive.semantic.model.ui.property.editors;

import java.util.ArrayList;

import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionBinding;

public class CompositeAction extends ActionBinding {

	private final ArrayList<Binding> actions = new ArrayList<Binding>();

	public CompositeAction() {

	}

	public void addChild(Binding binding) {
		this.actions.add(binding);
	}

	protected void removeChild(Binding binding) {
		this.actions.remove(binding);
	}

	public void doAction() {
		for (final Binding a : new ArrayList<Binding>(this.actions)) {
			a.actionPerformed(null, null);
		}
	}
}
