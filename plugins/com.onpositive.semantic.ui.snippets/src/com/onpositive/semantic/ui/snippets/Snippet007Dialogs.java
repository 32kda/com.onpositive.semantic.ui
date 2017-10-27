package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.layout.GridLayout;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.commons.ui.dialogs.InputElementDialog;
import com.onpositive.commons.ui.dialogs.TitledDialog;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.realm.AbstractFactory;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;

public class Snippet007Dialogs extends AbstractSnippet {

	public static class Point {
		@Required
		@Caption("%name")
		String name;

		@Caption("%x")
		int x;

		@Caption("%y")
		int y;
	}

	private final class PopupDialogFactory extends AbstractFactory {
		private final Binding bs;

		private PopupDialogFactory(Binding bs) {
			super("Popup Dialog", ""); //$NON-NLS-1$ //$NON-NLS-2$
			this.bs = bs;
		}

		public Object getValue(Object context) {
			new InputElementDialog(this.bs, Snippet007Dialogs.this.createContainer(this.bs),
					"Point", "This dialog allows to configure point").open(); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
	}

	private final class TitleDialogFactory extends AbstractFactory {
		private final Binding bs;

		private TitleDialogFactory(Binding bs) {
			super("Title Area Dialog", ""); //$NON-NLS-1$//$NON-NLS-2$
			this.bs = bs;
		}

		public Object getValue(Object context) {
			new TitledDialog(this.bs, Snippet007Dialogs.this.createContainer(this.bs)).openWidget();
			return null;
		}
	}

	Point point = new Point();

	
	protected AbstractUIElement<?> createContent() {
		final Binding bs = new Binding(this.point);
		final Container el = this.createContainer(bs);
		// el.getChildren().get(0).setCaption("AA");
		final ButtonSelector sl = new ButtonSelector();
		sl.setValue(new TitleDialogFactory(bs));
		sl.setCaption("Titled Dialog");
		final ButtonSelector sl1 = new ButtonSelector();
		sl1.setValue(new PopupDialogFactory(bs));
		sl1.setCaption("Popup Dialog");
		final Container buttons = new Container();
		buttons.setLayout(new GridLayout(2, true));
		buttons.add(sl);
		buttons.add(sl1);
		el.add(UIElementFactory.createHorizontalSeparator());
		el.add(buttons);
		DisposeBindingListener.linkBindingLifeCycle(bs, el);
		return el;
	}

	private Container createContainer(Binding bs) {
		final Container el = new Container();
		el.setLayoutManager(new OneElementOnLineLayouter());
		final OneLineTextElement<String> name = new OneLineTextElement<String>(bs
				.getBinding("name")); //$NON-NLS-1$

		final OneLineTextElement<String> x = new OneLineTextElement<String>(bs
				.getBinding("x")); //$NON-NLS-1$
		final OneLineTextElement<String> y = new OneLineTextElement<String>(bs
				.getBinding("y")); //$NON-NLS-1$
		el.add(name);
		el.add(x);
		el.add(y);
		return el;
	}

	
	protected String getDescription() {
		return "Shows how to add Button(also demonstrates two way data binding)"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "This samplle shows dialogs"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}
}
