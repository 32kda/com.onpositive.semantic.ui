package com.onpositive.commons.namespace.ide.ui;

import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.model.api.realm.AbstractFilter;
import com.onpositive.semantic.model.api.realm.IFilter;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.tree.ITreeNode;

public class HideAbstractFilter extends AbstractFilter implements IFilter {

	public boolean accept(Object element) {
		if (element instanceof ITreeNode<?>){
			ITreeNode<?>el=(ITreeNode<?>) element;
			element=el.getElement();
		}
		if (element instanceof IClusterizationPoint<?>){
			return true;
		}
		final ElementModel m = (ElementModel) element;
		return !m.isAbstract();
	}
}
