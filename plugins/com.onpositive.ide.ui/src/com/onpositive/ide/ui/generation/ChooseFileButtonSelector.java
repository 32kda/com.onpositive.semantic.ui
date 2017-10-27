package com.onpositive.ide.ui.generation;

import org.eclipse.ui.dialogs.SaveAsDialog;

import com.onpositive.semantic.model.api.factory.AbstractFactory;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.property.IFunction;


public class ChooseFileButtonSelector extends AbstractFactory
{
	public ChooseFileButtonSelector() {
		super("Select file...","Select output file:");
	}

	public Object getValue(Object context)
	{	
		SaveAsDialog dialog = new SaveAsDialog(null);
		
		dialog.open();
		return dialog.getResult().toString();
	}


}
