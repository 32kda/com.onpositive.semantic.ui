package com.onpositive.businessdroids.ui.actions;

import java.beans.PropertyChangeListener;

public interface IContributionItem extends IHasImage {

	/**
	 * @return Whether this item is enabled atm
	 */
	public boolean isEnabled();

	/**
	 * @return Item identifying human-readable text
	 */
	public String getText();

	/**
	 * @return Item identifier Usually should be equal to
	 *         {@link IContributionItem#getText()}, but in some cases can
	 *         differ, e.g. for some logical contribution group creating,
	 *         sorting etc.
	 */
	public String getId();

	void addPropertyChangeListener(PropertyChangeListener l);

	void removePropertyChangeListener(PropertyChangeListener l);
}
