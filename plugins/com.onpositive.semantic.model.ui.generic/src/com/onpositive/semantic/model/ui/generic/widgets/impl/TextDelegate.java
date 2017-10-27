package com.onpositive.semantic.model.ui.generic.widgets.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.labels.NotFoundException;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class TextDelegate extends EditorDelegate {

	public TextDelegate(BasicUIElement<?> ui) {
		super(ui);
	}

	String separatorCharacters = ","; //$NON-NLS-1$
	
	

	public String getSeparatorCharacters() {
		return this.separatorCharacters;
	}

	public void setSeparatorCharacters(String separatorCharacters) {
		this.separatorCharacters = separatorCharacters;
	}

	private boolean useLabelProviderForNull;

	public void setUseLabelProviderForNull(boolean useLabelProviderForNull) {
		this.useLabelProviderForNull = useLabelProviderForNull;
	}

	public boolean isUseLabelProviderForNull() {
		return this.useLabelProviderForNull;
	}

	public void internalSetBinding(IBinding binding) {
		if (binding != null) {
			final Object value = binding.getValue();
			this.setValue(binding, value);
		}
	}

	protected void setValue(IBinding binding, Object value) {
		BaseMeta dd = new BaseMeta(binding.getMeta());
		dd.setDefaultMeta(ui.getMeta());
		final ITextLabelProvider adapter = dd
				.getService(ITextLabelProvider.class);
		String stringValue = DefaultMetaKeys.getStringValue(dd,
				DefaultMetaKeys.SEPARATOR_CHARACTERS_KEY);
		if (stringValue != null) {
			separatorCharacters = stringValue;
		}
		Collection<Object> collectionIfCollection = ValueUtils
				.toCollectionIfCollection(value);
		value = collectionIfCollection != null ? collectionIfCollection : value;
		if (adapter != null) {
			if ((!binding.allowsMultiValues()) && (value instanceof Collection)) {
				final Collection<?> c = (Collection<?>) value;
				if (c.isEmpty()) {
					if (this.useLabelProviderForNull) {
						ui.setText(adapter.getText(ui.getMeta(),
								ui.getParentObject(), null));
					} else {
						ui.setText(""); //$NON-NLS-1$
					}
				} else {
					final Object next = c.iterator().next();
					ui.setText(adapter.getText(ui.getMeta(), ui.getParentObject(),
							next));
				}
			} else {
				if ((value == null) && !this.isUseLabelProviderForNull()) {
					ui.setText(""); //$NON-NLS-1$
				} else {
					if (value instanceof Collection) {
						final StringBuilder bl = new StringBuilder();
						final Collection<?> c = (Collection<?>) value;
						int pos = 0;
						for (final Object o : c) {
							bl.append(adapter.getText(ui.getMeta(),
									ui.getParentObject(), o));
							if (pos != c.size() - 1) {
								bl.append(this.separatorCharacters.charAt(0));
							}
							pos++;
						}
						ui.setText(value != null ? bl.toString() : ""); //$NON-NLS-1$
					} else {
						ui.setText(adapter.getText(ui.getMeta(),
								ui.getParentObject(), value));
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
					if (pos != c.size()) {
						bl.append(this.separatorCharacters.charAt(0));
					}
				}
				ui.setText(bl.toString()); //$NON-NLS-1$
			} else {
				ui.setText(value != null ? value.toString() : ""); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void processValueChange(ISetDelta<?> valueElements) {
		if (!ui.getBinding().allowsMultiValues()) {
			if (!valueElements.getAddedElements().isEmpty()) {
				this.setValue(ui.getBinding(), valueElements.getAddedElements()
						.iterator().next());
			} else {
				if (!valueElements.getChangedElements().isEmpty()) {
					this.setValue(ui.getBinding(), valueElements
							.getChangedElements().iterator().next());
				} else {
					if (!valueElements.getRemovedElements().isEmpty()) {
						this.setValue(ui.getBinding(), null);
					}
				}
			}
		} else {
			this.setValue(ui.getBinding(), ui.getBinding().getValue());
//			Collection<Object> adjustStringIfNeeded;
//			try {
//				adjustStringIfNeeded = ui.getBinding().lookupByLabels(content);
//				adjustStringIfNeeded.addAll(valueElements.getAddedElements());
//				adjustStringIfNeeded.removeAll(valueElements
//						.getRemovedElements());
//				
//			} catch (final NotFoundException e) {
//				return;
//			}
		}
	}

	public ArrayList<String> getContent() {
		final ArrayList<String> prevs = new ArrayList<String>();
		if (ui.getBinding().allowsMultiValues()) {
			final String text2 = ui.getText();
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
			String mm = ui.getText();
			if (mm != null) {
				prevs.add(mm.trim());
			}
		}
		Collections.reverse(prevs);
		return prevs;
	}

	@Override
	public void setValue(Object value) {
		setValue(ui.getBinding(), value);
	}

	@Override
	public void handleChange(IUIElement<?> b, Object value) {
		IBinding binding = ui.getBinding();
		if (binding != null) {
			doCommit(ui.getBinding());
		}
	}

	protected void commitToBinding(Object newValue) {
		if (ui.getBinding() != null) {
			ui.getBinding().setValue(newValue, ui.binding_chlistener);
		}
	}

	protected void doCommit(final IBinding binding2) {
		final String text2 = ui.getText();
		if (binding2.allowsMultiValues()) {
			final ArrayList<String> content = getContent();
			Object lookupResult;
			try {
				lookupResult = binding2.lookupByLabels(content);
			} catch (final NotFoundException e) {
				return;
			}
			commitToBinding(lookupResult);
		} else {
			Object lookupResult;
			try {
				lookupResult = binding2.lookupByLabel(text2);
			} catch (final NotFoundException e) {
				return;
			}
			commitToBinding(lookupResult);
		}
	}
}
