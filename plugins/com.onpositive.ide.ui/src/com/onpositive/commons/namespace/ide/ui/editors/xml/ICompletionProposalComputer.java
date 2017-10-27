package com.onpositive.commons.namespace.ide.ui.editors.xml;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModel;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;

public interface ICompletionProposalComputer {
	ICompletionProposal[] computeProposals(DomainEditingModel model,
			DomainEditingModelObject object, ITextViewer viewer, int offset,
			String startString, String fullString);
	
	ICompletionProposal[] computeProposals(DomainEditingModel model,
			DomainEditingModelObject object, ITextViewer viewer, int offset,
			String startString, DomainEditingModelObject parentNode, String fullString);
}
