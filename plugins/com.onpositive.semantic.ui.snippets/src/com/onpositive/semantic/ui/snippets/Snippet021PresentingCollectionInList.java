package com.onpositive.semantic.ui.snippets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.SWTEventListener;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;

public class Snippet021PresentingCollectionInList extends AbstractSnippet {

	ArrayList<String> values = new ArrayList<String>();

	public Snippet021PresentingCollectionInList() {
		this.values.add("Pavel"); //$NON-NLS-1$
		this.values.add("Pete"); //$NON-NLS-1$
	}

	
	protected AbstractUIElement<?> createContent() {
		try {

			final Container sc = new Container();
			sc.setLayoutManager(new OneElementOnLineLayouter());
			final Binding binding = new Binding(this);
			final ListEnumeratedValueSelector<Object> as = new ListEnumeratedValueSelector<Object>(
					binding.getBinding("values")); //$NON-NLS-1$
			as.setValueAsSelection(false);
			sc.add(as);
			final ButtonSelector sl = new ButtonSelector();
			sl.setCaption("Add"); //$NON-NLS-1$
			sl.addListener(SWT.Selection, new SWTEventListener<Button>() {

				public void handleEvent(AbstractUIElement<Button> element,
						Event event) {
					as.addValue("Hello"); //$NON-NLS-1$
					System.out.println(Snippet021PresentingCollectionInList.this.values);
				}

			});
			sc.add(sl);
			DisposeBindingListener.linkBindingLifeCycle(binding, sc);
			return sc;
		} catch (final Exception e) {
			Activator.log(e);
		}
		return null;
	}

	
	protected String getDescription() {
		return "<b>Demonstrates different controls</b>"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Test"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Editable collection shown in list"; //$NON-NLS-1$
	}
}