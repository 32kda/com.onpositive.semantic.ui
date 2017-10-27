package com.onpositive.semantic.ui.snippets;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.commons.elements.UniversalUIElement;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.common.ui.roles.IInformationalControlContentProducer;
import com.onpositive.semantic.model.api.property.java.JavaObjectManager;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Enablement;
import com.onpositive.semantic.model.api.property.java.annotations.LabelLookup;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;

public class Snippet011CustomInformationControls extends
		Snippet010ContentAssist {

	private static final String ENABLEMENT = "^cPerson&&!cc.contains(cPerson)&&!to.contains(cPerson)"; //$NON-NLS-1$

	@Required("%atLeastOne")
	@RealmProvider(ContactsProvider.class)
	@Caption("To:")
	@LabelLookup(PersonLookup.class)
	ArrayList<Person> to = new ArrayList<Person>();

	@RealmProvider(ContactsProvider.class)
	@Caption("Cc:")
	@LabelLookup(PersonLookup.class)
	ArrayList<Person> cc = new ArrayList<Person>();

	@RealmProvider(ContactsProvider.class)
	Person cPerson;

	@Enablement("!error")
	@Caption("Ok")
	void sent() {
		System.out.println("To:" + this.to); //$NON-NLS-1$
		System.out.println("Cc:" + this.cc); //$NON-NLS-1$
	}

	@Enablement(ENABLEMENT)
	@Caption("Cc:->")
	void addTocc() {
		this.cc.add(this.cPerson);
		JavaObjectManager.markChanged(this);
	}

	@Enablement(ENABLEMENT)
	@Caption("To:->")
	void setAsTo() {
		this.to.add(this.cPerson);
		JavaObjectManager.markChanged(this);
	}

	final class PersonInformationControlCreator implements
			IInformationalControlContentProducer {

		public Composite create(Object owner, Object parent, Object element,
				String role, String theme, String defContent) {
			final RootElement cs = new RootElement((Composite) parent);
			cs.setLayout(new GridLayout(2, false));
			cs.add(UIElementFactory.createRichLabel("<b>"+LabelManager.getInstance().getText(element, role, theme)+"</b>"));
			final UniversalUIElement<Label> createLabel = UIElementFactory
					.createImageLabel("com.onpositive.semantic.ui.editImage"); //$NON-NLS-1$				
			cs.add(createLabel);
			((Composite)parent).getShell().pack(true);
			return cs.getContentParent();
		}
	}

	
	protected AbstractUIElement<?> createContent() {
		final Container cm = new Container();
		cm.setLayoutManager(new OneElementOnLineLayouter());
		final Binding bs = new Binding(this);
		final PersonInformationControlCreator informationalControlContentProducer = this.addPrimaryControls(
				cm, bs);
		final Container ms = new Container();
		ms.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		final ListEnumeratedValueSelector<Person> ps = this.createListSelector(bs,
				informationalControlContentProducer);
		final Container actions = new Container();
		actions.setLayout(new FillLayout(SWT.VERTICAL));
		actions.add(new ButtonSelector(bs.getBinding("setAsTo"))); //$NON-NLS-1$
		actions.add(new ButtonSelector(bs.getBinding("addTocc"))); //$NON-NLS-1$		
		actions.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		ms.add(ps);
		ms.add(actions);
		ps.setLayoutData(new GridData(GridData.FILL_BOTH));
		cm.add(ms);
		cm.add(new ButtonSelector(bs.getBinding("sent"))); //$NON-NLS-1$
		DisposeBindingListener.linkBindingLifeCycle(bs, cm);
		return cm;
	}

	protected ListEnumeratedValueSelector<Person> createListSelector(
			Binding bs,
			PersonInformationControlCreator informationalControlContentProducer) {
		final ListEnumeratedValueSelector<Person> ps = new ListEnumeratedValueSelector<Person>(
				bs.getBinding("cPerson")); //$NON-NLS-1$
		ps
				.setTooltipInformationControlCreator(informationalControlContentProducer);
		ps.setElementRole("contacts"); //$NON-NLS-1$
		ps.setCaption(null);
		return ps;
	}

	protected PersonInformationControlCreator addPrimaryControls(Container cm,
			Binding bs) {
		final OneLineTextElement<Person> toTarget = new OneLineTextElement<Person>(bs
				.getBinding("to")); //$NON-NLS-1$
		final PersonInformationControlCreator informationalControlContentProducer = new PersonInformationControlCreator();
		toTarget
				.setContentAssistInformationControlCreator(informationalControlContentProducer);
		final OneLineTextElement<Person> cc = new OneLineTextElement<Person>(bs
				.getBinding("cc")); //$NON-NLS-1$
		cc
				.setContentAssistInformationControlCreator(informationalControlContentProducer);
		toTarget.setContentAssistRole("contacts"); //$NON-NLS-1$
		cc.setContentAssistRole("contacts"); //$NON-NLS-1$
		cm.add(toTarget);
		cm.add(cc);
		return informationalControlContentProducer;
	}

	
	protected String getDescription() {
		return "This snippet shows you how to setup cxustom tooltip and content assist informational controls"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Custom Informational Controls"; //$NON-NLS-1$
	}
}