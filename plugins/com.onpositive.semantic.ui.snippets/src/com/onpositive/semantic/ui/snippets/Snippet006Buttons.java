package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.widgets.Button;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Enablement;
import com.onpositive.semantic.model.api.property.java.annotations.Range;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;
import com.onpositive.semantic.model.ui.property.editors.structured.ComboEnumeratedValueSelector;

public class Snippet006Buttons extends AbstractSnippet {

	String value = "Hello world"; //$NON-NLS-1$

	@Range(min = 1, max = 150)
	@Caption("%age")
	int age = 0;

	@TextLabel(provider = YesNoProvider.class)
	@Caption("%isMarried")
	Boolean isMarried;

	@Enablement("!error")
	Runnable ms = new Runnable() {

		public void run() {
			System.out.println(Snippet006Buttons.this.value);
			System.out.println(Snippet006Buttons.this.age);
			System.out.println(Snippet006Buttons.this.isMarried);
		}
	};

	
	protected AbstractUIElement<?> createContent() {
		final Binding bs = new Binding(this);
		final Container el = new Container();
		el.setLayoutManager(new OneElementOnLineLayouter());
		final OneLineTextElement<String> name = new OneLineTextElement<String>(bs
				.getBinding("value")); //$NON-NLS-1$
		final OneLineTextElement<String> age = new OneLineTextElement<String>(bs
				.getBinding("age")); //$NON-NLS-1$
		final ComboEnumeratedValueSelector<Boolean> isM = new ComboEnumeratedValueSelector<Boolean>(
				bs.getBinding("isMarried")); //$NON-NLS-1$
		name.setCaption("Name:"); //$NON-NLS-1$
		final AbstractUIElement<Button> sl = new ButtonSelector(bs.getBinding("ms")); //$NON-NLS-1$
		sl.setCaption("Print Values"); //$NON-NLS-1$
		el.add(name);
		el.add(age);
		el.add(isM);
		el.add(UIElementFactory.createHorizontalSeparator(true, true));
		el.add(sl);
		return el;
	}

	
	protected String getDescription() {
		return "Shows how to add Button(also demonstrates two way data binding)"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Button that depends from form status"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}
}
