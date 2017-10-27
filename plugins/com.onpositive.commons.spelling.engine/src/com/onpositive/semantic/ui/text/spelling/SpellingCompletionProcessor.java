package com.onpositive.semantic.ui.text.spelling;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.onpositive.internal.ui.text.spelling.ContentAssistInvocationContext;
import com.onpositive.internal.ui.text.spelling.WordCompletionProposalComputer;

public class SpellingCompletionProcessor extends MultiContentAssistProcessor {

	WordCompletionProposalComputer computer = new WordCompletionProposalComputer();
	List <IContentAssistProcessor> additionalProcessors; 
	
	public SpellingCompletionProcessor() {
		additionalProcessors = new ArrayList<IContentAssistProcessor>();
	}
	
	
	@SuppressWarnings("unchecked")
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		final ContentAssistInvocationContext context = new ContentAssistInvocationContext(
				viewer.getDocument(), offset);
		ICompletionProposal[] multiroposals = super.computeCompletionProposals(viewer, offset);
		List<ICompletionProposal> initialProposals = new ArrayList<ICompletionProposal>();
		for (int i = 0; i < multiroposals.length; i++)
		{
			initialProposals.add(multiroposals[i]);
		}
		initialProposals.addAll(this.computer.computeCompletionProposals(context, null));
		//System.out.println(initialProposals);
		final ICompletionProposal[] prs = new ICompletionProposal[initialProposals
				.size()];
		initialProposals.toArray(prs);
		return prs;
	}

	
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	
	public String getErrorMessage() {
		return null;
	}	
	
	/**
	 * @return the computer
	 */
	public WordCompletionProposalComputer getComputer()
	{
		return computer;
	}



}
