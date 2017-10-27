package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;

public class Snippet019FullyXMLGui2 extends AbstractSnippet {

	String name;
	String position;
	int age;

	
	protected Point getSize() {
		return new Point(500, 400);
	}

	
	protected AbstractUIElement<?> createContent() {
		try {
			final CompositeEditor evaluateLocalPluginResource = (CompositeEditor) DOMEvaluator
					.getInstance().evaluateLocalPluginResource(
							Snippet017XMLUIElementSnippet.class,
							"snippet019.dlf", this); //$NON-NLS-1$													
			DisposeBindingListener.linkBindingLifeCycle(
					evaluateLocalPluginResource.getBinding(),
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

	protected String getDescriptionText() {
		return "<b>Sample of Style Configuration dialog</b>"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Configure Styles"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "XML"; //$NON-NLS-1$
	}

	
	protected String getDescription() {
		return "";
	}

}