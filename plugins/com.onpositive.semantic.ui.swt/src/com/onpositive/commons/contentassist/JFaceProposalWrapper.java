package com.onpositive.commons.contentassist;

import org.eclipse.jface.fieldassist.IContentProposal;

public class JFaceProposalWrapper implements com.onpositive.semantic.model.ui.generic.IContentProposal{

	IContentProposal proposal;

	public JFaceProposalWrapper(IContentProposal proposal) {
		super();
		this.proposal = proposal;
	}

	public String getContent() {
		return proposal.getContent();
	}

	public int getCursorPosition() {
		return proposal.getCursorPosition();
	}

	public String getLabel() {
		return proposal.getLabel();
	}

	public String getDescription() {
		return proposal.getDescription();
	}

	public Object getElement() {
		return null;
	}
}
