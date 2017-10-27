package com.onpositive.ui.help;

import org.eclipse.swt.widgets.Control;

public interface IHelpSystem {

	void displayHelp(String contextId);

	void setHelp(Control createDialogArea, String helpContext);

}
