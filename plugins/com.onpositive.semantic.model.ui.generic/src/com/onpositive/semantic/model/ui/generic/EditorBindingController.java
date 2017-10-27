package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingSetListener;
import com.onpositive.semantic.model.expressions.impl.ExpressionParserV2;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class EditorBindingController extends ElementListenerAdapter {

	private final String path;
	boolean isExpression = false;
	private final IBindable editor;
	private IPropertyEditor<?> owner;
	private IBindingSetListener ls;
	private ExpressionBinding expressionBinding;
	private Binding binding;

	public EditorBindingController(IBindable editor, String path) {
		super();
		this.editor = editor;

		for (int a = 0; a < path.length(); a++) {
			char c = path.charAt(a);
			if (c != '.' && !Character.isJavaIdentifierPart(c)) {
				isExpression = true;
				break;
			}
		}
		if (isExpression)
			this.path = "" + '{' + path + '}';
		else
			this.path = path;
	}

	public void hierarchyChanged(IUIElement<?> element) {
		IUIElement<?> parent = element;
		if (parent == this.editor&&parent.getParent()!=null) {
			parent = parent.getParent();
		}
		final IBinding binding2 = this.getBinding();
		if (parent != null) {
			final IPropertyEditor<?> service = parent
					.getService(IPropertyEditor.class);
			if (service == null && binding2 != null
					&& parent.getParent() == null) {
				return;
			}
			this.doChange(binding2, service);
		} else {
			if (binding2 != null) {
				this.install(null);
			}
		}
	}

	public void dispose() {
		if (expressionBinding != null) {
			expressionBinding.dispose();
		}
		if (owner != null) {
			this.owner.removeBindingSetListener(this.ls);
		}
	}

	protected IBinding getBinding() {
		return this.editor.getBinding();
	}

	private void doChange(IBinding binding2, IPropertyEditor<?> service) {

		if (service != null) {
			if ((this.owner != null) && (this.owner != service)) {
				this.owner.removeBindingSetListener(this.ls);
			}
			if (service != this.owner) {
				IBindingSetListener listener = new IBindingSetListener() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void bindingChanged(IBindable element,
							IBinding newBinding, IBinding oldBinding) {
						EditorBindingController.this.doChange(
								EditorBindingController.this.getBinding(),
								EditorBindingController.this.owner);
					}

				};
				service.addBindingSetListener(listener);
				ls = listener;
				this.owner = service;
			}
			IBinding rb = service.getBinding();
			while (rb == null && service instanceof IUIElement) {
				service = (IPropertyEditor<?>) ((IUIElement<?>) service).getParent();
				if (service instanceof IBindable)
					rb = service.getBinding();
			}

			if (rb != null) {

				final IBinding binding = getBinding(rb);

				if (binding2 != binding) {
					this.install(binding);
				}
			} else {
				if (binding2 != null) {
					this.install(null);
				}
			}
		} else {
			if (binding2 != null) {
				this.install(null);
			}
		}
	}
	protected IBinding oldParent;

	private IBinding getBinding(final IBinding rb) {
		if (isExpression) {
			ExpressionParserV2 expressionParserV2 = new ExpressionParserV2();
			final IListenableExpression<?> parse = expressionParserV2.parse(
					path, rb, null);
			if (parse == null) {
				System.err.print(expressionParserV2.getCompleteErrorMessage());
				return null;
			}
			if (binding != null) {
				binding.dispose();
			}
			
			if (oldParent==rb&&expressionBinding!=null){
				return expressionBinding;
			}
			if (expressionBinding != null) {
				expressionBinding.disposeExpression();
				if (expressionBinding.getParent()!=null){
					expressionBinding.dispose();
				}
			}
			oldParent=rb;
			// binding = new Binding(parse);
			expressionBinding = new ExpressionBinding(parse);
			if (rb instanceof AbstractBinding){
			expressionBinding.setParent((AbstractBinding) rb);
			}
			

			expressionBinding.setName(path);
			//expressionBinding.setReadOnly(true);
			return expressionBinding;
		}
		return rb.binding(this.path);
	}

	protected void install(IBinding binding) {
		this.editor.setBinding(binding);
	}
}
