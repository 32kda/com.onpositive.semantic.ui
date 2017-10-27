package com.onpositive.ide.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PDEExtensionRegistry;
import org.eclipse.swt.graphics.Image;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;

@SuppressWarnings("restriction")
public class ExtensionElementCompletionProposal {

	protected String extensionPointId;
	protected String elementName;

	public ExtensionElementCompletionProposal(String extensionPointId,
			String elementName) {
		super();
		this.extensionPointId = extensionPointId;
		this.elementName = elementName;
	}

	
	public void fillProposals(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String startString, int lengthCompletion,
			ArrayList<ICompletionProposal> result, String fullString,
			boolean addBraces,String typeSpec) {
		PDEExtensionRegistry extensionsRegistry = PDECore.getDefault()
				.getExtensionsRegistry();
		IExtension[] findExtensions = extensionsRegistry.findExtensions(
				extensionPointId, false);

		for (IExtension e : findExtensions) {
			IConfigurationElement[] configurationElements = e
					.getConfigurationElements();
			for (IConfigurationElement el : configurationElements) {
				String attribute = el.getAttribute("id");
				if (el.getName().equals(elementName)){
				if (attribute != null && attribute.startsWith(startString)) {
					Image d = SWTImageManager
							.getImage("com.onpositive.commons.namespace.ide.ui.namespace");
					String d1 = attribute+" - "+el.getContributor().getName();
					IContextInformation d2 = new ContextInformation(
							SWTImageManager
									.getImage("com.onpositive.commons.namespace.ide.ui.element"),
							d1, ""+d1);
					
					String d3 = d1;
					ICompletionProposal completionProposal = createCompletionProposal(
							offset, startString, attribute, d, d1, d2, d3,el);					
					result.add(completionProposal);

				}
				}
			}
		}
	}


	protected ICompletionProposal createCompletionProposal(int offset,
			String startString, String attribute, Image d, String d1,
			IContextInformation d2, String d3, IConfigurationElement el) {
		CompletionProposal completionProposal = new CompletionProposal(attribute, offset - startString.length(),
				startString.length(), offset + attribute.length()
						- startString.length(), d, d1,
				d2, d3);
		return completionProposal;
	}

	
}
