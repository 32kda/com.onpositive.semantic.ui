package com.onpositive.semantic.model.ui.property.editors;

import java.util.Collection;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.ISetDelta;


public class ColorSelectorWrapper extends AbstractEditor<Control>
{
	protected RGB value;
	protected ColorSelector instance = null;
	
	public void setValue(Object value) 
	{
		if (value instanceof RGB && instance != null)
			instance.setColorValue((RGB) value);
	}
	
	
	
	
	
	
	protected void internalSetBinding(IBinding binding)
	{
		this.setValue(binding.getValue());		
	}

	
	protected void processValueChange(ISetDelta<?> valueElements)
	{
		final Collection<?> addedElements = valueElements.getAddedElements();
		if (!addedElements.isEmpty()) 
		{
			final Object nextAdded = addedElements.iterator().next();
			if (nextAdded instanceof RGB) instance.setColorValue((RGB) nextAdded);
		}
		
	}
	
	public boolean needsLabel() {
		return false;
	}

	
	protected Control createControl(Composite conComposite)
	{
		instance = new ColorSelector(conComposite);
		instance.addListener(new IPropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent event)
			{
				Object color = event.getNewValue();
				if (color instanceof RGB) commitToBinding((RGB) color);				
			}
			
		});
		
		IBinding binding = getBinding();
		if (binding != null) {
			Object value=binding.getValue();
			if (value == null) {
				value = new RGB(0,0,0);
			}
			setIgnoreChanges(true);
			try 
			{
				if (value instanceof RGB) 
					instance.setColorValue((RGB) value);
			} finally {
				setIgnoreChanges(false);
			}
		}		
		return instance.getButton();
	}

}
