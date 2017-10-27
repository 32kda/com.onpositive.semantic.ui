package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.api.labels.ITextLabelProvider;


public interface IContentAssistConfiguration {

	char[] getAutoactivationCharacters();

	IContentProposalProvider getProposalProvider();

	ITextLabelProvider getProposalLabelProvider();

	int getProposalAcceptanceStyle();

	int getFilterStyle();

}
