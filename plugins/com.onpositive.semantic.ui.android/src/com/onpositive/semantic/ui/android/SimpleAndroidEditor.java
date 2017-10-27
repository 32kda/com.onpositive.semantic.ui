package com.onpositive.semantic.ui.android;

import android.view.View;

import com.onpositive.commons.xml.language.HandlesParent;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.ICommitListener;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;

public abstract class SimpleAndroidEditor extends AndroidUIElement  implements
IPropertyEditor<BasicUIElement<View>> {

	/**
	 * Serial Version UID 
	 */
	private static final long serialVersionUID = 1L;
	protected ICommitListener statusCommitListener = new ICommitListener() {
		
		private static final long serialVersionUID = -7367734697137496726L;

		@Override
		public void commitPerformed(ICommand command) {
			IBinding curBinding = getBinding();
			if (curBinding != null)
				onStatus(curBinding.getStatus());
		}
	};
	
	
	public SimpleAndroidEditor() {
	}
	
	protected void onCreate(
			com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIComposite<?> parent) {
		super.onCreate(parent);
		resetValue();
	};

	public String getCaption() {
		String caption = super.getCaption();
		if (caption == null || caption.length() == 0) {
			IBinding binding2 = getBinding();
			if (binding2 != null) {
				return binding2.getName();
			}
			return "Unknown";
		}
		return caption;
	};
	
	@Override
	@HandlesParent
	public void setBinding(IBinding binding) {
		if (getBinding() != null)
			getBinding().removeCommitListener(statusCommitListener);
		super.setBinding(binding);
		if (binding != null) {
			onStatus(binding.getStatus());
			binding.addCommitListener(statusCommitListener);
		}
	}
	
	protected void status(CodeAndMessage cm) {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void dispose() {
		((ICompositeElement) getParent()).remove(this);
		super.dispose();
	}

	

}
