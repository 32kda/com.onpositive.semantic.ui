package com.onpositive.commons.namespace.ide.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.onpositive.commons.namespace.ide.core.docgen.DocumentationGenerator;
import com.onpositive.semantic.language.model.NameSpaceContributionModel;
import com.onpositive.semantic.model.ui.actions.IBindedActionDelegate;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;

public class GenerateDocumentationActionDelegate implements	IBindedActionDelegate {

	
	public void run(IPropertyEditor<?> bindind) {
		final NameSpaceContributionModel object = (NameSpaceContributionModel) bindind
				.getBinding().getObject();
		final ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				Display.getCurrent().getActiveShell(), ResourcesPlugin
						.getWorkspace().getRoot(), true,
				"Please select folder for documentation");
		if (dialog.open() == Window.OK) {
			DocumentationGenerator.generate((IContainer) ResourcesPlugin
					.getWorkspace().getRoot().findMember(
							(IPath) dialog.getResult()[0]), object);
		}
	}

	public boolean isEnabled(Object value) {
		return true;
	}

}
