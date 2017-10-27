package com.onpositive.commons.namespace.ide.ui;

import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.ModelElement;
import com.onpositive.semantic.model.api.realm.AbstractFilter;
import com.onpositive.semantic.model.api.realm.IFilter;
import com.onpositive.semantic.model.api.realm.IOwned;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.tree.ITreeNode;

public class InheritedAttributesFilter extends AbstractFilter implements
		IFilter,IOwned {

	private Binding binding;

	public boolean accept(Object element) {
		//FIXME
		if (element instanceof ITreeNode<?>){
			ITreeNode<?>el=(ITreeNode<?>) element;
			element=el.getElement();
		}
		if (element instanceof IClusterizationPoint<?>){
			return true;
		}
		final AttributeModel ma = (AttributeModel) element;
		final ModelElement owner = ma.getOwner();
		Object value2 = this.binding.getParent()
				.getValue();
		if (value2 instanceof ElementModel){
			final ElementModel value = (ElementModel) value2;
			return value == owner;
		}
		return true;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}

	public void setOwner(Object owner) {
		if (owner instanceof IBindable){
			IBindable b=(IBindable) owner;
			this.binding=(Binding) b.getBinding();
		}
		else{
			this.binding=null;
		}
	}

}
