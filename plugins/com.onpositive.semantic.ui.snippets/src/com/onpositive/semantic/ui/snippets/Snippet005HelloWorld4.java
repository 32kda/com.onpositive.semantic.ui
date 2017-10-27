package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.widgets.Button;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.api.property.adapters.RealmProviderAdapter;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Range;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;
import com.onpositive.semantic.model.api.property.java.annotations.Validator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.ProxyBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.realm.ValidatorAdapter;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;

public class Snippet005HelloWorld4 extends AbstractSnippet {

	String value = "Hello world"; //$NON-NLS-1$

	public static class FriendsDescriptor extends RealmProviderAdapter<String> {

		public IRealm<String> getRealm(IBinding model) {
			return new Realm<String>("Mike", "Paul", "Jannet", "Pite", "Jerry"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}

	}

	public static final class WifeValidator extends ValidatorAdapter<String> {

		public CodeAndMessage isValid(IBinding context, String object) {
			if ((object == null) || (object.length() == 0)) {
				return CodeAndMessage
						.errorMessage("Wife Name should not be empty"); //$NON-NLS-1$
			}
			return super.isValid(context, object);
		}
	}

	public static class WifeDescriptor extends RealmProviderAdapter<String> {

		public IRealm<String> getRealm(IBinding model) {
			return new Realm<String>(
					"Helen", "Svetlana", "May", "Jannet", "Olga"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}

	}

	@RealmProvider(FriendsDescriptor.class)
	@Caption("%friends")
	String friends = "Pite, Mike"; //$NON-NLS-1$

	@RealmProvider(WifeDescriptor.class)
	@Validator(validatorClass = WifeValidator.class)
	@Required
	@Caption("%wife")
	String wife;

	@Range(min = 0, max = 150)
	@Caption("%age")
	int age = 0;

	@TextLabel(provider = YesNoProvider.class)
	@Caption("%isMarried")
	boolean isMarried;

	
	protected AbstractUIElement<?> createContent() {
		final Binding bs = new Binding(this);
		final Container el = new Container();
		el.setLayoutManager(new OneElementOnLineLayouter());
		final OneLineTextElement<String> name = new OneLineTextElement<String>(
				bs.getBinding("value")); //$NON-NLS-1$
		final OneLineTextElement<String> age = new OneLineTextElement<String>(
				bs.getBinding("age")); //$NON-NLS-1$
		final IBinding binding = bs.getBinding("isMarried"); //$NON-NLS-1$
		final AbstractUIElement<Button> isM = new ButtonSelector(binding);
		final ProxyBinding ps = new ProxyBinding(bs.getBinding("friends")); //$NON-NLS-1$
		final OneLineTextElement<String> friends = new OneLineTextElement<String>(
				ps);
		binding.addValueListener(new IValueListener<Boolean>() {

			public void valueChanged(Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					ps.setOwner(bs.getBinding("friends")); //$NON-NLS-1$
				} else {
					ps.setOwner(bs.getBinding("wife")); //$NON-NLS-1$
				}
			}

		});
		name.setCaption("Name:"); //$NON-NLS-1$		
		el.add(name);
		el.add(age);
		el.add(isM);
		el.add(friends);
		DisposeBindingListener.linkBindingLifeCycle(bs, el);
		return el;
	}

	
	protected String getDescription() {
		return "This snippet shows how control binding may be changed at runtime using proxy binging"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Binding Change using proxy binding"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}
}
