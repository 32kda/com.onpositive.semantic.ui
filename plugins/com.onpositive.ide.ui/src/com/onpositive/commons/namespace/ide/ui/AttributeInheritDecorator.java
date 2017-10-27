package com.onpositive.commons.namespace.ide.ui;

import java.util.Collection;

import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.ModelReadOnlyConstrainer;
import com.onpositive.semantic.model.api.decoration.DecorationContext;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.richtext.IRichTextDecorator;
import com.onpositive.semantic.model.ui.richtext.StyledString;

public class AttributeInheritDecorator implements
		IRichTextDecorator {

	public AttributeInheritDecorator() {
	}

	public StyledString decorate(DecorationContext parameterObject,
			StyledString text) {

		Object value =parameterObject.object;		
		if (value instanceof Collection){
			return text;
		}
		Object object = ((IBinding)parameterObject.binding).getObject();
		if (!(object instanceof ElementModel)){
			return text;
		}
		final ElementModel el = (ElementModel) object;
		final AttributeModel ml = (AttributeModel) parameterObject.object;
		if (ml.getOwner() != el) {
			text.append(new StyledString("(" + ml.getOwner().getName()
					+ ")", new StyledString.Style(
					"com.onpositive.semantic.ui.decoration", null)));
		}
		if (ModelReadOnlyConstrainer.isExternal(ml)){
			text.append(new StyledString("(readonly)", new StyledString.Style(
					"com.onpositive.semantic.ui.decoration", null)));
		}
		return text;
	}

}
