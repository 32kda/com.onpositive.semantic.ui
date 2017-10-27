package com.onpositive.commons.contentassist;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class ProposalConversion {

	public static IContentProposal[] convert(
			com.onpositive.semantic.model.ui.generic.IContentProposal[] pr) {
		IContentProposal[] result = new IContentProposal[pr.length];
		int a = 0;
		for (com.onpositive.semantic.model.ui.generic.IContentProposal p : pr) {
			if (p instanceof JFaceProposalWrapper){
				result[a++]=((JFaceProposalWrapper) p).proposal;
				continue;
			}
			BasicContentProposal b = new BasicContentProposal(p.getContent(),
					p.getLabel(), p.getDescription(), p.getCursorPosition(),
					p.getElement());
			result[a++]=b;
		}
		return result;

	}
	
	public static com.onpositive.semantic.model.ui.generic.IContentProposal[] convertToModel(IContentProposal[] prop)
	{
		com.onpositive.semantic.model.ui.generic.IContentProposal[] result=new com.onpositive.semantic.model.ui.generic.IContentProposal[prop.length];
		int a=0;
		for (IContentProposal p:prop){
			
			result[a++]=new JFaceProposalWrapper(p);
		}
		return result;
	}
	
	public static IContentProposalProvider2 convertProvider(final IContentProposalProvider prop){
		return new IContentProposalProvider2() {
			
			public com.onpositive.semantic.model.ui.generic.IContentProposal[] getProposals(
					String contents, int position) {
				IContentProposal[] proposals = prop.getProposals(contents, position);
				return convertToModel(proposals);
			}
			
			public void setContentAssistOwner(IHasContentAssist s) {
				
			}
		};		
	}
}
