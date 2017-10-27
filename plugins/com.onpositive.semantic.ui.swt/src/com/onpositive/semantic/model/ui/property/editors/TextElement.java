package com.onpositive.semantic.model.ui.property.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.property.adapters.NotFoundException;
import com.onpositive.semantic.model.api.property.java.annotations.SeparatorCharacters;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.ui.generic.widgets.ICanBeReadOnly;
import com.onpositive.semantic.model.ui.generic.widgets.ITextElement;
import com.onpositive.semantic.ui.core.Alignment;

public abstract class TextElement<T, A extends Control> extends
		DecoratableEditor<T, A> implements ICanBeReadOnly<A>,ITextElement<A>{

	String separatorCharacters = ","; //$NON-NLS-1$

	public String getSeparatorCharacters() {
		return this.separatorCharacters;
	}

	public void setSeparatorCharacters(String separatorCharacters) {
		this.separatorCharacters = separatorCharacters;
	}

	private boolean useLabelProviderForNull;
	private boolean readOnlyStyle;

	public void setUseLabelProviderForNull(boolean useLabelProviderForNull) {
		this.useLabelProviderForNull = useLabelProviderForNull;
	}

	public boolean isUseLabelProviderForNull() {
		return this.useLabelProviderForNull;
	}

	protected void processValueChange(ISetDelta<?> valueElements) {
		final ArrayList<String> content = this.getContent();
		if (!this.getBinding().allowsMultiValues()) {
			if (!valueElements.getAddedElements().isEmpty()) {
				this.setValue(this.getBinding(), valueElements
						.getAddedElements().iterator().next());
			} else {
				if (!valueElements.getChangedElements().isEmpty()) {
					this.setValue(this.getBinding(), valueElements
							.getChangedElements().iterator().next());
				} else {
					if (!valueElements.getRemovedElements().isEmpty()) {
						this.setValue(this.getBinding(), null);
					}
				}
			}
		} else {
			Collection<Object> adjustStringIfNeeded;
			try {
				adjustStringIfNeeded = this.getBinding().lookupByLabels(content);
				adjustStringIfNeeded.addAll(valueElements.getAddedElements());
				adjustStringIfNeeded.removeAll(valueElements
						.getRemovedElements());
				this.setValue(this.getBinding(), adjustStringIfNeeded);
			} catch (final NotFoundException e) {
				return;
			}
		}
	}

	protected void internalSetBinding(IBinding binding) {
		if (binding != null) {
			final Object value = binding.getValue();
			this.setValue(binding, value);
		}
	}
	
	

	protected void setValue(IBinding binding, Object value) {
		final ITextLabelProvider adapter = binding
				.getAdapter(ITextLabelProvider.class);
		SeparatorCharacters adapter2 = binding.getAdapter(SeparatorCharacters.class);
		if (adapter2!=null){
			separatorCharacters= adapter2.value();
		}
		if (adapter != null) {
			if ((!binding.allowsMultiValues())
					&& (value instanceof Collection)) {
				final Collection<?> c = (Collection<?>) value;
				if (c.isEmpty()) {
					if (this.useLabelProviderForNull) {
						this.setText(adapter.getText(null));
					} else {
						this.setText(""); //$NON-NLS-1$
					}
				} else {
					final Object next = c.iterator().next();
					this.setText(adapter.getText(next));
				}
			} else {
				if ((value == null) && !this.isUseLabelProviderForNull()) {
					this.setText(""); //$NON-NLS-1$
				} else {
					if (value instanceof Collection) {
						final StringBuilder bl = new StringBuilder();
						final Collection<?> c = (Collection<?>) value;
						int pos = 0;
						for (final Object o : c) {
							bl.append(adapter.getText(o));
							if (pos != c.size() - 1) {
								bl.append(this.separatorCharacters.charAt(0));
							}
							pos++;
						}
						this.setText(value != null ? bl.toString() : ""); //$NON-NLS-1$
					} else {
						this.setText(adapter.getText(value));
					}
				}
			}
		} else {
			if (value instanceof Collection) {
				final StringBuilder bl = new StringBuilder();
				final Collection<?> c = (Collection<?>) value;
				int pos = 0;
				for (final Object o : c) {
					bl.append(o.toString());
					pos++;
					if (pos != c.size() ) {
						bl.append(this.separatorCharacters.charAt(0));
					}
				}
				this.setText(bl.toString()); //$NON-NLS-1$
			} else {
				this.setText(value != null ? value.toString() : ""); //$NON-NLS-1$
			}
		}
	}

	public ArrayList<String> getContent() {
		final ArrayList<String> prevs = new ArrayList<String>();
		if (this.getBinding().allowsMultiValues()) {
			final String text2 = this.getText();
			int a = text2.length();
			if (this.separatorCharacters != null) {
				int b = a - 1;
				for (; b > 0; b--) {
					if (this.separatorCharacters.indexOf(text2.charAt(b)) != -1) {
						final String substring = text2.substring(b + 1, a);
						final String trim = substring.trim();
						if (trim.length() > 0) {
							if (!prevs.contains(trim)) {
								prevs.add(trim);
							}
						}
						a = b;
					}
				}
				final String substring = a != text2.length() ? text2.substring(
						b, a) : text2;
				final String trim = substring.trim();
				if (trim.length() > 0) {
					if (!prevs.contains(trim)) {
						prevs.add(trim);
					}
				}
			}
		} else {
			prevs.add(this.getText().trim());
		}
		Collections.reverse(prevs);
		return prevs;
	}

	public TextElement() {
		this(SWT.NONE);
	}

	public TextElement(int style) {
		super(style);
		getLayoutHints().setGrabHorizontal(true);
		getLayoutHints().setAlignmentHorizontal(Alignment.FILL);
	}

	public void setReadOnly(boolean parseBoolean) {
		final boolean isReadOnly = this.readOnlyStyle;
		this.readOnlyStyle = parseBoolean;
		if (this.isCreated() && (isReadOnly != parseBoolean)) {
			this.recreate();
		}
	}

	public boolean isReadOnly() {
		return this.readOnlyStyle;
	}

}