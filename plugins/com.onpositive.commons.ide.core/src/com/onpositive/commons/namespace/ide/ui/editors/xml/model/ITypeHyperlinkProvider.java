package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public interface ITypeHyperlinkProvider {

	IHyperlink[] calculateHyperlinks(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String startString, int lengthCompletion,
			String fullString, String typeSpecialization);
}
