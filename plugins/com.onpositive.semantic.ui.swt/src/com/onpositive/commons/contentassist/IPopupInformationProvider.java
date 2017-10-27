package com.onpositive.commons.contentassist;

import org.eclipse.swt.widgets.Composite;

public interface IPopupInformationProvider {

	public void create(Composite parent);

	public void setContent(Object element, String description, String role,
			String theme);
}
