package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;


public interface ITypeCompletionProvider {

	void fillProposals(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String startString, int lengthCompletion,
			ArrayList<ICompletionProposal> result, String fullString, boolean addBraces, String typeSpecialization);

}
