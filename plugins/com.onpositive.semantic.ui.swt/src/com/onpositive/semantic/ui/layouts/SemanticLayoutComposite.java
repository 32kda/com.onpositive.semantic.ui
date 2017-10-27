package com.onpositive.semantic.ui.layouts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.HashDelta;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;

public class SemanticLayoutComposite extends CompositeEditor {

	private ArrayList<VisualisationAspect> currentElements = new ArrayList<VisualisationAspect>();
	private VisualisationAspect currentAspect;
	private final ISemanticLayout layout = new TabFolderLayout();

	public SemanticLayoutComposite() {
		this.add((AbstractUIElement<?>) this.layout);
	}

	protected void internalSetBinding(IBinding binding2) {
		super.internalSetBinding(binding2);
		((IPropertyEditor<?>) this.layout).setBinding(binding2);
	}

	protected void processValueChange(ISetDelta<?> valueElements) {

	}

	public void create() {
		super.create();
		final Object value = this.getBinding().getValue();
		this.internalSet(value);
	}

	@SuppressWarnings("unchecked")
	protected void internalSet(Object object) {
		if (!this.isCreated()) {
			return;
		}
		final ArrayList<VisualisationAspect> elements = AspectRegistry
				.getInstance().getElements(object, this.getRole(),
						this.getTheme());
		final HashDelta<VisualisationAspect> dltDelta = HashDelta.buildFrom(
				this.currentElements, elements);
		final boolean installNew = !elements.contains(this.currentAspect);
		Collections.sort(elements);
		if (!elements.contains(this.currentAspect)) {

		}
		for (final VisualisationAspect a : dltDelta.getRemovedElements()) {
			this.removeAspect(a);
		}
		final ArrayList<VisualisationAspect> addedElements = new ArrayList<VisualisationAspect>(
				dltDelta.getAddedElements());
		Collections.sort(addedElements);
		for (final VisualisationAspect e : addedElements) {
			this.installAspect(e);
		}
		if (installNew && (elements.size() > 0)) {
			this.selectAspect(elements.get(0));
		}
		for (final VisualisationAspect a : elements) {
			this.refreshAspect(a);
		}
		this.currentElements = elements;
	}

	protected void installAspect(VisualisationAspect e) {
		this.layout.installAspect(e);
	}

	protected void removeAspect(VisualisationAspect a) {
		this.layout.uninstallAspect(a);
	}

	protected void refreshAspect(VisualisationAspect e) {
		this.layout.refreshAspect(e);
	}

	public List<VisualisationAspect> getAspects() {
		return new ArrayList<VisualisationAspect>(this.currentElements);
	}

	public void selectAspect(VisualisationAspect aspect) {
		this.currentAspect = aspect;
		this.layout.setActive(aspect);
	}
}