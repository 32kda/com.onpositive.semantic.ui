package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.widgets.Button;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Range;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;

public class Snippet002HelloWorld3 extends AbstractSnippet {

	String value = "Hello world"; //$NON-NLS-1$

	@Caption("%friends")
	String friends = "Pite, Mike"; //$NON-NLS-1$
	@Caption("%wife")
	String wife = "Helen"; //$NON-NLS-1$

	@Range(min = 0, max = 150)
	@Caption("%age")
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
		binding.addValueListener(new IValueListener<Boolean>() {

			public void valueChanged(Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					friends.setBinding(bs.getBinding("friends")); //$NON-NLS-1$
				} else {
					friends.setBinding(bs.getBinding("wife")); //$NON-NLS-1$
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
		return "This snippet shows how control binding may be changed at runtime"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Binding Change"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}
}
