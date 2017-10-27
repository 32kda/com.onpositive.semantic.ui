package com.onpositive.semantic.model.binding;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;


public class Controller {
	
	private final IListenableExpression<Object> inputExpression ;
	private final IBinding outputBinding ;
	
	IValueListener<Object> exp = new IValueListener<Object>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void valueChanged(Object oldValue, Object newValue) {
			outputBinding.setValue(newValue, null);
		}
	};
	
	@SuppressWarnings("unchecked")
	public Controller(IBinding bnd,IListenableExpression<?>input) {
		outputBinding=bnd;
		this.inputExpression=(IListenableExpression<Object>) input;
		inputExpression.addValueListener(exp);
		
	}

	public static void bind(Object obj,
			String string, IListenableExpression<?> expr) {
		new Controller(new Binding(obj).binding(string), expr);
	}
}
