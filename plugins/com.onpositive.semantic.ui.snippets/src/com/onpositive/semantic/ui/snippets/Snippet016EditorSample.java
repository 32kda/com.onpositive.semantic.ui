package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.commons.ui.appearance.HorizontalLayouter;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Enablement;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.binding.BindingStack;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;
import com.onpositive.semantic.model.ui.property.editors.FormEditor;
import com.onpositive.semantic.ui.core.Alignment;

public class Snippet016EditorSample extends AbstractSnippet {

	@Caption("%name")
	@Required
	String name;

	@RealmProvider(KnownPositions.class)
	@Required
	@Caption("%position")
	String position;

	@Required
	@Caption("%age")
	int age;

	public static class KnownPositions implements IRealmProvider<String> {

		public IRealm<String> getRealm(IBinding model) {
			return new Realm<String>(
					"Developer", "QA Engeneer", "Accountant", "Support", "Other"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$//$NON-NLS-5$
		}
	}

	
	protected void createUI(Composite comp) {
		final RootElement cm = new RootElement(comp);
		final AbstractUIElement<?> createContent = this.createContent();
		cm.add(createContent);
	}

	
	protected AbstractUIElement<?> createContent() {
		final FormEditor editor = new FormEditor(this, false);
		editor.setBindingInTitle(true);
		final OneElementOnLineLayouter el = new OneElementOnLineLayouter();
		editor.setLayoutManager(el);
		editor.add(UIElementFactory.createRichLabel(this.getDescriptionText()));
		editor.addString("name"); //$NON-NLS-1$
		editor.addCombo("position"); //$NON-NLS-1$
		editor.addSpinner("age"); //$NON-NLS-1$
		final CompositeEditor er = new CompositeEditor(editor.getBinding());
		er.getLayoutHints().setAlignmentHorizontal(Alignment.RIGHT);
		er.setLayoutManager(new HorizontalLayouter());
		er.addButton("ok"); //$NON-NLS-1$
		er.addButton("cancel"); //$NON-NLS-1$		
		editor.add(er);
		DisposeBindingListener.linkBindingLifeCycle(editor.getBinding(), editor
				.getUIElement());
		return editor;
	}

	@Caption("%Ok")
	@Enablement("!error")
	public void ok() {
		IBinding bnd = BindingStack.getCaller();
		while (bnd.getParent() != null) {
			bnd = bnd.getParent();
		}
		bnd.commit();
		this.print();
		Display.getCurrent().getActiveShell().close();
	}

	@Caption("%Cancel")
	public void cancel() {
		this.print();
		Display.getCurrent().getActiveShell().close();
	}

	private void print() {
		System.out.println("Name:" + this.name); //$NON-NLS-1$
		System.out.println("Position:" + this.position); //$NON-NLS-1$
		System.out.println("Age:" + this.age); //$NON-NLS-1$
	}

	protected String getDescriptionText() {
		return "<b>This Simple Snippet shows how to use Form Composite Editor</b>"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Simple Composite Editor"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}

	
	protected String getDescription() {
		return "";
	}
}