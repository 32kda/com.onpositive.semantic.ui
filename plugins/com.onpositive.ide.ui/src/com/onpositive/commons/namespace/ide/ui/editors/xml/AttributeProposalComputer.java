package com.onpositive.commons.namespace.ide.ui.editors.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModel;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentAttributeNode;
import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.DocumentationContribution;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.NamespaceModel;
import com.onpositive.semantic.language.model.NamespacesModel;

public class AttributeProposalComputer implements ICompletionProposalComputer {

	boolean isInside;

	public AttributeProposalComputer(boolean isInside) {
		this.isInside = isInside;
	}

	public ICompletionProposal[] computeProposals(DomainEditingModel model,
			DomainEditingModelObject object, ITextViewer viewer, int offset,
			String startString, String fullString) {
		int repalcamentLength = startString.length();
		if (fullString != null && !fullString.equals("")) repalcamentLength = fullString.length();
		final ArrayList<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
		NamespaceModel resolveNamespace;
		final String namespace = object.getNamespace();
		resolveNamespace = NamespacesModel.getInstance().resolveNamespace(
				namespace);
		final IDocumentAttributeNode[] nodeAttributes = object.getNodeAttributes();
		final HashSet<String> existingAttributes = new HashSet<String>();
		for (final IDocumentAttributeNode n : nodeAttributes) {
			existingAttributes.add(n.getAttributeName());
		}
		if (resolveNamespace != null) {
			ElementModel parentElement = null;

			parentElement = resolveNamespace.resolveElement(object
					.getLocalName());
			
			if (parentElement == null) return new ICompletionProposal[] {};
			
			final HashSet<AttributeModel> allProperties = parentElement
					.getAllProperties();
			HashSet<AttributeModel> restrictedAttributes = parentElement.getRestrictedAttributes();
			for (final AttributeModel m : allProperties) {
				final String name = m.getName();
				if (existingAttributes.contains(name)) {
					continue;
				}
				if (restrictedAttributes.contains(m)){
					continue;
				}
				if (name.startsWith(startString.trim())) {
					final String replace = name + "=\"\"";
					final int length = replace.length();
					final CompletionProposal proposal = new CompletionProposal(
							replace, offset - startString.length(), repalcamentLength
									, length - 1, SWTImageManager.getImage(m, null, null),
							name, null, this.getDescription(m));

					list.add(proposal);
				}
			}
			final String name = "xmlns";
			if (name.startsWith(startString.trim())) {
				final int length = name.length();
				final CompletionProposal proposal = new CompletionProposal(name,
						offset - startString.length(), repalcamentLength,
						length, SWTImageManager.getImage("", null,
								null), name, null, "Namespace declaration");

				list.add(proposal);
			}
		}
		final ICompletionProposal[] ls = new ICompletionProposal[list.size()];
		list.toArray(ls);
		Arrays.sort(ls, new Comparator<ICompletionProposal>() {

			public int compare(ICompletionProposal o1, ICompletionProposal o2) {
				String displayString = o1.getDisplayString();
				if (displayString != null)
					return displayString.compareTo(o2.getDisplayString());
				return -1;
			}
		});
		return ls;
	}

	private String getDescription(AttributeModel m) {
		final StringBuilder result = new StringBuilder();
		result.append("<b>");

		result.append("Attribute:");
		result.append(m.getName());
		if (m.isRequired() || m.isTranslatable()) {
			result.append('(');
			if (m.isRequired()) {
				result.append("required");
				if (m.isTranslatable()) {
					result.append(',');
				}
			}
			if (m.isTranslatable()) {
				result.append("tranlatable");
			}
			result.append(')');
		}
		result.append("<br/>Type: ");
		result.append(m.getType());
		result.append("<br/>");
		String group = m.getGroup();
		if (group.length() == 0) {
			group = "Not specified";
		}
		result.append("Group:" + group);
		final String name = m.getOwner().getName();
		result.append("<br> Declared in: " + name + "("
				+ m.getOwner().getModel().getUrl() + ")");
		result.append("</b>");
		result.append("<br/>");
		final String description = m.getDescription();
		if ((description != null) && (description.length() > 0)) {
			result.append("<b>Description:</b>" + description);
		}
		final DocumentationContribution documentationContribution = m
				.getDocumentationContribution();

		if (documentationContribution != null) {
			final String contents = documentationContribution.getContents();
			if ((contents != null) && (contents.trim().length() > 0)) {
				result.append("<b>Documentation:</b><br/>");
				result.append(contents);
			}
		}
		return result.toString();
	}

	
	public ICompletionProposal[] computeProposals(DomainEditingModel model,
			DomainEditingModelObject object, ITextViewer viewer, int offset,
			String startString, DomainEditingModelObject parentNode, String fullString)
	{
		return computeProposals(model,object,viewer,offset, startString, fullString);
	}

}
