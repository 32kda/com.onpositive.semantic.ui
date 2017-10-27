package com.onpositive.ide.ui;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.onpositive.commons.namespace.ide.ui.completion.TypeContentProposal;
import com.onpositive.commons.namespace.ide.ui.completion.TypeContentProposalProvider;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeCompletionProvider;

public class JavaTypeValueProvider implements ITypeCompletionProvider {

	public JavaTypeValueProvider() {
	}

	
	public void fillProposals(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String startString, int lengthCompletion,
			ArrayList<ICompletionProposal> result, String fullString, boolean addBraces,String typeSpec) {
		
		final IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (activeEditor != null) {
			final IEditorInput editorInput = activeEditor.getEditorInput();
			IProject pr = null;
			if ((editorInput != null)
					&& (editorInput instanceof IFileEditorInput)) {
				final IFileEditorInput fl = (IFileEditorInput) editorInput;
				pr = fl.getFile().getProject();
			}

			final TypeContentProposalProvider processor = new TypeContentProposalProvider(
					pr, IJavaSearchConstants.CLASS
							| IJavaSearchConstants.PACKAGE);
			final String repl = startString.length()>0&&startString.charAt(0)=='"'?startString.substring(1):startString;
			
			final IContentProposal[] computeCompletionProposals = processor
					.getProposals(repl, repl.length());
			if (computeCompletionProposals != null) {
				for (final IContentProposal p : computeCompletionProposals) {
					final CompletionProposal pa = new CompletionProposal(p
							.getContent(), offset - repl.length(), repl
							.length(), p.getContent().length(),
							((TypeContentProposal) p).getImage(), p.getLabel(),
							null, p.getDescription()!=null?p.getDescription():"");
					result.add(pa);

				}
			}
		}
	}

}
