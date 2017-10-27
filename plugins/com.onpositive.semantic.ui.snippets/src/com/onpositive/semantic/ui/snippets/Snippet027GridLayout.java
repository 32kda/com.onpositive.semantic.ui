package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.widgets.Composite;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;


public class Snippet027GridLayout extends AbstractSnippet
{
	
	
	protected AbstractUIElement<?> createContent() {
		try {
			final Binding context = new Binding(this);
			final AbstractUIElement<?> evaluateLocalPluginResource = (AbstractUIElement<?>) DOMEvaluator
					.getInstance().evaluateLocalPluginResource(
							Snippet027GridLayout.class,
							"snippet027.dlf", context); //$NON-NLS-1$
			DisposeBindingListener.linkBindingLifeCycle(context,
					evaluateLocalPluginResource);
			return evaluateLocalPluginResource;

		} catch (final Exception e) {
			Activator.log(e);
		}
		return null;
	}

	
	protected void createUI(Composite comp) {
		final RootElement cm = new RootElement(comp);
		final AbstractUIElement<?> createContent = this.createContent();
		cm.add(createContent);
	}
	
	
	protected String getDescription()
	{		
		return "Creating Grid Layout";
	}

	
	public String getGroup()
	{
		return "Creating Grid Layout";
	}

	
	protected String getName()
	{
		return "Creating Grid Layout";
	}

}
