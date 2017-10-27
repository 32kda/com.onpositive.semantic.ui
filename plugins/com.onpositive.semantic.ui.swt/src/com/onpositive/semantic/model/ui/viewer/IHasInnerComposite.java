package com.onpositive.semantic.model.ui.viewer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Superinteface for widget wrappers, which has iner composite
 * @author kor
 *
 */
public interface IHasInnerComposite {

	/**
	 * Returns inner composite for this UI element
	 * @return inner composite for this UI element
	 */
	Composite getComposite();

	/**
	 * Returns main information control for this UI element
	 * @return main information control for this UI element, or <code>null</code> if none
	 */
	Control getMainControl();
}
