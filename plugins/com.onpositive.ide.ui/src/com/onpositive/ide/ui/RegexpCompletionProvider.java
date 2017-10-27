package com.onpositive.ide.ui;

import java.util.ArrayList;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.text.FindReplaceDocumentAdapterContentProposalProvider;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeCompletionProvider;

public class RegexpCompletionProvider implements ITypeCompletionProvider {

	public void fillProposals(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String startString, int lengthCompletion,
			ArrayList<ICompletionProposal> result, String fullString,
			boolean addBraces,String typeSpec) {
		FindReplaceDocumentAdapterContentProposalProvider d = new FindReplaceDocumentAdapterContentProposalProvider(
				true);
		IContentProposal[] proposals = d.getProposals(startString, startString.length());
		for (IContentProposal p : proposals) {			
			Image image=SWTImageManager.getImage("com.onpositive.commons.namespace.ide.ui.rename");
			result.add(new CompletionProposal(p.getContent(), offset
					, 0, p.getContent().length(),image,p.getLabel(),null,p.getDescription()));
		}
	}

}
