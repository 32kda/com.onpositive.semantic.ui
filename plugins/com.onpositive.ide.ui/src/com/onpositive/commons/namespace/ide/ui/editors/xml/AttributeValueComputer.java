package com.onpositive.commons.namespace.ide.ui.editors.xml;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.namespace.ide.ui.completion.ExpressionComplitionProvider;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.CompletionProviderRegistry;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModel;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeCompletionProvider;
import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.NamespaceModel;
import com.onpositive.semantic.language.model.NamespacesModel;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.ui.roles.ImageManager;

public class AttributeValueComputer {

	private final String attributeName;

	public AttributeValueComputer(String name) {
		this.attributeName = name;
	}

	/**
	 * Used to compute proposal list for attribute value
	 * 
	 * @param model
	 *            {@link DomainEditingModel}
	 * @param findElement
	 *            finded element
	 * @param viewer
	 *            {@link ITextViewer} component link
	 * @param offset
	 *            offset, where completion was called
	 * @param startString
	 *            piece of string, after or inside that completion was called.
	 *            F.e. cen|ter (| means cursor pos) "cen" must be passed here
	 * @param lengthCompletion
	 *            don't know yet
	 * @param fullString
	 *            full string, inside that completion was called. F.e. cen|ter
	 *            (| means cursor pos) "center" must be passed here
	 * @return
	 */
	public ICompletionProposal[] computeProposals(DomainEditingModel model,
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String startString, int lengthCompletion,
			String fullString) {
		boolean needToAddBraces = true;
		String trim = startString.trim();
		if (startString.startsWith("\"")) // Really detects, is we inside or
											// outside attribute value braces
											// ("")
		{ // if true, means that we are inside braces and don't need to
			// remove/add again them in completion
			fullString = dequoteString(fullString);
			trim = startString.trim().substring(1);
			needToAddBraces = false;
		}
		final ArrayList<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		if (this.attributeName.startsWith("xmlns")) {
			final IRealm<NamespaceModel> models = NamespacesModel.getInstance()
					.getModels();
			for (final NamespaceModel m : models) {
				String name = m.getUrl() + "\"";
				if (name.startsWith(trim)) {
					// TODO see, howitworx if (needToAddBraces) name = "\"" +
					// name + "\"";
					final CompletionProposal proposal = new CompletionProposal(
							name, offset - startString.length() + 1,
							lengthCompletion - 1, lengthCompletion - 2,
							SWTImageManager.getImage(m, null, null), name,
							null, m.getDescription());

					result.add(proposal);
				}
			}
		} else {
			if (findElement != null) {
			
				final DomainEditingModelObject parentNode = findElement;
//				final ArrayList<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
				NamespaceModel resolveNamespace;
				final String namespace = findElement != null ? findElement
						.getNamespace() : null;
				resolveNamespace = NamespacesModel.getInstance()
						.resolveNamespace(namespace);

				ElementModel parentElement = null;

				parentElement = resolveNamespace != null ? resolveNamespace
						.resolveElement(parentNode.getLocalName()) : null;
				if (parentElement != null) {
					
					final HashSet<AttributeModel> allProperties = parentElement
							.getAllProperties();
					for (final AttributeModel m : allProperties) {
						if (m.getName().equals(this.attributeName)) {
							
							if ( m.supportsExpressions()){
								//do custom expression completion here
								ExpressionComplitionProvider expressionProvider = new ExpressionComplitionProvider() ;
								expressionProvider.fillProposals(
										this.attributeName, findElement,
										viewer, offset, trim, lengthCompletion,
										result, fullString, needToAddBraces,
										m.getTypeSpecialization(),m.getType());						
								
							}
							final String type = m.getType();
							final ITypeCompletionProvider completionProvider = CompletionProviderRegistry
									.getCompletionProvider(type);
							
							if (completionProvider != null) {
								completionProvider.fillProposals(
										this.attributeName, findElement,
										viewer, offset, trim, lengthCompletion,
										result, fullString, needToAddBraces,
										m.getTypeSpecialization());
							}
						}
					}
				}
			}
		}
		final ICompletionProposal[] ps = new ICompletionProposal[result.size()];
		result.toArray(ps);
		return ps;
	}

	private String dequoteString(String str) {
		if (str == null)
			return str;
		if (str.length() > 0 && str.charAt(0) == '"')
			str = str.substring(1);
		if (str.length() > 0 && str.charAt(str.length() - 1) == '"')
			str = str.substring(0, str.length() - 1);

		return str;
	}
	
	
	

}
