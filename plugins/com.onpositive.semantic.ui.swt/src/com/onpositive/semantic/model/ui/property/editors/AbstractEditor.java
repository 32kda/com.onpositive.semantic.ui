package com.onpositive.semantic.model.ui.property.editors;

import java.util.HashSet;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.xml.language.HandlesAttributeIndirectly;
import com.onpositive.commons.xml.language.HandlesParent;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.ui.generic.EditorBindingController;
import com.onpositive.semantic.model.ui.generic.IBindingSetListener;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;

@SuppressWarnings("rawtypes")
public abstract class AbstractEditor<T extends Control> extends
		AbstractUIElement<T> implements IPropertyEditor<AbstractUIElement> {

	public AbstractUIElement<? extends Control> getUIElement() {
		return this;
	}

	private IBinding binding;
	private boolean shouldIngoreChanges;
	private boolean ignore;

	private final HashSet<IBindingSetListener> listeners = new HashSet<IBindingSetListener>();

	public void addBindingSetListener(IBindingSetListener ls) {
		this.listeners.add(ls);
	}

	public void removeBindingSetListener(IBindingSetListener ls) {
		this.listeners.remove(ls);
	}

	public AbstractEditor() {
	}

	public AbstractEditor(int style) {
		super(null, style);
	}

	public void recreate() {
		super.recreate();
		if (this.binding != null) {
			binding.addBindingChangeListener(this.listener);
		}
	}

	public void create() {
		try {
			this.shouldIngoreChanges = true;
			super.create();

		} finally {
			this.shouldIngoreChanges = false;
		}
	}

	public boolean shouldIgnoreChanges() {
		return this.shouldIngoreChanges || this.ignore;
	}

	private final IBindingChangeListener<Object> listener = new IBindingChangeListener<Object>() {

		public void valueChanged(final ISetDelta<Object> valueElements) {
			AbstractEditor.this.shouldIngoreChanges = true;
			try {
				if (Display.getCurrent() == null) {
					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							AbstractEditor.this
									.processValueChange(valueElements);
						}
					});
				} else {
					AbstractEditor.this.processValueChange(valueElements);
				}

			} finally {
				AbstractEditor.this.shouldIngoreChanges = false;
			}
		}

		public void enablementChanged(final boolean isEnabled) {
			if (AbstractEditor.this.isCreated()) {
				if (Display.getCurrent() == null) {
					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							AbstractEditor.this.setEnabled(isEnabled);
						}
					});
				} else {
					AbstractEditor.this.setEnabled(isEnabled);
				}
			} else {
				AbstractEditor.this.setEnabled(isEnabled);
			}
		}

		public void changed() {
			final IBinding binding2 = AbstractEditor.this.getBinding();
			AbstractEditor.this.binding = null;

			AbstractEditor.this.setBinding(binding2);
		}

	};

	public IBinding getBinding() {
		return this.binding;
	}

	protected abstract void processValueChange(ISetDelta<?> valueElements);

	@HandlesParent
	public final void setBinding(IBinding binding) {
		this.shouldIngoreChanges = true;
		final IBinding old = this.binding;
		if (old == binding) {
			return;
		}
		this.unhookBinding(old);
		if (this.binding != null) {
			this.binding.removeBindingChangeListener(this.listener);
		}
		this.binding = binding;
		if ((this.getCaption() == null) || (this.getCaption().length() == 0)) {
			if (binding != null && binding.getName() != null
					&& binding.getName().length() > 0) {
				this.setCaption(binding.getName());
			}
		}
		processEnablement(binding);
		if (binding != null) {
			this.setRole(binding.getRole());
		}
		this.internalSetBinding(binding);
		if (binding != null) {
			binding.addBindingChangeListener(this.listener);
		}

		this.fireBindingChanged(binding, old);
		this.shouldIngoreChanges = false;
	}

	boolean enablementFromBinding = true;

	public boolean isEnablementFromBinding() {
		return enablementFromBinding;
	}

	public void setEnablementFromBinding(boolean enablementFromBinding) {
		this.enablementFromBinding = enablementFromBinding;
	}

	protected void processEnablement(IBinding binding) {
		if (enablementFromBinding) {
			this.setEnabled(this.isEnabled(binding));
		}
	}

	protected boolean isEnabled(IBinding binding) {
		return binding == null || !binding.isReadOnly();
	}

	protected void fireBindingChanged(IBinding newValue, IBinding oldValue) {
		super.fireBindingChanged(newValue, oldValue);
		for (final IBindingSetListener la : this.listeners) {
			la.bindingChanged(this, newValue, oldValue);
		}
	}

	protected void unhookBinding(IBinding old) {
		if (this.binding != null) {
			this.binding.removeBindingChangeListener(this.listener);
		}
	}

	protected abstract void internalSetBinding(IBinding binding);

	protected void commitToBinding(Object newValue) {
		if (this.binding != null) {

			this.binding.setValue(newValue, this.listener);
		}
	}

	public void dispose() {
		this.unhookBinding(this.binding);
		super.dispose();
	}

	protected void setIgnoreChanges(boolean ignore) {
		this.ignore = ignore;
	}

	public final BindingExpressionController controller=new BindingExpressionController(this,this);
	
	@HandlesAttributeIndirectly("bindTo")
	public BindingExpressionController getBindingExpressionController(){
		return controller;
	}
}
