package com.onpositive.ide.ui;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeCompletionProvider;

public class ExtensionCompletionProvider implements ITypeCompletionProvider {

	public ExtensionCompletionProvider() {

	}

	public void fillProposals(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String startString, int lengthCompletion,
			ArrayList<ICompletionProposal> result, String fullString,
			boolean addBraces, String typeSpec) {
		if (typeSpec != null && typeSpec.indexOf('/') != -1) {
			String trim = typeSpec.trim();
			int p = trim.indexOf('/');
			String extension = trim.substring(0, p);
			String elem = trim.substring(p + 1);
			new ExtensionElementCompletionProposal(extension, elem)
					.fillProposals(attributeName, findElement, viewer, offset,
							startString, lengthCompletion, result, fullString,
							addBraces, typeSpec);
		}

	}

}
