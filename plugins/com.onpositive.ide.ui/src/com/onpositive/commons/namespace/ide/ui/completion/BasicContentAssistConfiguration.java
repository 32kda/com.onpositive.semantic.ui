package com.onpositive.commons.namespace.ide.ui.completion;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;

public class BasicContentAssistConfiguration {

	public BasicContentAssistConfiguration() {
		super();
	}

	public char[] getAutoactivationCharacters() {
		return null;
	}

	public int getFilterStyle() {
		return ContentProposalAdapter.FILTER_NONE;
	}

	public int getProposalAcceptanceStyle() {
		return ContentProposalAdapter.PROPOSAL_REPLACE;
	}

}