package com.onpositive.ide.ui;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeCompletionProvider;

@SuppressWarnings("restriction")
public class ImageCompletionProvider extends ExtensionElementCompletionProposal implements ITypeCompletionProvider {


	private final class ImageCompletionProposal extends CompletionProposal implements ICompletionProposalExtension3{
		public IConfigurationElement el;

		private ImageCompletionProposal(String replacementString,
				int replacementOffset, int replacementLength,
				int cursorPosition, Image image, String displayString,
				IContextInformation contextInformation,
				String additionalProposalInfo) {
			super(replacementString, replacementOffset, replacementLength,
					cursorPosition, image, displayString, contextInformation,
					additionalProposalInfo);
		}

		public IInformationControlCreator getInformationControlCreator() {
			return new IInformationControlCreator() {
				
				public IInformationControl createInformationControl(Shell parent) {
					BrowserInformationControl.isAvailable(parent);
					IContributor contributor = el.getContributor();
					IPluginModelBase findModel = PDECore.getDefault().getModelManager().findModel(contributor.getName());
					//el.
					String installLocation = findModel.getInstallLocation();
					if (installLocation!=null){
						if (installLocation.endsWith(".jar")){
							
						}
						else{
							String replace = installLocation.replace('\\', '/');
							if(replace.charAt(replace.length()-1)!='/'){
								replace+='/';
							}
							String string = "<img src='file://"+replace+el.getAttribute("image")+"'/>";
							ImageCompletionProposal.this.fAdditionalProposalInfo=string;
						}
					}
					return new BrowserInformationControl(parent, JFaceResources.DEFAULT_FONT, true){
						@Override
						public void setInput(Object input) {
							super.setInput(new BrowserInformationControlInput(null) {
								public String getHtml() {
									return fAdditionalProposalInfo;
								}

								public String getInputName() {
									return ""; //$NON-NLS-1$
								}

								public Object getInputElement() {
									return fAdditionalProposalInfo;
								}
							});
						}
					};
				}
			};
		}

		public CharSequence getPrefixCompletionText(IDocument document,
				int completionOffset) {
			return null;
		}

		public int getPrefixCompletionStart(IDocument document,
				int completionOffset) {
			return 0;
		}
	}

	public ImageCompletionProvider() {
		super("com.onpositive.semantic.model.images","image");
	}	

	protected ImageCompletionProposal createCompletionProposal(int offset,
			String startString, String attribute, Image d, String d1,
			IContextInformation d2, String d3, IConfigurationElement el) {
		ImageCompletionProposal completionProposal = new ImageCompletionProposal(attribute, offset - startString.length(),
				startString.length(), offset + attribute.length()
						- startString.length(), d, d1,
				d2, d3);
		completionProposal.el=el;
		return completionProposal;
	}


}
