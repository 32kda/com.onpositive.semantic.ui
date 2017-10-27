package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public interface IUseLabelsForNull {

	@HandlesAttributeDirectly("useLabelsForNull")
	public void setUseLabelProviderForNull(boolean useLabelProviderForNull);

}