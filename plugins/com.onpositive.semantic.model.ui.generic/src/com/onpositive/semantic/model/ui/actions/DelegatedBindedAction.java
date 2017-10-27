package com.onpositive.semantic.model.ui.actions;


import com.onpositive.commons.xml.language.Activator;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.binding.IBindingSetListener;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.BindedAction;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;

public class DelegatedBindedAction extends BindedAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	protected class ChangeListener implements IBindingChangeListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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
	protected IUIElement<?> parentContext;
	protected ChangeListener changeListener; 

	public DelegatedBindedAction(int style, String className, ClassLoader classLoader, final IUIElement<?> parentContext)
	{
		super(style);
		setBinding(new Binding(""));
		this.className = className;
		this.classLoader = classLoader;
		this.parentContext = parentContext;
		if (parentContext instanceof IBindable)
		{
			final IBindable iBindable = (IBindable) parentContext;
			final IBinding controlBinding = iBindable.getBinding();
			if (controlBinding==null){
				iBindable.addBindingSetListener(new IBindingSetListener() {
					
					@Override
					public void bindingChanged(IBindable element, IBinding newBinding,
							IBinding oldBinding) {
						if (changeListener!=null&&oldBinding!=null){
							oldBinding.removeBindingChangeListener(changeListener);
						}
						// TODO Auto-generated method stub
						IBinding controlBinding = iBindable.getBinding();
						changeListener = new ChangeListener(controlBinding);
						controlBinding.addBindingChangeListener(changeListener);			
					}
				});
			}
			
		}
	}
	
	public void run() {
		delegate = getDelegate();
		IUIElement<?> control = (IUIElement<?>) parentContext;
		while (!(control instanceof IBindable)){
			control=(IUIElement<?>)control.getParent();
		}
		this.delegate.run(((IPropertyEditor<?>) control));
	}
	
	@Override
	public void setImageId(String imageId)
	{
		this.imageId = imageId;
		if ((imageId != null) && (imageId.length() > 0)) {
			ImageDescriptor imageDescriptor2 = ImageManager.getImageDescriptorByPath(getDelegate(), imageId);
			this.setImageDescriptor(imageDescriptor2);
		} else
			this.setImageDescriptor(null);
	}

	
	@Override
	public String getImageId() {
		return super.getImageId();
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return super.getImageDescriptor();
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
