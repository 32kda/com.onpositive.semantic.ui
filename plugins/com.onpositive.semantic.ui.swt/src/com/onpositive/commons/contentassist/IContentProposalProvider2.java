package com.onpositive.commons.contentassist;

import com.onpositive.semantic.model.ui.generic.IContentProposalProvider;



public interface IContentProposalProvider2 extends IContentProposalProvider{

	void setContentAssistOwner(IHasContentAssist s);
}
