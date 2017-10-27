package com.onpositive.commons.ui.dialogs;

import org.eclipse.jface.wizard.IWizardPage;


public interface INextPageProvider {

	IWizardPage getNextPage(IWizardPage cureIWizardPage);	
}
