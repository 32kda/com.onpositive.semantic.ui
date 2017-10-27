package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public interface IDecoratableEditor<T> extends IUIElement<T>{

	public boolean isShowHoverOnError();

	@HandlesAttributeDirectly("showHoverOnError")
	public void setShowHoverOnError(boolean showHoverOnError);
	
	public boolean isInstallRequiredDecoration();

	@HandlesAttributeDirectly("installRequiredDecoration")
	public void setInstallRequiredDecoration(boolean installRequiredDecoration);
}
