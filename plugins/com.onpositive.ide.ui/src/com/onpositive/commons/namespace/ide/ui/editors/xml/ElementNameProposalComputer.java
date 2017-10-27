package com.onpositive.commons.namespace.ide.ui.editors.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModel;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.DocumentationContribution;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.ModelElement;
import com.onpositive.semantic.language.model.NamespaceModel;
import com.onpositive.semantic.language.model.NamespacesModel;

public class ElementNameProposalComputer implements ICompletionProposalComputer {

	boolean isInside;
	
	protected String addAtBeginningStr = "";
	protected boolean needToCloseTag = true;

	public ElementNameProposalComputer(boolean isInside) {
		this.isInside = isInside;
	}

	public ICompletionProposal[] computeProposals(DomainEditingModel model,
			DomainEditingModelObject object, ITextViewer viewer, int offset,
			String startString, String fullString) 
	{
		final DomainEditingModelObject parentNode = object != null ? (DomainEditingModelObject) object
				.getParentNode()
				: null;
		return computeProposals(model,object,viewer,offset,startString,parentNode, fullString);
	}
	
	
	public ICompletionProposal[] computeProposals(DomainEditingModel model,
			DomainEditingModelObject object, ITextViewer viewer, int offset,
			String startString, DomainEditingModelObject parentNode, String fullString)
	{
		if ((startString.length() > 0) && (startString.charAt(0) == '<')) {
			startString = startString.substring(1);
		}
		int replacementLength;
		if (fullString != null && fullString.length() > 0) replacementLength = fullString.length();
		else replacementLength = startString.length();
		
		final ArrayList<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
		NamespaceModel resolveNamespace;
		final ArrayList<ElementModel> candidates = new ArrayList<ElementModel>();
		final String namespace = object != null ? object.getNamespace() : null;
		resolveNamespace = NamespacesModel.getInstance().resolveNamespace(
				namespace);

		final ArrayList<ModelElement> members = NamespacesModel.getInstance()
				.getAllElements();
		ElementModel parentElement = null;
		if (this.isInside) {			
			if (parentNode != null)  //TODO Непонятная пурга. Всё одинаково...
			parentElement = resolveNamespace != null ? resolveNamespace
					.resolveElement(parentNode.getLocalName()) : null;
		} else if (parentNode != null) {
			parentElement = resolveNamespace != null ? resolveNamespace
					.resolveElement(parentNode.getLocalName()) : null;
		}

		for (final ModelElement e : members) {
			if (e instanceof ElementModel) {
				final ElementModel el = (ElementModel) e;
				final String url = el.getModel().getUrl();

				String name = this.getDisplayString(el);
				if (object != null) {
					final String resolvePrefix = this.resolvePrefix(object, url);
					if ((resolvePrefix != null) && (resolvePrefix.length() > 0)) {
						name = resolvePrefix + ":" + name;
					}
				} else {
					name += " xmlns=\"" + url + "\"";
				}
				if (name.startsWith(startString)) {
					if (el.isAbstract()) {
						continue;
					}
					if (parentElement != null) {
						if (!parentElement.isAllowedChild(el)) {
							continue;
						}
					}
					candidates.add(el);
				}
			}
		}
		for (final ElementModel m : candidates) {
			final String url = m.getModel().getUrl();
			final ArrayList<AttributeModel> requiredAttributes = m
					.getRequiredAttributes();
			if ((object != null) && !url.equals(object.getNamespace())) {
				final String resolvePrefix = object.resolvePrefix(url);
				if (resolvePrefix != null) {
					final String name = this.getDisplayString(m);

					String replacementString = resolvePrefix + ":"
							+ m.getName();
					replacementString = this.update(replacementString,
							requiredAttributes, m);
					final CompletionProposal proposal = new CompletionProposal(
							replacementString, offset - startString.length(),
							replacementLength, replacementString.length(),
							SWTImageManager.getImage(m, null, null),
							name, null, this.getDescription(m));
					list.add(proposal);
				}
				else{
					final String name = this.getDisplayString(m);

					String replacementString = m.getName();
					replacementString = this.update(replacementString+" xmlns=\""+m.getModel().getUrl()+"\"",
							requiredAttributes, m) ;
					final CompletionProposal proposal = new CompletionProposal(
							replacementString, offset - startString.length(),
							replacementLength, replacementString.length(),
							SWTImageManager.getImage(m, null, null),
							name, null, this.getDescription(m));
					list.add(proposal);
				}
			} else {
				final String name = this.getDisplayString(m);
				if (object == null) {

					String replacementString = m.getName() + " xmlns=\"" + url
							+ "\"";
					replacementString = this.update(replacementString,
							requiredAttributes, m);
					final CompletionProposal proposal = new CompletionProposal(
							replacementString, offset - startString.length(),
							replacementLength, replacementString.length(),
							SWTImageManager.getImage(m, null, null),
							name, null, this.getDescription(m));
					list.add(proposal);
				} else {
					final CompletionProposal proposal = new CompletionProposal(
							this.update(m.getName(), requiredAttributes, m), offset
									- startString.length(), replacementLength, m.getName().length(),
							SWTImageManager.getImage(m, null, null),
							name, null, this.getDescription(m));
					list.add(proposal);
				}
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
	


	private String update(String replacementString,
			ArrayList<AttributeModel> requiredAttributes, ElementModel m) {
		replacementString = addAtBeginningStr + replacementString;
		if (!requiredAttributes.isEmpty()) {
			final StringBuilder bld = new StringBuilder(replacementString);
			for (final AttributeModel ma : requiredAttributes) {
				bld.append(' ');
				bld.append(ma.getName());
				bld.append('=');
				bld.append('"');
				bld.append('"');
			}
			replacementString = bld.toString();
		}
		if (!Character.isWhitespace(replacementString.charAt(replacementString
				.length() - 1))) {
			replacementString += ' ';
		}
		if (needToCloseTag)
		{
			if (m.mayHaveChilds()) {
				replacementString += ">";
			} else {
				replacementString += "/>";
			}
		}
		return replacementString;
	}

	private String getDescription(ElementModel m) {
		final StringBuilder result = new StringBuilder();
		result.append("<b>");
		result.append("Element:");
		result.append(m.getName());
		String group = m.getGroup();
		if (group.length() == 0) {
			group = "Not specified";
		}
		result.append("<br/>");
		result.append("Group:" + group);
		// String name = m.getOwner().getName();
		result.append("<br> Declared in: " + m.getModel().getUrl());
		result.append("</b>");
		result.append("<br/>");
		final String description = m.getDescription();
		if ((description != null) && (description.length() > 0)) {
			result.append("<b>Description:</b><br/>"+description);
			result.append("<br/>");
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

	private String getDisplayString(ElementModel m) {
		return m.getName() + "(" + m.getModel().getUrl() + ")";
	}

	private String resolvePrefix(DomainEditingModelObject object, String url) {
		return object.resolvePrefix(url);
	}
	
	/**
	 * @return the addAtBeginningStr
	 */
	public String getAddAtBeginningStr()
	{
		return addAtBeginningStr;
	}
	
	/**
	 * @param addAtBeginningStr the addAtBeginningStr to set
	 */
	public void setAddAtBeginningStr(String addAtBeginningStr)
	{
		this.addAtBeginningStr = addAtBeginningStr;
	}

	
	/**
	 * @return the needToCloseTag
	 */
	public boolean isNeedToCloseTag()
	{
		return needToCloseTag;
	}

	
	/**
	 * @param needToCloseTag the needToCloseTag to set
	 */
	public void setNeedToCloseTag(boolean needToCloseTag)
	{
		this.needToCloseTag = needToCloseTag;
	}


}
