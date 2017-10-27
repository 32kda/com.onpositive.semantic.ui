package com.onpositive.semantic.ui.snippets;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.ui.dialogs.InputElementDialog;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.model.api.property.adapters.RegexpValidator;
import com.onpositive.semantic.model.api.property.java.JavaObjectManager;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.BindingStack;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;

public class Snippet019Contacts extends AbstractSnippet {

	static class Person {
		public Person(String fname, String lname) {
			this.firstName = fname;
			this.lastName = lname;
		}

		String firstName;
		String lastName;
		String company;
		String phone;
		String fax;
		String cell;
		HashSet<String> mailAddresses = new HashSet<String>();
	}

	ArrayList<Person> contactList = new ArrayList<Person>();

	public Snippet019Contacts() {
		this.contactList.add(new Person("Pavel", "Petrochenko")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	
	protected void createUI(Composite comp) {
		final RootElement cm = new RootElement(comp);
		final AbstractUIElement<?> createContent = this.createContent();
		cm.add(createContent);
	}

	
	protected AbstractUIElement<?> createContent() {
		try {
			final CompositeEditor evaluateLocalPluginResource = (CompositeEditor) DOMEvaluator
					.getInstance().evaluateLocalPluginResource(
							Snippet019Contacts.class, "snippet019.dlf", this); //$NON-NLS-1$													
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
		return "This snippet shows- how to create simple contacts editor"; //$NON-NLS-1$
	}

	public void addMail() {
		final IBinding root = BindingStack.getCaller().getRoot();
		final IBinding sPersons = root.getBinding("contact"); //$NON-NLS-1$
		final Person ps = (Person) sPersons.getValue();
		final Binding binding = new Binding(String.class) {
			public void commit() {
				final String value2 = (String) this.getValue();
				ps.mailAddresses.add(value2);
				JavaObjectManager.markChanged(ps);
			}
		};
		binding.addValidator(new RegexpValidator(
				".*@.*\\.*", "Email address is expected")); //$NON-NLS-1$ //$NON-NLS-2$
		binding.setName("Add New Mail address "); //$NON-NLS-1$
		binding.setDescription("Type email address,and then press enter"); //$NON-NLS-1$
		this.showDialog(binding);
	}

	public void newPerson() {
		final IBinding root = BindingStack.getCaller().getRoot();
		final Snippet019Contacts cs = (Snippet019Contacts) root.getValue();
		final Binding binding = new Binding(String.class) {
			public void commit() {
				final String value2 = (String) this.getValue();
				final int space = value2.indexOf(' ');
				final String fName = value2.substring(0, space);
				final String lName = value2.substring(space + 1);
				final Person ps = new Person(fName, lName);
				cs.contactList.add(ps);
				JavaObjectManager.markChanged(cs);
			}
		};
		binding
				.addValidator(new RegexpValidator(
						"(([a-z]|[A-Z])[a-z]+)\\s(([a-z]|[A-Z])[a-z]+)", "First Name and then Last Name are expected")); //$NON-NLS-1$ //$NON-NLS-2$
		binding.setName("Add New Person"); //$NON-NLS-1$
		binding.setDescription("Type Person full Name,and then press enter"); //$NON-NLS-1$
		this.showDialog(binding);
	}

	private void showDialog(final Binding binding) {
		binding.setAutoCommit(false);
		final OneLineTextElement<String> ra = new OneLineTextElement<String>(binding);
		final InputElementDialog dlg = new InputElementDialog(ra);
		dlg.open();
		DisposeBindingListener.linkBindingLifeCycle(binding, ra);
	}

	
	public String getGroup() {
		return "XML"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Contacts List"; //$NON-NLS-1$
	}

	
	protected Point getSize() {
		return new Point(600, 500);
	}
}