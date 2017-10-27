package com.onpositive.semantic.ui.snippets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.semantic.model.api.property.java.JavaObjectManager;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.realm.OrderedRealm;
import com.onpositive.semantic.model.tree.GroupingPointProvider;
import com.onpositive.semantic.model.tree.IClusterizationPointProvider;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.model.ui.property.editors.structured.AbstractEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;
import com.onpositive.semantic.ui.snippets.engine.Snippet030EngineSnippet;

public class SnippetList extends AbstractSnippet {

	
	protected Point getSize() {
		return null;
	}

	
	ArrayList<AbstractSnippet> all = new ArrayList<AbstractSnippet>();

	public SnippetList() {
		this.all.add(new Snippet000HelloWorld());
		this.all.add(new Snippet001HelloWorld2());
		this.all.add(new Snippet002HelloWorld3());
		this.all.add(new Snippet003RadioAndGroups());
		this.all.add(new Snippet004CheckControl());
		this.all.add(new Snippet005HelloWorld4());
		this.all.add(new Snippet006Buttons());
		this.all.add(new Snippet007Dialogs());
		this.all.add(new Snippet008Club());
		this.all.add(new Snippet009Extensions());
		this.all.add(new Snippet010ContentAssist());
		this.all.add(new Snippet011CustomInformationControls());
		this.all.add(new Snippet012Toolbar());
		this.all.add(new Snippet013PopupMenu());
		this.all.add(new Snippet014MultyValueTest());
		this.all.add(new Snippet015TreeMultiValueTest());
		this.all.add(new Snippet016EditorSample());
		this.all.add(new Snippet017XMLUIElementSnippet());
		this.all.add(new Snippet018FullyXMLGui());
		//this.all.add(new Snippet019FullyXMLGui2());
		//this.all.add(new Snippet019Contacts());
		this.all.add(new Snippet020Checkboxes());
		this.all.add(new Snippet021PresentingCollectionInList());
		this.all.add(new Snippet022LabelPropogation());
		this.all.add(new Snippet023Columns());
		this.all.add(new Snippet024CustomCellEditor());
		this.all.add(new Snippet025MyFirstSnippet());
		this.all.add(new Snippet026RichText());
		this.all.add(new Snippet027GridLayout());
		this.all.add(new Snippet028BindingDemonstration());
		this.all.add(new Snippet029Summator());
		this.all.add(new Snippet030EngineSnippet());
	}

	
	protected int getStyle() {
		return SWT.DIALOG_TRIM | SWT.RESIZE;
	}

	private AbstractEnumeratedValueSelector<AbstractSnippet> createSelector(
			Binding objectBinding) {
		final ListEnumeratedValueSelector<AbstractSnippet> listEnumeratedValueSelector = new ListEnumeratedValueSelector<AbstractSnippet>(
				objectBinding);
		// listEnumeratedValueSelector.setAsTree(true);
		listEnumeratedValueSelector
				.setClusterizationPointProviders(null, new IClusterizationPointProvider[]{new GroupingPointProvider<AbstractSnippet>() {

					
					public Set<String> getGroup(AbstractSnippet o) {
						return Collections.singleton(o.getGroup());
					}

					
					public Object getPresentationObject(Object o) {
						return o;
					}

				}});
		return listEnumeratedValueSelector;
	}

	
	protected String getDescription() {
		return "<b>Selected snippet</b> is displayed as a shell Selected snippet is displayed as a shell"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Snippet List Form"; //$NON-NLS-1$
	}

	
	protected AbstractUIElement<?> createContent() {
		DisposeBindingListener.DEBUG = true;
		final Binding objectBinding = new Binding(this) {

			AbstractSnippet old;

			protected void commit(Object value) {
				if (value == null) {
					return;
				}
				if (value == this.old) {
					this.old.activate();
					return;

				}
				if (this.old != null) {
					this.old.close();
				}
				final AbstractSnippet sn = (AbstractSnippet) value;
				this.old = sn;
				sn.run();
			}
		};
		objectBinding.setRealm(new OrderedRealm<AbstractSnippet>(this.all));
		objectBinding.setMaxCardinality(1);
		final AbstractEnumeratedValueSelector<AbstractSnippet> cl = this.createSelector(objectBinding);

		cl.addElementListener(new ElementListenerAdapter() {

			
			public void elementDisposed(IUIElement<?> element) {
				objectBinding.dispose();
				JavaObjectManager.printState();
				System.exit(0);
			}

		});
		return cl;
	}

	
	public String getGroup() {
		return "JAVA"; //$NON-NLS-1$
	}
}