package com.onpositive.semantic.ui.text.spelling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
/**
 * This class is used to collect proposals from several different {@link IContentAssistProcessor}'s
 * Use <b>addContentAssistProcessor(IContentAssistProcessor)</b> to add processors to this multi-processor
 * @author 32kda
 *
 */
public class MultiContentAssistProcessor implements IContentAssistProcessor {

	List <IContentAssistProcessor> additionalProcessors; 
	
	public MultiContentAssistProcessor() {
		additionalProcessors = new ArrayList<IContentAssistProcessor>();
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		final List<ICompletionProposal> initialProposals = new ArrayList<ICompletionProposal>();
		for (Iterator<IContentAssistProcessor> iterator = additionalProcessors.iterator(); iterator
				.hasNext();) {
			IContentAssistProcessor processor = (IContentAssistProcessor) iterator.next();
			ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, offset);			
			for (int i = 0; i < proposals.length; i++) {
				initialProposals.add(proposals[i]);
			}
		}
		return initialProposals.toArray(new ICompletionProposal[]{});
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Adds {@link IContentAssistProcessor} providing some completions to this multi processor
	 * @param processor {@link IContentAssistProcessor} to add
	 */
	public void addContentAssistProcessor(IContentAssistProcessor processor) {
		additionalProcessors.add(processor);
	}


	/**
	 * Removes {@link IContentAssistProcessor} from this multi processor
	 * @param processor {@link IContentAssistProcessor} to remove
	 */
	public void removeContentAssistProcessor(IContentAssistProcessor processor) {
		additionalProcessors.remove(processor);
	}


}
