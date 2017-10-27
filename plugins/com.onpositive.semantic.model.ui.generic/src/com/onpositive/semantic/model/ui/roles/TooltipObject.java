package com.onpositive.semantic.model.ui.roles;

import com.onpositive.core.runtime.IConfigurationElement;

public class TooltipObject extends ContentProducerObject {

	public TooltipObject(IConfigurationElement element) {
		super(element);
	}

	public boolean useNativeTooltip() {
		return this.getBooleanAttribute("useNative", false); //$NON-NLS-1$
	}

	public int getTooltipShowDelay() {
		return this.getIntegerAttribute("tooltipShowDelay", 500); //$NON-NLS-1$
	}

	public int getTooltipVisibilityTime() {
		return this.getIntegerAttribute("tooltipVisibilityTime", 6000); //$NON-NLS-1$
	}
}
