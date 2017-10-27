package com.onpositive.semantic.ui.snippets;

import java.util.ArrayList;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.property.adapters.ILabelLookup;
import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.api.property.adapters.NotFoundException;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.LabelLookup;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;

public class Snippet010ContentAssist extends AbstractSnippet {

	static class Person {
		String name;
		String position;
		String email;

		public Person(String email) {
			super();
			this.email = email;
			this.position = ""; //$NON-NLS-1$
			this.name = ""; //$NON-NLS-1$
		}

		public Person(String email, String position, String name) {
			super();
			this.email = email;
			this.name = name;
			this.position = position;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPosition() {
			return this.position;
		}

		public void setPosition(String position) {
			this.position = position;
		}

		public String getEmail() {
			return this.email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		
		public String toString() {
			return this.email + "(Name:" + this.name + " Position:" + this.position + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	ArrayList<Person> contacts = new ArrayList<Person>();

	Snippet010ContentAssist() {
		this.contacts
				.add(new Person(
						"petrochenko.pavel.a@gmail.com", "Developer", "Pavel Petrochenko")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.contacts.add(new Person("joe.doe@gmail.com", "Unknown", "Joe Doe")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.contacts.add(new Person(
				"mark.twain@gmail.com", "Tech Writer", "Mark Twain")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.contacts.add(new Person(
				"best.manager@gmail.com", "Manager", "Mike Murito")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static class ContactsProvider implements IRealmProvider<Person> {

		public ContactsProvider() {

		}

		public IRealm<Person> getRealm(IBinding model) {
			final Snippet010ContentAssist ds = (Snippet010ContentAssist) model
					.getObject();
			return new Realm<Person>(ds.contacts);
		}
	}

	public static class PersonLookup implements ILabelLookup {

		private static final String WRONG_EMAIL_ADRESS = "Wrong email adress"; //$NON-NLS-1$

		public Object lookUpByLabel(IBinding model, String label)
				throws NotFoundException {
			final Snippet010ContentAssist ds = (Snippet010ContentAssist) model
					.getObject();
			for (final Person p : ds.contacts) {
				if (p.email.equals(label)) {
					return p;
				}
			}
			final int indexOf = label.indexOf('@');
			if (indexOf == label.length() - 1) {
				throw new NotFoundException(WRONG_EMAIL_ADRESS);
			}
			if (indexOf > 0) {
				if (label.substring(0, indexOf).trim().length() == 0) {
					throw new NotFoundException(WRONG_EMAIL_ADRESS);
				}
				if (label.substring(indexOf + 1).trim().length() == 0) {
					throw new NotFoundException(WRONG_EMAIL_ADRESS);
				}
				return new Person(label);
			}
			throw new NotFoundException(WRONG_EMAIL_ADRESS);
		}

	}

	ArrayList<Person> mailTo = new ArrayList<Person>();

	boolean storeMail;

	@Caption("%clickMe")
	void buttonClicked() {
		System.out.println(this.mailTo);
	}

	
	protected AbstractUIElement<?> createContent() {
		final Container cm = new Container();
		cm.setLayoutManager(new OneElementOnLineLayouter());
		final Binding bs = new Binding(this);
		final OneLineTextElement<Person> element = new OneLineTextElement<Person>(bs
				.getBinding("MailTo")); //$NON-NLS-1$
		element.setContentAssistRole("contacts"); //$NON-NLS-1$
		cm.add(element);
		cm.add(new ButtonSelector(bs.getBinding("StoreMail"))); //$NON-NLS-1$
		cm.add(new ButtonSelector(bs.getBinding("buttonClicked"))); //$NON-NLS-1$
		DisposeBindingListener.linkBindingLifeCycle(bs, cm);
		return cm;
	}

	
	protected String getDescription() {
		return "This snippet shows how to use text field for selecting set of persons"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Text Field with content assist"; //$NON-NLS-1$
	}

	@Required("%atLeastOne")
	@RealmProvider(ContactsProvider.class)
	@Caption("%mailTo")
	@LabelLookup(PersonLookup.class)
	public ArrayList<Person> getMailTo() {
		return this.mailTo;
	}

	public void setMailTo(ArrayList<Person> mailTo) {
		this.mailTo = mailTo;
	}

	@Caption("%isStoreMail")
	public boolean isStoreMail() {
		return this.storeMail;
	}

	public void setStoreMail(boolean storeMail) {
		System.out.println("Storing mail changed"); //$NON-NLS-1$
		this.storeMail = storeMail;
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}
}
