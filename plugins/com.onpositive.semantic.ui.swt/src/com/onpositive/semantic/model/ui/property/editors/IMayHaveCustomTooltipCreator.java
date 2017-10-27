package com.onpositive.semantic.model.ui.property.editors;

import com.onpositive.semantic.common.ui.roles.IInformationalControlContentProducer;

public interface IMayHaveCustomTooltipCreator<T> {

	public abstract void setTooltipInformationControlCreator(
			IInformationalControlContentProducer informationalControlContentProducer);

}