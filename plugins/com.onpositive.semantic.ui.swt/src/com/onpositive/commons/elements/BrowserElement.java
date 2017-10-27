package com.onpositive.commons.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class BrowserElement extends AbstractUIElement<Browser> {

	
	protected Browser createControl(Composite conComposite) {
		return new Browser(conComposite, SWT.NONE);
	}
	

}
