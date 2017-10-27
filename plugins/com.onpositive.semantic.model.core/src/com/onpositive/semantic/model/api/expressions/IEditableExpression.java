package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.meta.IHasMeta;




public interface IEditableExpression<T> extends IListenableExpression<T>,IHasMeta {
	
	public void setValue( Object value );
	
	public boolean isReadOnly();
		
}
