package com.onpositive.semantic.model.api.labels;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

public class ExpressionBasedLabelProvider implements ITextLabelProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private String description;

	public ExpressionBasedLabelProvider(String message, String description) {
		super();
		this.message = message;
		this.description = description;
	}
	public ExpressionBasedLabelProvider(String message) {
		super();
		this.message = message;
		this.description = message;
	}

	
	@Override
	public String getText(IHasMeta meta, Object parent, Object object) {
		return ExpressionAccess.calculateAsString(message, meta, parent, object);
	}

	
	@Override
	public String getDescription(Object object) {
		return ExpressionAccess.calculateAsString(description, MetaAccess.getMeta(object), null, object);
	}

}
