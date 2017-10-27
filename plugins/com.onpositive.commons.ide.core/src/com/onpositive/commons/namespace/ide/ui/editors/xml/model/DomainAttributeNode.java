/**
 * 
 */
package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

import com.onpositive.commons.namespace.ide.ui.internal.core.text.DocumentAttributeNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentRange;

@SuppressWarnings("serial")
public final class DomainAttributeNode extends DocumentAttributeNode implements
		IDocumentRange {

	private String namespace;
	private String localName;

	public DomainAttributeNode() {

	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getLocalName() {
		return this.localName;
	}

	public void calculateNamespaces() {
		String tagName = this.getAttributeName();
		String pref = "";
		final int index = tagName.indexOf(':');
		if (index != -1) {
			pref = tagName.substring(0, index);
			tagName = tagName.substring(index + 1);
		}
		this.localName = tagName;
		this.namespace = ((DomainEditingModelObject) this.getEnclosingElement())
				.calcNamespace(pref);
	}

}