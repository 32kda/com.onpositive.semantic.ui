package com.onpositive.commons.namespace.ide.ui.completion;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;

import com.onpositive.commons.contentassist.ProposalConversion;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;

public class JavaContentAssistConfiguration extends
		BasicContentAssistConfiguration implements IContentAssistConfiguration {

	IProject project;

	
	public ITextLabelProvider getProposalLabelProvider() {
		return new TypeProposalLabelProvider();
	}

	
	public com.onpositive.semantic.model.ui.generic.IContentProposalProvider getProposalProvider() {
		return ProposalConversion.convertProvider(new TypeContentProposalProvider(this.project,
				IJavaSearchConstants.CLASS));
	}

	public IProject getProject() {
		return this.project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
