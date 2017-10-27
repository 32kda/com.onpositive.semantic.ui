/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.onpositive.commons.namespace.ide.ui.internal.core.text;

public class DocumentAttributeNode extends DocumentXMLNode implements
		IDocumentAttributeNode {

	private static final long serialVersionUID = 1L;

	private transient IDocumentElementNode fEnclosingElement;
	private transient int fNameOffset;
	private transient int fNameLength;
	private transient int fValueOffset;
	private transient int fValueLength;

	private String fValue;
	private String fName;

	/**
	 * 
	 */
	public DocumentAttributeNode() {
		this.fEnclosingElement = null;
		this.fNameOffset = -1;
		this.fNameLength = -1;
		this.fValueOffset = -1;
		this.fValueLength = -1;
		this.fValue = null;
		this.fName = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#getAttributeName
	 * ()
	 */
	public String getAttributeName() {
		return this.fName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#getAttributeValue
	 * ()
	 */
	public String getAttributeValue() {
		return this.fValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#getEnclosingElement
	 * ()
	 */
	public IDocumentElementNode getEnclosingElement() {
		return this.fEnclosingElement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#getNameLength()
	 */
	public int getNameLength() {
		return this.fNameLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#getNameOffset()
	 */
	public int getNameOffset() {
		return this.fNameOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#getValueLength
	 * ()
	 */
	public int getValueLength() {
		return this.fValueLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#getValueOffset
	 * ()
	 */
	public int getValueOffset() {
		return this.fValueOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#setAttributeName
	 * (java.lang.String)
	 */
	public void setAttributeName(String name) {
		this.fName = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#setAttributeValue
	 * (java.lang.String)
	 */
	public void setAttributeValue(String value) {
		this.fValue = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#setEnclosingElement
	 * (org.eclipse.pde.internal.core.text.IDocumentElementNode)
	 */
	public void setEnclosingElement(IDocumentElementNode node) {
		this.fEnclosingElement = node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#setNameLength
	 * (int)
	 */
	public void setNameLength(int length) {
		this.fNameLength = length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#setNameOffset
	 * (int)
	 */
	public void setNameOffset(int offset) {
		this.fNameOffset = offset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#setValueLength
	 * (int)
	 */
	public void setValueLength(int length) {
		this.fValueLength = length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#setValueOffset
	 * (int)
	 */
	public void setValueOffset(int offset) {
		this.fValueOffset = offset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.core.text.IDocumentAttributeNode#write()
	 */
	public String write() {
		return this.fName + "=\"" + //$NON-NLS-1$
				PDEXMLHelper.getWritableAttributeString(this.fValue) + "\""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentAttributeNode#reconnect(org
	 * .eclipse.pde.internal.core.text.IDocumentElementNode)
	 */
	public void reconnect(IDocumentElementNode parent) {
		// Transient field: Enclosing element
		// Essentially is the parent (an element)
		// Note: Parent field from plugin document node parent seems to be
		// null; but, we will set it any ways
		this.fEnclosingElement = parent;
		// Transient field: Name Length
		this.fNameLength = -1;
		// Transient field: Name Offset
		this.fNameOffset = -1;
		// Transient field: Value Length
		this.fValueLength = -1;
		// Transient field: Value Offset
		this.fValueOffset = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.core.text.IDocumentRange#getLength()
	 */
	public int getLength() {
		// Implemented for backwards compatibility with utility methods that
		// assume that an attribute is a document range.
		// Stems from the problem that attributes are considered as elements
		// in the hierarchy in the manifest model

		// Includes: name length + equal + start quote
		final int len1 = this.getValueOffset() - this.getNameOffset();
		// Includes: value length
		final int len2 = this.getValueLength();
		// Includes: end quote
		final int len3 = 1;
		// Total
		final int length = len1 + len2 + len3;

		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.core.text.IDocumentRange#getOffset()
	 */
	public int getOffset() {
		// Implemented for backwards compatibility with utility methods that
		// assume that an attribute is a document range.
		// Stems from the problem that attributes are considered as elements
		// in the hierarchy in the manifest model
		return this.getNameOffset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.core.text.IDocumentXMLNode#getXMLType()
	 */
	public int getXMLType() {
		return F_TYPE_ATTRIBUTE;
	}

}
