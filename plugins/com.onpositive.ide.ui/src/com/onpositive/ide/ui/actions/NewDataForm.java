package com.onpositive.ide.ui.actions;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class NewDataForm implements IObjectActionDelegate{

	private ISelection selection;

	public NewDataForm() {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (targetPart==null){
			selection=null;
		}
	}

	public void run(IAction action) {
		IStructuredSelection sel=(IStructuredSelection) selection;
		IType type=(IType) sel.getFirstElement();
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection=selection;
	}

}
