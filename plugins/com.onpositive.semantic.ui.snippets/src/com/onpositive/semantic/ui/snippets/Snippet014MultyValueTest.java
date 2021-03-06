package com.onpositive.semantic.ui.snippets;

import java.util.ArrayList;

import org.eclipse.swt.layout.GridData;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.semantic.model.api.property.adapters.RealmProviderAdapter;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;

public class Snippet014MultyValueTest extends AbstractSnippet {

	@RealmProvider(BigRealm.class)
	ArrayList<Integer> m = new ArrayList<Integer>();

	public static class BigRealm extends RealmProviderAdapter<Integer> {

		public IRealm<Integer> getRealm(IBinding model) {
			final ArrayList<Integer> rs = new ArrayList<Integer>();
			for (int a = 0; a < 10000; a++) {
				rs.add(a);
			}
			return new Realm<Integer>(rs);
		}

	}

	public Snippet014MultyValueTest() {
		for (int a = 0; a < 100; a++) {
			this.m.add((a) * 5);
		}
	}

	
	protected AbstractUIElement<?> createContent() {
		final Binding bs = new Binding(this);
		final ListEnumeratedValueSelector<Integer> aa = new ListEnumeratedValueSelector<Integer>(
				bs.getBinding("m")); //$NON-NLS-1$
		aa.setAsCheckBox(true);
		aa.setElementRole("formula"); //$NON-NLS-1$
		aa.setCaption(""); //$NON-NLS-1$
		aa.setLayoutData(new GridData(400, 300));
		DisposeBindingListener.linkBindingLifeCycle(bs, aa);
		return aa;
	}

	
	protected String getDescription() {
		return "Tests how selection works"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Selection and value test in big viewer"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}

}
