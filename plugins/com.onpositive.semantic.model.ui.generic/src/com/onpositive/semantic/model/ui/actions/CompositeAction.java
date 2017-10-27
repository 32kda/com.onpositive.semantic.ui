package com.onpositive.semantic.model.ui.actions;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.onpositive.commons.xml.language.ChildSetter;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionBinding;

public class CompositeAction extends ActionBinding {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final LinkedHashSet<Binding> actions = new LinkedHashSet<Binding>();

	public CompositeAction() {

	}

	@ChildSetter(needCasting=false,value="abstractBinding")
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
