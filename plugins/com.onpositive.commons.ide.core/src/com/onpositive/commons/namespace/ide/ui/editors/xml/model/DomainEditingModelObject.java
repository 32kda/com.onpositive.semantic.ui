package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

import org.eclipse.pde.core.IModel;

import com.onpositive.commons.namespace.ide.ui.internal.core.text.DocumentObject;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentAttributeNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentElementNode;

public class DomainEditingModelObject extends DocumentObject {

	private String nameSpace;
	private String localName;

	public DomainEditingModelObject(IModel model, String tagName) {
		super(model, tagName);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void calculateNamespaces() {
		final int size = this.getChildCount();
		String tagName = this.getXMLTagName();
		String pref = "";
		final int index = tagName.indexOf(':');
		if (index != -1) {
			pref = tagName.substring(0, index);
			tagName = tagName.substring(index + 1);
		}
		this.localName = tagName;
		this.nameSpace = this.calcNamespace(pref);
		for (int a = 0; a < size; a++) {
			final DomainEditingModelObject ma = (DomainEditingModelObject) this
					.getChildAt(a);
			ma.calculateNamespaces();
		}
		final IDocumentAttributeNode[] nodeAttributes = this
				.getNodeAttributes();
		for (final IDocumentAttributeNode m : nodeAttributes) {
			final DomainAttributeNode ma = (DomainAttributeNode) m;
			ma.calculateNamespaces();
		}
	}

	String calcNamespace(String nameSpace2) {
		final IDocumentAttributeNode[] nodeAttributes = this
				.getNodeAttributes();
		for (final IDocumentAttributeNode n : nodeAttributes) {
			final String attributeName = n.getAttributeName();
			if (attributeName.startsWith("xmlns")) {
				if ((attributeName.length() == 5) && (nameSpace2.length() == 0)) {
					return n.getAttributeValue();
				}
				final int q = attributeName.indexOf(':');
				if (q != -1) {
					final String substring = attributeName.substring(q + 1);
					if (nameSpace2.equals(substring)) {
						return n.getAttributeValue();
					}
				}
			}
		}
		final IDocumentElementNode parentNode = this.getParentNode();
		if (parentNode != null) {
			final DomainEditingModelObject md = (DomainEditingModelObject) parentNode;
			return md.calcNamespace(nameSpace2);
		}
		return null;
	}

	public String getNamespace() {
		return this.nameSpace;
	}

	public String getLocalName() {
		return this.localName;
	}

	public void traverse(ModelVisitor visitor) {
		visitor.enterNode(this);
		final int size = this.getChildCount();

		for (int a = 0; a < size; a++) {
			final DomainEditingModelObject ma = (DomainEditingModelObject) this
					.getChildAt(a);
			ma.traverse(visitor);
		}
		IDocumentAttributeNode[] nodeAttributes = this.getNodeAttributes();
		for (int a=0;a<nodeAttributes.length;a++){
			visitor.visitAttribute((DomainAttributeNode) nodeAttributes[a]);
		}
		visitor.exitNode(this);
	}

	public DomainEditingModelObject findElement(int offset) {
		final int childCount = this.getChildCount();
		for (int a = 0; a < childCount; a++) {
			final DomainEditingModelObject childAt = (DomainEditingModelObject) this
					.getChildAt(a);
			final int offset2 = childAt.getOffset();
			if (offset2 > offset) {
				break;
			}
			if ((offset2 + childAt.getLength() > offset) //TODO Was >=
					&& (offset2 <= offset)) {
				return childAt.findElement(offset);
			}
		}
		return this;
	}

	public String resolvePrefix(String url) {
		final IDocumentAttributeNode[] nodeAttributes = this
				.getNodeAttributes();
		for (final IDocumentAttributeNode n : nodeAttributes) {
			final String attributeName = n.getAttributeName();
			if (attributeName.startsWith("xmlns")) {
				final int q = attributeName.indexOf(':');
				if (q != -1) {
					final String substring = attributeName.substring(q + 1);
					final String attributeValue = n.getAttributeValue();
					if (attributeValue.equals(url)) {
						return substring;
					}
				}
			}
		}
		final IDocumentElementNode parentNode = this.getParentNode();
		if (parentNode != null) {
			final DomainEditingModelObject md = (DomainEditingModelObject) parentNode;
			return md.resolvePrefix(url);
		}
		return null;
	}

	public DomainEditingModelObject getRoot() {
		if (getParentNode()!=null){
			return getParentNode().getRoot();
		}
		return this;		
	}
}