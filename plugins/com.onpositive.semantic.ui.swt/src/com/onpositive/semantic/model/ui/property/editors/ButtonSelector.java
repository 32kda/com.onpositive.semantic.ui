package com.onpositive.semantic.model.ui.property.editors;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IFactory;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.ui.generic.widgets.ICanBeReadOnly;
import com.onpositive.semantic.model.ui.generic.widgets.IHasImage;
import com.onpositive.semantic.ui.core.Alignment;

public class ButtonSelector extends AbstractEditor<Button>implements ICanBeReadOnly<Button>,IHasImage {

	protected Object value;
	private boolean readOnlyStyle;
	private Image image;
	
	

	public void setImage(Image image) {
		this.image = image;
	}

	private IPropertyChangeListener listener=new IPropertyChangeListener(){

		public void propertyChange(PropertyChangeEvent event) {
			setEnabled(((Action)value).isEnabled());
		}
		
	};

	public ButtonSelector(IBinding binding) {
		this.setBinding(binding);
		getLayoutHints().setAlignmentHorizontal(Alignment.LEFT);
	}

	public ButtonSelector() {
		getLayoutHints().setAlignmentHorizontal(Alignment.LEFT);
	}

	public ButtonSelector(IBinding binding, int radio) {
		super(radio);
		this.setBinding(binding);
		getLayoutHints().setAlignmentHorizontal(Alignment.LEFT);
	}

	public ButtonSelector(int check) {
		super(check);
		getLayoutHints().setAlignmentHorizontal(Alignment.LEFT);
	}

	public boolean needsLabel() {
		return false;
	}

	protected void internalSetBinding(IBinding binding) {
		// setText(binding.getName());
		this.setValue(binding.getValue());
	}

	public void setValue(Object value) {
		if (this.value instanceof Action){
			Action ac=(Action) this.value;
			ac.removePropertyChangeListener(listener);
		}
		this.value = value;
		final Button control = this.getControl();
		this.adjustValue(value, control);
	}

	private void adjustValue(Object value, Button control) {
		
		if (control != null) {
			if (this.value != null) {
				if (value instanceof Boolean) {
					control.setSelection((Boolean) this.value);
				}
				
				else if (value instanceof Action) {
					final Action action = (Action) this.value;
					control.setSelection(action.isChecked());
					action.addPropertyChangeListener(listener);
					setEnabled(action.isEnabled());
					if (this.getCaption() == null) {
						this.setCaption(action.getText());
					}
				}
				else if (value instanceof IFactory) {
					final IFactory factory = (IFactory) this.value;
					if (this.getCaption() == null) {
						this.setCaption(factory.getName());
					}
				}
				else{
					control.setSelection((Boolean) Boolean.parseBoolean(this.value.toString()));
				}
			} else {
				if (this.isCreated()) {
					this.getControl().setSelection(false);
				}
			}
		}
	}

	public int calcStyle() {
		if (this.style != null) {
			return this.style;
		}
		if (this.getBinding() == null) {
			return super.calcStyle();
		}
		final Class<?> subjectClass = this.getBinding().getSubjectClass();
		if (subjectClass == boolean.class) {
			return SWT.CHECK;
		}
		if (subjectClass == Boolean.class) {
			return SWT.CHECK;
		} else if (Action.class.isAssignableFrom(subjectClass)) {
			final Action c = (Action) this.getBinding().getValue();
			if (c.getStyle() == IAction.AS_CHECK_BOX) {
				return SWT.CHECK;
			}
			if (c.getStyle() == IAction.AS_RADIO_BUTTON) {
				return SWT.RADIO;
			}
			return SWT.PUSH;
		} else if (Runnable.class.isAssignableFrom(subjectClass)) {
			return SWT.PUSH;
		} else if (IFactory.class.isAssignableFrom(subjectClass)) {
			return SWT.PUSH;
		}
		return SWT.PUSH;
	}

	protected Button createControl(Composite conComposite) {
		final Button button = new Button(conComposite, this.calcStyle());
		button.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				if (ButtonSelector.this.readOnlyStyle) {
					button.setSelection(!button.getSelection());
				}
				final boolean selection = button.getSelection();
				final IBinding binding2 = ButtonSelector.this.getBinding();
				Class<?> subjectClass = this.getSubjectClass();
				if (binding2 != null) {
					if (Action.class.isAssignableFrom(subjectClass)) {
						binding2.actionPerformed(event, this);
					} else if (IFactory.class
							.isAssignableFrom(subjectClass)) {
						binding2.actionPerformed(event, this);
					} else {
						if (Runnable.class
								.isAssignableFrom(subjectClass)) {
							binding2.actionPerformed(event, this);
						} else {
							if (subjectClass==String.class){
								ButtonSelector.this.commitToBinding(selection+"");	
							}
							else{
							ButtonSelector.this.commitToBinding(selection);
							}
						}
					}
				} else {
					if (Action.class.isAssignableFrom(subjectClass)) {
						final Action a = (Action) ButtonSelector.this.value;
						a.run();
					} else if (IFactory.class
							.isAssignableFrom(subjectClass)) {
						final IFactory a = (IFactory) ButtonSelector.this.value;
						a.getValue(null);
					} else if (Runnable.class
							.isAssignableFrom(subjectClass)) {
						final Runnable a = (Runnable) ButtonSelector.this.value;
						a.run();
					}
				}
			}

			private Class<?> getSubjectClass() {
				final IBinding binding2 = ButtonSelector.this.getBinding();
				if (binding2 == null) {
					if (ButtonSelector.this.value != null) {
						return ButtonSelector.this.value.getClass();
					}
					return Boolean.class;
				}
				return binding2.getSubjectClass();
			}

		});
		this.adjustValue(this.value, button);
		if (image!=null){
			button.setImage(image);
		}
		else if (imageString!=null){
			button.setImage(SWTImageManager.getImage(imageString));
		}
		return button;
	}

	protected void processValueChange(ISetDelta<?> valueElements) {
		final Collection<?> addedElements = valueElements.getAddedElements();
		if (!addedElements.isEmpty()) {
			final Object next2 = addedElements.iterator().next();
			if (next2 instanceof Boolean) {
				final Boolean next = (Boolean) next2;
				this.getControl().setSelection(next);
			} else {
				this.getControl().setSelection(false);
			}
		}
	}

	public void setReadOnly(boolean parseBoolean) {
		this.readOnlyStyle = parseBoolean;
	}

	public boolean isReadOnly() {
		return readOnlyStyle;
	}

	String imageString;

	public void setImage(String image) {
		imageString=image;
		if (isCreated()){
			getControl().setImage(SWTImageManager.getImage(imageString));
		}
	}

	public String getImage() {
		return imageString;
	}

}