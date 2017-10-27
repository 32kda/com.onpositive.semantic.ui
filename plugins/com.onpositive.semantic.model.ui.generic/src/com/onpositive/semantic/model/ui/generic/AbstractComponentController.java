package com.onpositive.semantic.model.ui.generic;

import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingSetListener;
import com.onpositive.semantic.model.expressions.impl.BinaryExpression;
import com.onpositive.semantic.model.expressions.impl.ExpressionParserV2;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public abstract class AbstractComponentController extends
		ElementListenerAdapter {

	protected final String expressionPath;
	protected final IUIElement<?> editor;
	private IBindable owner;
	private IListenableExpression<?> expression;
	private IBindingSetListener bindingSetListener;

	public AbstractComponentController(String expressionPath,
			IUIElement<?> editor) {
		super();
		this.expressionPath = expressionPath;
		this.editor = editor;
	}

	public void dispose() {
		if (expression != null) {
			expression.disposeExpression();
		}
		if (this.owner != null) {
			this.owner.removeBindingSetListener(this.bindingSetListener);
		}
	}

	protected abstract void setValue(Object newValue);

	
	public void hierarchyChanged(IUIElement<?> element) {
		if (expression != null) {
			expression.disposeExpression();
			expression=null;
		}
		IUIElement<?> parent = element;
		parent = doRaiseUp(parent);
		if (parent != null) {
			IBindable service2 = (IBindable) parent.getService(IBindable.class);
			final IBindable service = service2;
			this.doChange(expression, service);
		} else {
			if (expression != null) {
				this.install(expression);
			}
		}
	}

	protected IUIElement<?> doRaiseUp(IUIElement<?> parent) {
		if (parent == this.editor) {
			parent = parent.getParent();
		}
		return parent;
	}

	
	public void elementDisposed(IUIElement<?> element) {		
		this.dispose();
	}

	
	public void bindingChanged(IUIElement<?> element, IBinding newBinding,
			IBinding oldBinding) {
		doChange(expression, owner);
	}

	private void doChange(IListenableExpression<?> binding2, IBindable service) {

		if (service != null) {
			if ((this.owner != null) && (this.owner != service)) {
				this.owner.removeBindingSetListener(this.bindingSetListener);
			}
			if (service != this.owner) {
				IBindingSetListener newBindingSetListener = new IBindingSetListener() {

					public void bindingChanged(IBindable element,
							IBinding newBinding, IBinding oldBinding) {
						doChange(expression, owner);
					}

				};
				service.addBindingSetListener(newBindingSetListener);
				bindingSetListener = newBindingSetListener;
				this.owner = service;
			}
			final IBinding rb = getBinding(service);

			if (rb != null) {
				
				ExpressionParserV2 expressionParserV2 = new ExpressionParserV2();
				final IListenableExpression<?> newExpr = expressionParserV2
						.parse(expressionPath, rb, rb.getClassResolver());//FIXME
				
				if (newExpr == null) {
					System.err.print(expressionParserV2
							.getCompleteErrorMessage());
					return;
				}
				this.install(newExpr);

			} else {
				if (binding2 != null) {
					this.install(null);
				}
			}
		} else {
			if (binding2 != null) {
				this.install(binding2);
			}
		}
	}

	protected IBinding getBinding(IBindable service) {
		return service.getBinding();
	}
	
	boolean ft=true;
	IValueListener<Object> exp = new IValueListener<Object>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void valueChanged(Object oldValue, Object newValue) {
			setValue1(newValue);

		};
	};
	
	protected void install(IListenableExpression<?> newExpr) {
		if (expression != null) {
			expression.removeValueListener(exp);
			expression.disposeExpression();		
			expression=null;
		}
		
		this.expression = newExpr;
		if (newExpr != null) {
			this.expression.addValueListener(exp);
			Object newValue = this.expression.getValue();
			if (newValue != null||ft){
				ft=false;
				setValue1(newValue);
			}
		}
	}

	private void setValue1(final Object newValue) {
		editor.executeOnUiThread(new Runnable(){

			@Override
			public void run() {
				setValue(newValue);
			}
			
		});
	}

}