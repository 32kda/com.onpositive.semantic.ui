package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Enablement;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.BindingStack;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;

public class Snippet017XMLUIElementSnippet extends AbstractSnippet {

	// start of data model
	@Caption("%name") //Caption setting annotation
	@Required //Validating annotation
	String name;

	@RealmProvider(KnownPositions.class) //Realm provider for ComboBox elements list
	@Required
	@Caption("%position")
	String position;

	@Required
	@Caption("%age")
	int age;
	
	@Caption("RGB")
	RGB rgb = new RGB(255,0,0); 

	public static class KnownPositions implements IRealmProvider<String> {

		public IRealm<String> getRealm(IBinding options) {
			return new Realm<String>(
					"Developer", "QA Engeneer", "Accountant", "Support", "Other"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$//$NON-NLS-5$
		}
	}

	// end data model

	/**
	 * 'ok' button handler
	 */
	@Caption("%Ok")
	@Enablement("!error") // Enablement rule for button
	public void ok() {
		final IBinding bnd = BindingStack.getCaller().getRoot();
		bnd.commit();
		this.print();
		Display.getCurrent().getActiveShell().close();
		
	}

	/**
	 * 'cancel' button handler
	 */
	@Caption("%Cancel")
	public void cancel() {
		this.print();
		Display.getCurrent().getActiveShell().close();
	}

	
	protected AbstractUIElement<?> createContent() {
		try {
			final Binding context = new Binding(this);
			final AbstractUIElement<?> evaluateLocalPluginResource = (AbstractUIElement<?>) DOMEvaluator
					.getInstance().evaluateLocalPluginResource(
							Snippet017XMLUIElementSnippet.class,
							"snippet017.xml", context); //$NON-NLS-1$
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

	protected String getDescriptionText() {
		return "<b>This snippet shows how to build gui from XML</b>"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "First XML GUI"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "XML"; //$NON-NLS-1$
	}

	
	protected Point getSize() {
		return new Point(500, 300);
	}

	/**
	 * prints current content
	 */
	private void print() {
		System.out.println("Name:" + this.name); //$NON-NLS-1$
		System.out.println("Position:" + this.position); //$NON-NLS-1$
		System.out.println("Age:" + this.age); //$NON-NLS-1$
		System.out.println(rgb.toString());
	}

	
	protected String getDescription() {
		return "";
	}
}