package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;

public class EditableWrapper extends MesagingExpression implements IEditableExpression<Object>{

	public EditableWrapper(IListenableExpression<?> exp) {
		super(exp,"");
	}
	
	
	@Override
	public String getMessage() {
		return exp.getMessage();
	}

	
	@Override
	public IMeta getMeta() {
		BaseMeta baseMeta = new BaseMeta();
		baseMeta.putMeta(DefaultMetaKeys.READ_ONLY_KEY, true);
		return baseMeta;
	}

	
	@Override
	public void setValue(Object value) {
		
	}

	
	@Override
	public boolean isReadOnly() {
		return true;
	}

}
