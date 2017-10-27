package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.IEditableExpression;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.binding.Binding;

public final class ExpressionBinding extends Binding {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final IListenableExpression<?> parse;

	IValueListener<Object> exp = new IValueListener<Object>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void valueChanged(Object oldValue, Object newValue) {
			ignore=true;
			try{
			if (parse instanceof IEditableExpression){
				IEditableExpression<?>m=(IEditableExpression<?>) parse;
				setReadOnly(m.isReadOnly());
				//TODO REVIW ME
			}
			setValue(parse.getValue(), null);
			}finally{
				ignore=false;
			}
		}
	};
	
	boolean ignore;
	public ExpressionBinding(IListenableExpression<?> object) {
		super(object.getValue());
		this.parse = object;

		parse.addValueListener(exp);
	}
	@Override
	public void dispose() {
		parse.removeValueListener(exp);
		super.dispose();
	}

	protected void commit(Object value) {
		if (ignore){
			return;
		}
		if (parse instanceof IEditableExpression) {
			IEditableExpression e = (IEditableExpression) parse;
			e.setValue(value);
		}
	}
	
	
	public Class<?> getSubjectClass() {
		if (parse instanceof IEditableExpression){
			IEditableExpression<?>m=(IEditableExpression<?>) parse;
			return DefaultMetaKeys.getSubjectClass(m);
		}
		// TODO Auto-generated method stub
		return Object.class;
	}

	public boolean isReadOnly() {
		if (parse instanceof IEditableExpression) {
			if (DefaultMetaKeys.isReadonly(this)){
				return true;
			}
			IEditableExpression ed = (IEditableExpression) parse;
			return ed.isReadOnly();
		}
		return true;
	}
}