package com.onpositive.ide.ui.generation;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.status.IStatusChangeListener;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.ValidatorAdapter;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.roles.WidgetObject;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;


public class CustomEditorWrapper extends AbstractUIElement<Control>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9002069231368194858L;
	protected Iterable<Object> currentList;
	protected Composite composite;
	protected AbstractBinding globalBinding;
	protected ArrayList<AbstractBinding> childBindingList;
	protected BindingStatusChangeListener listener = new BindingStatusChangeListener();
	
	
	
	public boolean needsLabel()
	{
		return false;
	}

	protected class BindingStatusChangeListener implements IStatusChangeListener
	{		

		/**
		 * 
		 */
		private static final long serialVersionUID = 3021186033344668695L;

		public void statusChanged(IHasStatus bnd, CodeAndMessage cm)
		{					
			globalBinding.setupStatus(validateStatus());	
		}
		
	}
	
	
	protected void internalSetBinding(IBinding binding)
	{
		if (binding instanceof AbstractBinding) globalBinding = (AbstractBinding) binding;
		setValue(binding, binding.getValue());
	}

	
	protected void processValueChange(ISetDelta<?> valueElements)
	{
		// TODO Auto-generated method stub

	}

	
	protected Control createControl(Composite conComposite)
	{
		composite = new ScrolledComposite(conComposite,SWT.V_SCROLL);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		RootElement rootElement = new RootElement(composite);
		rootElement.setBinding(globalBinding);
		rootElement.setLayoutManager(new OneElementOnLineLayouter());
		childBindingList = new ArrayList<AbstractBinding>();
		for (Object element : currentList)
		{		
			try
			{
				rootElement.add(createGUILineForElement(composite,element));
			}
			catch (Exception e) 
			{
				Activator.log(e);
			}
		}

		globalBinding.addValidator(new ValidatorAdapter<Object>(){

			private static final long serialVersionUID = 7626678824757858857L;

			public CodeAndMessage isValid(IValidationContext context, Object object)
			{
				return validateStatus();
			}
			
			
		});
		Point computeSize = rootElement.getContentParent().computeSize(-1,-1);
		((ScrolledComposite)composite).setContent(rootElement.getContentParent());
		((ScrolledComposite)composite).setMinHeight(computeSize.y);
		((ScrolledComposite)composite).setExpandVertical(true);
		((ScrolledComposite)composite).setExpandHorizontal(true);
		return composite;
	}
	
	protected AbstractUIElement<?> createGUILineForElement(Composite parentComposite,
			Object element) throws Exception
	{		
		WidgetObject widgetObject = WidgetRegistry.getInstance().getWidgetObject(element,null,null);
		final Binding context = new Binding(element);
		final AbstractUIElement<?> evaluateLocalPluginResource = (AbstractUIElement<?>) DOMEvaluator
				.getInstance().evaluateLocalPluginResource(
						CustomEditorWrapper.class,
						widgetObject.getResource(), context); //$NON-NLS-1$
		DisposeBindingListener.linkBindingLifeCycle(context,
				(IUIElement<?>) evaluateLocalPluginResource);
		childBindingList.add(context);
		context.addStatusChangeListener(listener);
		return evaluateLocalPluginResource;
	}
	
	protected CodeAndMessage validateStatus()
	{
		CodeAndMessage status = CodeAndMessage.OK_MESSAGE;
		for (Iterator<AbstractBinding> iterator = childBindingList.iterator(); iterator.hasNext();)
		{
			AbstractBinding binding = (AbstractBinding) iterator.next();
			CodeAndMessage curStatus = binding.getStatus();
			if (curStatus.getCode() > status.getCode()) status = curStatus; 
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	protected void setValue(IBinding binding, Object value) 
	{
		if (value instanceof Iterable)
		{
			currentList = (Iterable<Object>) value;
		}	
		else 
		{
			currentList = new ArrayList<Object>();
			((ArrayList<Object>)currentList).add(value);
		}
	}

}
