package com.onpositive.commons.namespace.ide.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.onpositive.semantic.language.model.NamespaceModel;
import com.onpositive.semantic.language.model.NamespacesModel;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.ui.workbench.elements.XMLView;

public class NamespacesViewPart extends XMLView {

	public static class NameSpaceRealmProvider implements
			IRealmProvider<NamespaceModel> {

		public IRealm<NamespaceModel> getRealm(IHasMeta model,Object p,Object o) {
			final NamespacesModel instance = NamespacesModel.getInstance();
			return instance.getModels();
		}
	}

	private NamespaceModel selection;

	public NamespacesViewPart() {
		super("namespacesView.xml");

	}

	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		this.getSite().getPage().addSelectionListener(new ISelectionListener() {

			
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				System.out.println(selection);
			}

		});
	}

	
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

	@RealmProvider(value = NameSpaceRealmProvider.class)
	public NamespaceModel getSelection() {
		return this.selection;
	}

	public void setSelection(NamespaceModel selection) {
		this.selection = selection;
	}

}
