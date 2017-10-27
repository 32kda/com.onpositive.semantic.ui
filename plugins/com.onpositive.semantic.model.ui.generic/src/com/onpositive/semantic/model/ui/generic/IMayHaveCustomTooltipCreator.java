package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.ui.roles.IInformationalControlContentProducer;

public interface IMayHaveCustomTooltipCreator<T> {

	public abstract void setTooltipInformationControlCreator(
			IInformationalControlContentProducer informationalControlContentProducer);

}