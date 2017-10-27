package com.onpositive.semantic.ui.snippets;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;


public class TestAction extends Action
{
	
	public TestAction()
	{
		setText("Info");
		setToolTipText("Info");
		setImageDescriptor(ImageDescriptor.createFromFile(TestAction.class,"releng_gears.gif"));
	}

	
	public void run() 
	{
		MessageDialog.openInformation(null,"Info!", "Piece of ino should be here.");
	}
	
}
