package com.onpositive.semantic.ui.snippets;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.ToolbarElement;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.property.java.JavaObjectManager;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Enablement;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.BindedAction;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;

public class Snippet012Toolbar extends Snippet011CustomInformationControls {

	@Enablement("(^cPerson):%PersonNotSelected&&(cc.contains(cPerson)||to.contains(cPerson)):%PersonNotConained")
	@Caption("Remove")
	void remove() {
		this.cc.remove(this.cPerson);
		this.to.remove(this.cPerson);
		JavaObjectManager.markChanged(this);
	}

	
	protected AbstractUIElement<?> createContent() {
		final Container cm = new Container();
		cm.setLayoutManager(new OneElementOnLineLayouter());
		final Binding bs = new Binding(this);
		final PersonInformationControlCreator informationalControlContentProducer = this
				.addPrimaryControls(cm, bs);
		final Container ms = new Container();
		ms.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		final ListEnumeratedValueSelector<Person> ps = this.createListSelector(
				bs, informationalControlContentProducer);
		final ToolbarElement actions = new ToolbarElement();
		final BindedAction action = new BindedAction(bs.getBinding("setAsTo")); //$NON-NLS-1$
		action.setImageId("add"); //$NON-NLS-1$
		action.setDisabledImageId("addd"); //$NON-NLS-1$
		action.setToolTipText("Add Selected Person to To"); //$NON-NLS-1$
		actions.addToToolbar(action);
		final BindedAction action2 = new BindedAction(bs.getBinding("addTocc")); //$NON-NLS-1$
		action2.setToolTipText("Add Selected Person to CC"); //$NON-NLS-1$
		action2.setImageId("add2"); //$NON-NLS-1$
		action2.setDisabledImageId("add2d"); //$NON-NLS-1$
		final BindedAction action3 = new BindedAction(bs.getBinding("remove")); //$NON-NLS-1$		
		action3.setToolTipText("remove selected person from to and cc"); //$NON-NLS-1$
		action3.setImageId("delete"); //$NON-NLS-1$
		action3.setDisabledImageId("deleted"); //$NON-NLS-1$
		actions.addToToolbar(action2);
		actions.addToToolbar(action3);
		actions.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2,
				1));
		ms.add(actions);
		ms.add(ps);
		ps.setLayoutData(new GridData(GridData.FILL_BOTH));
		cm.add(ms);
		cm.add(new ButtonSelector(bs.getBinding("sent"))); //$NON-NLS-1$
		DisposeBindingListener.linkBindingLifeCycle(bs, cm);
		return cm;
	}

	
	protected String getDescription() {
		return "This snippet shows use of toolbar element"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "The same as previous but with toolbar"; //$NON-NLS-1$
	}
}
