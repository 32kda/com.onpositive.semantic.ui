package com.onpositive.commons.contentassist;

import com.onpositive.semantic.model.binding.IBinding;

public interface IHasContentAssist {

	String getSeparatorCharacters();

	IBinding getBinding();

	String getContentAssistRole();

	String getTheme();

	ContentProposalAdapter getContentAssist();

	
}
