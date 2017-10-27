package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.widgets.Composite;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;


public class Snippet026RichText extends AbstractSnippet
{

	String textStr = ""; 
	
	
	protected void createUI(Composite comp) {
		final RootElement cm = new RootElement(comp);
		final AbstractUIElement<?> createContent = this.createContent();
		cm.add(createContent);
	}
	
	
	protected AbstractUIElement<?> createContent()
	{
		try {
			final Binding context = new Binding(this);
			final AbstractUIElement<?> evaluateLocalPluginResource = (AbstractUIElement<?>) DOMEvaluator
					.getInstance().evaluateLocalPluginResource(
							Snippet026RichText.class,
							"snippet026.dlf", context); //$NON-NLS-1$
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
		return "richtext";
	}

	
	public String getGroup()
	{
		// TODO Auto-generated method stub
		return "richtext";
	}

	
	protected String getName()
	{
		// TODO Auto-generated method stub
		return "richtext";
	}
	
	public void print()
	{
		System.out.println(textStr);
	}

}
