package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.widgets.Button;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Enablement;
import com.onpositive.semantic.model.api.property.java.annotations.Range;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;
import com.onpositive.semantic.model.api.property.java.annotations.Validator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.AbstractFactory;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;

public class Snippet004CheckControl extends AbstractSnippet {

	String value = "Hello world"; //$NON-NLS-1$

	@Enablement("!isMarried")
	@Caption("%friends")
		
	String friends = "Pite, Mike"; //$NON-NLS-1$

	@Enablement("isMarried")
	@Caption("%wife")
	String wife = "Helen"; //$NON-NLS-1$

	@Caption("%age")
	@Range(min = 0, max = 150)
	int age = 0;

	@Caption("%isMarried")
	@TextLabel(provider = YesNoProvider.class)
	boolean isMarried;

	
	protected AbstractUIElement<?> createContent() {
		final Binding bs = new Binding(this);
		final Container el = new Container();
		el.setLayoutManager(new OneElementOnLineLayouter());
		final OneLineTextElement<String> name = new OneLineTextElement<String>(bs
				.getBinding("value")); //$NON-NLS-1$
		final OneLineTextElement<String> age = new OneLineTextElement<String>(bs
				.getBinding("age")); //$NON-NLS-1$
		final IBinding binding = bs.getBinding("isMarried"); //$NON-NLS-1$
		final AbstractUIElement<Button> isM = new ButtonSelector(binding);

		final OneLineTextElement<String> friends = new OneLineTextElement<String>(
				bs.getBinding("friends")); //$NON-NLS-1$
		final OneLineTextElement<String> wife = new OneLineTextElement<String>(
				bs.getBinding("wife")); //$NON-NLS-1$
		wife.setSelector(new AbstractFactory("Break Marriage...", "") { //$NON-NLS-1$ //$NON-NLS-2$

					public Object getValue(Object context) {
						binding.setValue(false, null);
						return null;
					}

				});
		name.setCaption("Name:"); //$NON-NLS-1$		
		el.add(name);
		el.add(age);
		el.add(isM);
		el.add(friends);
		el.add(wife);
		DisposeBindingListener.linkBindingLifeCycle(bs, el);
		return el;
	}

	
	protected String getDescription() {
		return "This snippet shows how binding enablement is connected to controls"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Binding enablement"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}
}
