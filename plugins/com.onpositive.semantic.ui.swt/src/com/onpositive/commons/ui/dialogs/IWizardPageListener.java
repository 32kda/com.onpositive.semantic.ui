package com.onpositive.commons.ui.dialogs;

public interface IWizardPageListener {

	public boolean beforeNext(BindedWizardPage page);
	public boolean beforeBack(BindedWizardPage page);
}
