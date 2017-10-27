package com.onpositive.semantic.model.api.labels;

import com.onpositive.semantic.model.api.meta.IHasMeta;


public interface ITextLabelProvider extends ILabelProvider{

	public String getText(IHasMeta meta, Object parent, Object object);

	public String getDescription(Object object);

}
