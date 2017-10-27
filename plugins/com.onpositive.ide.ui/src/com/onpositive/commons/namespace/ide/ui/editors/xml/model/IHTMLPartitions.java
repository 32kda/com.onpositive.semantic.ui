package com.onpositive.commons.namespace.ide.ui.editors.xml.model;


/**
 * This interface is not intended to be implemented. It defines the partition
 * types for HTML. Clients should reference the partition type Strings defined
 * here directly.
 * 
 * @since 1.1
 */
public interface IHTMLPartitions {

	String HTML_DEFAULT = "org.eclipse.wst.html.HTML_DEFAULT"; //$NON-NLS-1$
	String HTML_DECLARATION = "org.eclipse.wst.html.HTML_DECLARATION"; //$NON-NLS-1$
	String HTML_COMMENT = "org.eclipse.wst.html.HTML_COMMENT"; //$NON-NLS-1$

	String SCRIPT = "org.eclipse.wst.html.SCRIPT"; //$NON-NLS-1$
	String SCRIPT_EVENTHANDLER = SCRIPT + ".EVENTHANDLER"; //$NON-NLS-1$
	
	/**
	 * @deprecated this partition type is not used locally any longer
	 */
	String STYLE = "org.eclipse.wst.html.STYLE"; //$NON-NLS-1$

	// ISSUE: I think meta tag areas are here too?
}
