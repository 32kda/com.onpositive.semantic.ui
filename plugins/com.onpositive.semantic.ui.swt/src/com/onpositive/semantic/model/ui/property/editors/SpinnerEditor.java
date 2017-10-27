package com.onpositive.semantic.model.ui.property.editors;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import com.onpositive.semantic.model.api.property.java.annotations.Range;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.EmptyDelta;
import com.onpositive.semantic.model.realm.HashDelta;
import com.onpositive.semantic.model.realm.ISetDelta;

public class SpinnerEditor extends AbstractEditor<Spinner> {

	protected boolean isFloat;
	protected double scale = 1;

	public SpinnerEditor(){
		getLayoutHints().setGrabVertical(false);
		getLayoutHints().setGrabHorizontal(true);
	}
	
	protected void internalSetBinding(IBinding binding) {
		if (this.isCreated()) {
			final Spinner control = this.getControl();
			this.configure(binding, control);
		}
	}

	protected void configure(IBinding binding, Spinner control) {
		final Range adapter = binding.getAdapter(Range.class);
		if (adapter != null) {
			final int digits = adapter.digits();
			if (digits != 0) {
				control.setDigits(digits);
				this.isFloat = true;
			} else {
				this.isFloat = false;
			}
			double max = adapter.max();
			double min = adapter.min();
			double increament = adapter.increment();
			double pageincrement = adapter.pageIncrement();
			this.scale = 1;
			for (int a = 0; a < digits; a++) {
				max = max * 10;
				min = min * 10;
				this.scale *= 10;
				increament = increament * 10;
				pageincrement = pageincrement * 10;
			}
			control.setMinimum((int) min);
			control.setMaximum((int) max);
			control.setIncrement((int) increament);
			control.setPageIncrement(((int) pageincrement));

			final Object vl = binding.getValue();
			if (vl != null) {
				final HashDelta<?> dlt = HashDelta.createAdd(vl);
				this.processValueChange(dlt);
			} else {
				this.processValueChange(EmptyDelta.getDelta());
			}
		}
		initValue(control, binding.getValue());
	}

	protected void processValueChange(ISetDelta<?> valueElements) {
		final Collection<?> addedElements = valueElements.getAddedElements();
		if (this.isCreated()) {
			final Spinner control = this.getControl();

			if (addedElements.isEmpty()) {
				control.setSelection(0);
			} else {
				final Object next = addedElements.iterator().next();
				initValue(control, next);
			}
		}
	}

	protected void initValue(final Spinner control, final Object next) {
		if (next instanceof String) {
			if (this.isFloat) {
				final double parseDouble = Double
						.parseDouble((String) next);
				control.setSelection((int) (parseDouble * this.scale));
			} else {
				try{
				final int k = Integer.parseInt((String) next);
				control.setSelection(k);
				}catch (NumberFormatException e) {
					// ignore sliently
				}
			}
		} else if (next instanceof Number) {
			final Number n = (Number) next;
			if (this.isFloat) {
				control
						.setSelection((int) (n.doubleValue() * this.scale));
			} else {
				control.setSelection(n.intValue());
			}
		} else {
			control.setSelection(0);
		}
	}

	protected Spinner createControl(Composite conComposite) {
		final Spinner spinner = new Spinner(conComposite, this.calcStyle()
				| SWT.BORDER);
		spinner.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL) {
					spinner.setSelection(spinner.getMinimum());
					e.doit = false;
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.DEL) {
					spinner.setSelection(spinner.getMinimum());
					e.doit = false;
				}
			}

		});
		final IBinding binding2 = this.getBinding();
		if (binding2 != null) {
			this.configure(binding2, spinner);
		}
		final Listener listener2 = new Listener() {

			public void handleEvent(Event event) {
				if (SpinnerEditor.this.shouldIgnoreChanges()) {
					return;
				}
				final int selection = spinner.getSelection();
				if (SpinnerEditor.this.isFloat) {
					final Number value = (selection) / SpinnerEditor.this.scale;
					this.processSet(binding2, value);
				} else {
					final Number value = spinner.getSelection();
					this.processSet(binding2, value);
				}

			}

			private void processSet(final IBinding binding2, Number value) {

				if (binding2 != null) {
					final Class<?> subjectClass = binding2.getSubjectClass();
					if (subjectClass == Double.class) {
						SpinnerEditor.this.commitToBinding(value);
					} else if (subjectClass == Float.class) {
						SpinnerEditor.this.commitToBinding(value.floatValue());
					} else if (subjectClass == Long.class) {
						SpinnerEditor.this.commitToBinding(value.longValue());
					} else if (subjectClass == Integer.class) {
						SpinnerEditor.this.commitToBinding(value.intValue());
					} else if (subjectClass == Short.class) {
						SpinnerEditor.this.commitToBinding(value.shortValue());
					} else if (subjectClass == double.class) {
						SpinnerEditor.this.commitToBinding(value);
					} else if (subjectClass == float.class) {
						SpinnerEditor.this.commitToBinding(value.floatValue());
					} else if (subjectClass == long.class) {
						SpinnerEditor.this.commitToBinding(value.longValue());
					} else if (subjectClass == int.class) {
						SpinnerEditor.this.commitToBinding(value.intValue());
					} else if (subjectClass == short.class) {
						SpinnerEditor.this.commitToBinding(value.shortValue());
					} else if (subjectClass == byte.class) {
						SpinnerEditor.this.commitToBinding(value.byteValue());
					} else if (subjectClass == Byte.class) {
						SpinnerEditor.this.commitToBinding(value.byteValue());
					} else if (subjectClass == String.class) {
						SpinnerEditor.this.commitToBinding(value.toString());
					} else {
						SpinnerEditor.this.commitToBinding(value);
					}
				}
			}

		};
		spinner.addListener(SWT.Modify, listener2);
		spinner.addListener(SWT.Selection, listener2);
		spinner.addListener(SWT.Verify, listener2);
		return spinner;
	}

}
