package com.onpositive.semantic.ui.snippets;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;

public class Snippet020Checkboxes extends AbstractSnippet {

	
	protected AbstractUIElement<?> createContent() {
		try {
			final CompositeEditor evaluateLocalPluginResource = (CompositeEditor) DOMEvaluator
					.getInstance().evaluateLocalPluginResource(
							Snippet019Contacts.class, "snippet20.xml", this); //$NON-NLS-1$													
			DisposeBindingListener.linkBindingLifeCycle(
					evaluateLocalPluginResource.getBinding(),
					evaluateLocalPluginResource);
			return evaluateLocalPluginResource;

		} catch (final Exception e) {
			Activator.log(e);
		}
		return null;
	}

	
	protected String getDescription() {
		return "<b>Demonstrates different controls</b>"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "XML"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "CheckBoxes And Tab Panel"; //$NON-NLS-1$
	}

}