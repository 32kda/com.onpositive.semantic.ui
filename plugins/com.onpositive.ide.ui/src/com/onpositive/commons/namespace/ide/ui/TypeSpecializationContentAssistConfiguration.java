package com.onpositive.commons.namespace.ide.ui;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.text.FindReplaceDocumentAdapterContentProposalProvider;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.ischema.ISchema;
import org.eclipse.pde.internal.core.schema.SchemaRegistry;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.onpositive.commons.contentassist.IContentProposalProvider2;
import com.onpositive.commons.contentassist.IHasContentAssist;
import com.onpositive.commons.contentassist.ProposalConversion;
import com.onpositive.commons.namespace.ide.ui.completion.BasicContentAssistConfiguration;
import com.onpositive.commons.namespace.ide.ui.completion.TypeContentProposalProvider;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.ui.generic.ContentProposal;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;
import com.onpositive.semantic.model.ui.generic.IContentProposal;
import com.onpositive.semantic.model.ui.generic.IContentProposalProvider;

public class TypeSpecializationContentAssistConfiguration extends
		BasicContentAssistConfiguration implements IContentAssistConfiguration {

	private final class TypeSpecContentProposalProvider implements
			IContentProposalProvider, IContentProposalProvider2 {

		IHasContentAssist owner;

		
		public IContentProposal[] getProposals(String contents, int position) {
			String value = (String) owner.getBinding().getParent()
					.getBinding("Type").getValue();
			ArrayList<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
			if (value.equals("string")) {
				FindReplaceDocumentAdapterContentProposalProvider d = new FindReplaceDocumentAdapterContentProposalProvider(
						true);
				org.eclipse.jface.fieldassist.IContentProposal[] proposals = d.getProposals(contents,position);
				
				return ProposalConversion.convertToModel(proposals);						
				
			}
			if (value.equals("java")) {
				final IEditorPart activeEditor = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				
					final IEditorInput editorInput = activeEditor.getEditorInput();
					IProject pr = null;
					if ((editorInput != null)
							&& (editorInput instanceof IFileEditorInput)) {
						final IFileEditorInput fl = (IFileEditorInput) editorInput;
						pr = fl.getFile().getProject();
					}
				final TypeContentProposalProvider processor = new TypeContentProposalProvider(
						pr, IJavaSearchConstants.TYPE);
				final String repl = contents;
				final IContentProposal[] computeCompletionProposals =ProposalConversion.convertToModel(processor
						.getProposals(repl, repl.length()));
				return computeCompletionProposals;
			}
			if (value.equals("extension")) {
				if (contents.length()>0&&contents.indexOf('/')!=-1){
					int indexOf = contents.indexOf('/');
					String substring = contents.substring(0,indexOf);
					IPluginExtensionPoint findExtensionPoint = PDECore.getDefault().getExtensionsRegistry().findExtensionPoint(substring);
					if (findExtensionPoint!=null){
						ISchema schema = PDECore.getDefault().getSchemaRegistry().getSchema(contents.substring(0,indexOf));
						ArrayList<IContentProposal>p=new ArrayList<IContentProposal>();
						for (String s:schema.getElementNames()){
							p.add(new ContentProposal(substring+'/'+s, s,s));
						}
						return p.toArray(new IContentProposal[p.size()]);
					}
				}
				final IEditorPart activeEditor = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().getActiveEditor();				
					final IEditorInput editorInput = activeEditor.getEditorInput();
					IProject pr = null;
					if ((editorInput != null)
							&& (editorInput instanceof IFileEditorInput)) {
						final IFileEditorInput fl = (IFileEditorInput) editorInput;
						pr = fl.getFile().getProject();

						IPluginModelBase[] models = PluginRegistry.getActiveModels();
						SchemaRegistry registry = PDECore.getDefault().getSchemaRegistry();
						ArrayList<IPluginExtensionPoint>rs=new ArrayList<IPluginExtensionPoint>();
						// cycle through all active plug-ins and their extension points
						for (int i = 0; i < models.length; i++) {
							IPluginExtensionPoint[] points = models[i].getPluginBase().getExtensionPoints();
							for (IPluginExtensionPoint p:points){
								if (p.getFullId().contains(contents)){
									rs.add(p);
								}
							}							
						}
						IContentProposal[]r=new IContentProposal[rs.size()];
						int a=0;
						for (IPluginExtensionPoint point:rs){
							
							ContentProposal p=new ContentProposal(point.getFullId()+'/',point.getName());							
							r[a++]=p;							
						}
						return r;
					}
			}
			IContentProposal[] rs=new IContentProposal[result.size()];
			int a=0;
			for (ICompletionProposal p:result){
				ContentProposal pr=new ContentProposal(p.getDisplayString(),p.getDisplayString(),p.getAdditionalProposalInfo());
				rs[a++]=pr;
			}
			return rs;
		}

		public void setContentAssistOwner(IHasContentAssist s) {
			this.owner = s;
		}
	}

	
	public com.onpositive.semantic.model.ui.generic.IContentProposalProvider getProposalProvider() {
		return new TypeSpecContentProposalProvider();
	}


	public ITextLabelProvider getProposalLabelProvider() {
		return null;
	}

	
	

}
