package com.onpositive.ide.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewDataFormWizard extends Wizard implements INewWizard {

	public NewDataFormWizard() {
	}

	
	public boolean performFinish() {
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	
	}

}
