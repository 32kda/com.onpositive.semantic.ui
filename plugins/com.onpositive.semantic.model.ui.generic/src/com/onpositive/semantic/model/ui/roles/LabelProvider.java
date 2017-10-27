package com.onpositive.semantic.model.ui.roles;

import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.meta.IHasMeta;

public final class LabelProvider implements ITextLabelProvider {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getDescription(Object object) {
		return LabelAccess.getDescription(null, null, object);
	}

	public String getText(IHasMeta meta, Object parent, Object object) {
		return LabelAccess.getLabel(meta, parent, object);
	}				
}