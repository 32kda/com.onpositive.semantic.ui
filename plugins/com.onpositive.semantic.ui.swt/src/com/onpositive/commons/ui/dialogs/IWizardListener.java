package com.onpositive.commons.ui.dialogs;

public interface IWizardListener {

	public boolean performFinish(BindedWizard wizard);
	public boolean performCancel(BindedWizard wizard);
	public boolean preFinish(BindedWizard bindedWizard);
}
