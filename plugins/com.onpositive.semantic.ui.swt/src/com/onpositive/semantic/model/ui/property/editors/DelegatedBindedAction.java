package com.onpositive.semantic.model.ui.property.editors;

import org.eclipse.swt.widgets.Control;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.BindedAction;

public class DelegatedBindedAction extends BindedAction
{
	protected class ChangeListener implements IBindingChangeListener
	{
		private IBinding bnd;

		protected ChangeListener(IBinding bnd)
		{
			this.bnd = bnd;
		}

		public void changed()
		{
			setEnabled(getDelegate().isEnabled(bnd.getValue()));
		}

		public void enablementChanged(boolean isEnabled)
		{
			
		}

		public void valueChanged(ISetDelta valueElements)
		{
			setEnabled(getDelegate().isEnabled(valueElements));			
		}
		
		public void setBinding(IBinding bnd)
		{
			this.bnd = bnd;
		}
	}

	protected IBindedActionDelegate delegate;
	protected ClassLoader classLoader;
	protected String className;
	protected AbstractUIElement<Control> parentContext;
	protected ChangeListener changeListener; 

	public DelegatedBindedAction(int style, String className, ClassLoader classLoader, final AbstractUIElement<Control> parentContext)
	{
		super(style);
		this.className = className;
		this.classLoader = classLoader;
		this.parentContext = parentContext;
		if (parentContext instanceof AbstractEditor)
		{
			IBinding controlBinding = ((AbstractEditor) parentContext).getBinding();
			changeListener = new ChangeListener(controlBinding);
			controlBinding.addBindingChangeListener(changeListener);
		}
	}
	
	public void run() {
		delegate = getDelegate();
		AbstractUIElement<?> control = (AbstractUIElement<Control>) parentContext;
		while (!(control instanceof IPropertyEditor<?>)){
			control=(AbstractUIElement<?>)control.getParent();
		}
		this.delegate.run(((IPropertyEditor<?>) control));
	}

	protected IBindedActionDelegate getDelegate()
	{
		if (delegate == null) {
			try {
				delegate = (IBindedActionDelegate) classLoader
						.loadClass(className).newInstance();
			} catch (final InstantiationException e) {
				Activator.log(e);
			} catch (final IllegalAccessException e) {
				Activator.log(e);
			} catch (final ClassNotFoundException e) {
				Activator.log(e);
			}
		}
		return delegate;
	}
	
	
}
