package com.onpositive.semantic.ui.snippets;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;


public class Snippet028BindingDemonstration extends AbstractSnippet
{

	protected String name; //Field, on which we mapped our text field and label
	
	
	protected AbstractUIElement<?> createContent() //This method evaluate form content
	//and bindings form our dlf-file. (dlf is XML subset, this file contents is shown upper) 
	{
		try {
			final Binding context = new Binding(this);
			final AbstractUIElement<?> evaluateLocalPluginResource = (AbstractUIElement<?>) DOMEvaluator
					.getInstance().evaluateLocalPluginResource(
							Snippet028BindingDemonstration.class,
							"snippet028.dlf", context); //$NON-NLS-1$
			DisposeBindingListener.linkBindingLifeCycle(context,
					evaluateLocalPluginResource);
			//context.getBinding("textStr").setValue("",null);
			return evaluateLocalPluginResource;

		} catch (final Exception e) {
 			e.printStackTrace();
			Activator.log(e);
		}
		return null;
	}

	
	protected String getDescription()
	{
		return "Simple bindings demo";
	}

	
	public String getGroup()
	{
		return "";
	}

	
	protected String getName()
	{
		return "Bindings";
	}

}
