package com.onpositive.semantic.model.api.labels;

import com.onpositive.semantic.model.api.meta.IHasMeta;

public abstract class TextProviderAdapter implements ITextLabelProvider {

	@Override
	public String getDescription(Object object) {
		return null;
	}

	@Override
	public String getText(IHasMeta meta, Object parent, Object object) {
		return getText(object);
	}

	public  String getText(Object object) {
		return object==null?"":object.toString();
	}

}
