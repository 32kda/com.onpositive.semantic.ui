package com.onpositive.semantic.model.ui.generic.widgets.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collection;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.generic.widgets.IButton;
import com.onpositive.semantic.model.ui.generic.widgets.ICanBeReadOnly;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class ButtonDelegate extends EditorDelegate {

	private final class PL implements PropertyChangeListener ,Serializable{
		public void propertyChange(PropertyChangeEvent event) {
			ui.setEnabled(((IAction) value).isEnabled());
		}
	}

	private Object value;
	
	private String expressionToSet;

	public String getExpressionToSet() {
		return expressionToSet;
	}

	public void setExpressionToSet(String expressionToSet) {
		this.expressionToSet = expressionToSet;
	}

	public ButtonDelegate(BasicUIElement<?> ui) {
		super(ui);
	}

	private PropertyChangeListener listener = new PL();
	public void internalSetBinding(IBinding binding) {
		if (ui.getText()==null&&ui.getCaption()==null||ui.getCaption().length()==0){
			String name = binding.getName();
			ui.setText(name);
		}
		this.setValue(binding.getValue());
	}

	public void onDispose(
			com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement<?> element) {
		unhookListener();
	};

	public void setValue(Object value) {
		unhookListener();
		this.value = value;
		final IButton<?> control = (IButton<?>) ui;
		this.adjustValue(value, control);
	}

	private void unhookListener() {
		if (this.value instanceof IAction) {
			IAction ac = (IAction) this.value;
			ac.removePropertyChangeListener(listener);
		}
	}

	@SuppressWarnings("rawtypes")
	public void processValueChange(ISetDelta<?> valueElements) {
		final Collection<?> addedElements = valueElements.getAddedElements();
		if (!addedElements.isEmpty()) {
			final Object next2 = addedElements.iterator().next();			
			if (next2 instanceof Boolean) {
				final Boolean next = (Boolean) next2;
				((IButton) ui).setSelection(next);
			} else {
				((IButton) ui).setSelection(false);
			}
		}
	}

	private Class<?> getSubjectClass() {
		final IBinding binding2 = ui.getBinding();
		if (binding2 == null) {
			if (this.value != null) {
				return this.value.getClass();
			}
			return Boolean.class;
		}
		return binding2.getSubjectClass();
	}

	public void handleChange(IUIElement<?> b,Object value) {
		IButton<?> button=(IButton<?>) b;
		if (ui instanceof ICanBeReadOnly) {
			if (((ICanBeReadOnly<?>) ui).isReadOnly()) {
				button.setSelection(!button.getSelection());
			}
		}
		final boolean selection = button.getSelection();
		
		final IBinding binding2 = ui.getBinding();
		if (expressionToSet!=null){
			Object calculate = ExpressionAccess.calculate(expressionToSet, binding2,selection);
			ui.commitToBinding(calculate);
			return;
		}
		Class<?> subjectClass = this.getSubjectClass();
		if (binding2 != null) {
			if (IAction.class.isAssignableFrom(subjectClass)) {
				binding2.actionPerformed(null, this);
			} else if (IFunction.class.isAssignableFrom(subjectClass)) {
				binding2.actionPerformed(null, this);
			} else {
				if (Runnable.class.isAssignableFrom(subjectClass)) {
					binding2.actionPerformed(null, this);
				} else {
					if (subjectClass == String.class) {
						ui.commitToBinding(selection + "");
					} else {
						ui.commitToBinding(selection);
					}
				}
			}
		} else {
			if (IAction.class.isAssignableFrom(subjectClass)) {
				final IAction a = (IAction) this.value;
				a.run();
			} else if (IFunction.class.isAssignableFrom(subjectClass)) {
				final IFunction a = (IFunction) this.value;
				a.getValue(null);
			} else if (Runnable.class.isAssignableFrom(subjectClass)) {
				final Runnable a = (Runnable) this.value;
				a.run();
			}
		}
	}

	public void adjustValue(Object value, IButton<?> control) {

		if (control != null) {
			if (this.value != null) {
				if (value instanceof Boolean) {
					control.setSelection((Boolean) this.value);
				}

				else if (value instanceof IAction) {
					final IAction IAction = (IAction) this.value;
					control.setSelection(IAction.getSelection());
					IAction.addPropertyChangeListener(listener);
					ui.setEnabled(IAction.isEnabled());
					if (ui.getCaption() == null) {
						ui.setCaption(IAction.getText());
					}
				} else if (value instanceof IFunction) {
					final IFunction factory = (IFunction) this.value;
					if (ui.getCaption() == null) {
						ui.setCaption(DefaultMetaKeys.getCaption(factory));
					}
				} else {
					control.setSelection((Boolean) Boolean
							.parseBoolean(this.value.toString()));
				}
			} else {
				if (ui.isCreated()) {
					control.setSelection(false);
				}
			}
		}
	}

	public void initValue() {
		adjustValue(value, (IButton<?>) ui);
	}

	
}
