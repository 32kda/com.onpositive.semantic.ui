/**
 * 
 */
package com.onpositive.semantic.ui.snippets;

import com.onpositive.semantic.model.api.property.adapters.TextProviderAdapter;

public class YesNoProvider extends TextProviderAdapter {

	
	public String getText(Object object) {
		final Boolean bs = (Boolean) object;
		if (bs == null) {
			return "Unknown"; //$NON-NLS-1$
		}
		return bs ? "Yes" : "No"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
