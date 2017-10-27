package com.onpositive.semantic.model.ui.generic;

import java.io.Serializable;

/**
 * Classes that implement this interface will be notified when hyperlinks are
 * entered, exited and activated.
 * 
 * @see org.eclipse.ui.forms.widgets.Hyperlink
 * @see org.eclipse.ui.forms.widgets.ImageHyperlink
 * @see org.eclipse.ui.forms.widgets.FormText
 * @since 3.0
 */
public interface IHyperlinkListener extends Serializable{
	/**
	 * Sent when hyperlink is entered either by mouse entering the link client
	 * area, or keyboard focus switching to the hyperlink.
	 * 
	 * @param e
	 *            an event containing information about the hyperlink
	 */
	void linkEntered(HyperlinkEvent e);
	/**
	 * Sent when hyperlink is exited either by mouse exiting the link client
	 * area, or keyboard focus switching from the hyperlink.
	 * 
	 * @param e
	 *            an event containing information about the hyperlink
	 */
	void linkExited(HyperlinkEvent e);
	/**
	 * Sent when hyperlink is activated either by mouse click inside the link
	 * client area, or by pressing 'Enter' key while hyperlink has keyboard
	 * focus.
	 * 
	 * @param e
	 *            an event containing information about the hyperlink
	 */
	void linkActivated(HyperlinkEvent e);
}
