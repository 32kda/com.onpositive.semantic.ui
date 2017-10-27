/**
 * 
 */
package com.onpositive.commons.ui.dialogs;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public final class NextValidatingWizardDialog extends WizardDialog {
	public NextValidatingWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}
	public boolean isNextPressed;
	
	
	protected void nextPressed() {
		isNextPressed=true;
		IWizardPage currentPage = getCurrentPage();
		if (currentPage instanceof INextValidatingPage){
			INextValidatingPage p=(INextValidatingPage) currentPage;
			if (!p.nextPressed()){
				return;
			}
		}
		try{
		super.nextPressed();
		}finally{
			isNextPressed=false;
		}
	}
}