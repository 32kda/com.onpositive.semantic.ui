package com.onpositive.semantic.model.ui.property.editors;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.UniversalUIElement;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.commons.xml.language.HandlesAttributeIndirectly;
import com.onpositive.commons.xml.language.HandlesParent;
import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.ui.generic.IBindingSetListener;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.property.editors.structured.ComboEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;

@SuppressWarnings("rawtypes")
public class CompositeEditor extends Container implements
		IPropertyEditor<AbstractUIElement> {

	protected boolean bindingInTitle = false;
	private boolean shouldIngoreChanges;
	private Object object;

	protected boolean shouldIgnoreChanges() {
		return this.shouldIngoreChanges;
	}

	public boolean isBindingInTitle() {
		return this.bindingInTitle;
	}

	@HandlesAttributeDirectly("showValueInTitle")
	public void setBindingInTitle(boolean bindingInTitle) {
		this.bindingInTitle = bindingInTitle;
	}

	private final IBindingChangeListener<Object> listener = new IBindingChangeListener<Object>() {

		public void valueChanged(ISetDelta<Object> valueElements) {
			CompositeEditor.this.shouldIngoreChanges = true;
			try {
				CompositeEditor.this.processValueChange(valueElements);
			} finally {
				CompositeEditor.this.shouldIngoreChanges = false;
			}
		}

		public void enablementChanged(boolean isEnabled) {
			if (CompositeEditor.this.isCreated()) {
				CompositeEditor.this.setEnabled(isEnabled);
			}
		}

		public void changed() {
			final IBinding binding2 = CompositeEditor.this.getBinding();
			CompositeEditor.this.binding = null;
			CompositeEditor.this.setBinding(binding2);
		}

	};

	protected IBinding binding;
	protected HashMap<String, IPropertyEditor<?>> editors = new HashMap<String, IPropertyEditor<?>>();

	private String role;

	private IValueListener<Object> exp;
	private boolean isEnablementFromBinding = true;

	public CompositeEditor() {
		this.setLayoutManager(new OneElementOnLineLayouter());
	}

	public CompositeEditor(Object object, boolean autoCommit) {
		this.setLayoutManager(new OneElementOnLineLayouter());
		final Binding bnd = new Binding(object);
		bnd.setAutoCommit(autoCommit);
		this.setBinding(bnd);
	}

	public CompositeEditor(IBinding bnd) {
		this.setLayoutManager(new OneElementOnLineLayouter());
		this.setBinding(bnd);
	}

	public CompositeEditor(IBinding bnd, int style) {
		super(style);
		this.setLayoutManager(new OneElementOnLineLayouter());
		this.setBinding(bnd);
	}

	protected void processValueChange(ISetDelta<?> valueElements) {

	}

	public IBinding getBinding() {
		return this.binding;
	}

	protected void unhookBinding(IBinding old) {
		if (this.binding != null) {
			this.binding.removeBindingChangeListener(this.listener);
		}
	}

	public void dispose() {
		this.unhookBinding(this.binding);
		super.dispose();
	}

	
	public void setBinding(IBinding bnd) {

		this.shouldIngoreChanges = true;
		final IBinding old = this.binding;
		if (old == bnd) {
			return;
		}
		this.unhookBinding(old);

		if (this.binding != null) {
			this.binding.removeBindingChangeListener(this.listener);
		}
		this.binding = bnd;
		if (this.isBindingInTitle() && binding != null) {
			this.setCaption(this.binding.getName());
		}
		if (this.binding != null) {
			if (this.isEnablementFromBinding) {
				this.setEnabled(!this.binding.isReadOnly());
			}
			this.setRole(this.binding.getRole());
		}
		this.internalSetBinding(this.binding);

		if (this.binding != null) {
			this.binding.addBindingChangeListener(this.listener);
		}
		this.fireBindingChanged(this.binding, old);
		this.shouldIngoreChanges = false;
		if (this.binding != null) {
			for (final String s : this.editors.keySet()) {
				this.editors.get(s).setBinding(this.binding.getBinding(s));
			}
		}
		this.fireBindingChanged(bnd, old);
	}

	protected void internalSetBinding(IBinding binding2) {
		if (this.binding != null) {
			this.binding.removeValueListener(this.exp);
		}
		if (binding2 == null) {
			return;
		}
		final Object value = binding2.getValue();
		this.set(null, value);
		this.exp = new IValueListener<Object>() {

			public void valueChanged(Object oldValue, Object newValue) {
				CompositeEditor.this.set(oldValue, newValue);
			}

		};
		binding2.addValueListener(this.exp);
	}

	protected final void set(Object value, Object newValue) {
		if (newValue == value) {
			return;
		}
		if (value != null) {
			if ((this.object != null) && this.object.equals(newValue)) {
				return;
			}
		}
		if (this.isCreated() && this.isRedraw()) {
			this.getControl().setLayoutDeferred(true);
			this.getControl().setRedraw(false);
		}
		this.internalSet(newValue);
		if (this.isCreated() && this.isRedraw()) {
			this.getControl().setLayoutDeferred(false);
			this.getControl().setRedraw(true);
		}
		this.object = newValue;

	}

	protected void internalSet(Object value) {

	}

	public void setObject(Object object) {
		this.binding.setValue(object, null);
	}

	public Object getObject() {
		return this.binding.getValue();
	}

	public void addField(String bindTo) {

	}

	public void addString(String bindTo) {
		this.addField(bindTo, new OneLineTextElement<Object>());
	}

	public void addCombo(String bindTo) {
		this.addField(bindTo, new ComboEnumeratedValueSelector<Object>());
	}

	public void addList(String bindTo) {
		this.addField(bindTo, new ListEnumeratedValueSelector<Object>());
	}

	public void addBoolean(String bindTo) {
		this.addField(bindTo, new ButtonSelector(SWT.CHECK));
	}

	public void addButton(String bindTo) {
		this.addField(bindTo, new ButtonSelector(SWT.PUSH));
	}

	public void addSpinner(String bindTo) {
		this.addField(bindTo, new SpinnerEditor());
	}

	@SuppressWarnings("rawtypes")
	public void addField(String bindTo,
			IPropertyEditor<? extends AbstractUIElement> editor) {
		this.addField(bindTo, editor, this);
	}

	@SuppressWarnings("rawtypes")
	public void addField(String bindTo,
			IPropertyEditor<? extends AbstractUIElement> editor, Container owner) {
		this.editors.put(bindTo, editor);
		if (this.binding != null) {
			editor.setBinding(this.binding.getBinding(bindTo));
		}
		owner.add(editor.getUIElement());
	}

	public void removeField(String field) {
		final IPropertyEditor<?> element = this.editors.get(field);
		this.editors.remove(field);
		this.remove((AbstractUIElement<?>) element.getUIElement());
	}

	public void create() {
		super.create();
		this.internalSetBinding(this.getBinding());
	}

	public void addSeparator(boolean vertical) {
		this.add(new UniversalUIElement<Label>(Label.class,
				vertical ? SWT.VERTICAL : SWT.HORIZONTAL));
	}

	private final HashSet<IBindingSetListener> listeners = new HashSet<IBindingSetListener>();

	public void addBindingSetListener(IBindingSetListener ls) {
		this.listeners.add(ls);
	}

	public void removeBindingSetListener(IBindingSetListener ls) {
		this.listeners.remove(ls);
	}

	public AbstractUIElement<? extends Control> getUIElement() {
		return this;
	}

	protected void fireBindingChanged(IBinding newValue, IBinding oldValue) {
		super.fireBindingChanged(newValue, oldValue);
		for (final IBindingSetListener la : this.listeners) {
			la.bindingChanged(this, newValue, oldValue);
		}
	}

	public void setEnablementFromBinding(boolean b) {
		this.isEnablementFromBinding = b;
	}

	public final BindingExpressionController controller = new BindingExpressionController(
			this, this);

	@HandlesAttributeIndirectly("bindTo")
	public BindingExpressionController getBindingExpressionController() {
		return controller;
	}

}
