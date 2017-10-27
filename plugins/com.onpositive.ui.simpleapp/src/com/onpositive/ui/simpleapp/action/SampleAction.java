package com.onpositive.ui.simpleapp.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class SampleAction implements IViewActionDelegate {

	@Override
	public void run(IAction action) {
		MessageDialog.openInformation(Display.getDefault().getActiveShell(),"Information","Dialog");
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub

 	}

}
