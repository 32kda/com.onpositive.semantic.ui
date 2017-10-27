/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.onpositive.commons.namespace.ide.ui.internal.core.text;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.pde.internal.core.util.PDEXMLHelper;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class DocumentHandler extends DefaultHandler {

	protected FindReplaceDocumentAdapter fFindReplaceAdapter;
	protected Stack fDocumentNodeStack = new Stack();
	protected int fHighestOffset = 0;
	private Locator fLocator;
	private IDocumentElementNode fLastError;
	private final boolean fReconciling;

	public DocumentHandler(boolean reconciling) {
		this.fReconciling = reconciling;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		this.fDocumentNodeStack.clear();
		this.fHighestOffset = 0;
		this.fLastError = null;
		this.fFindReplaceAdapter = new FindReplaceDocumentAdapter(this
				.getDocument());
	}

	protected IDocumentElementNode getLastParsedDocumentNode() {
		if (this.fDocumentNodeStack.isEmpty()) {
			return null;
		}
		return (IDocumentElementNode) this.fDocumentNodeStack.peek();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		final IDocumentElementNode parent = this.getLastParsedDocumentNode();
		final IDocumentElementNode node = this.getDocumentNode(qName, parent);
		try {
			final IDocument doc = this.getDocument();
			int nodeOffset=0;
			try{
			nodeOffset = this.getStartOffset(qName);
			node.setOffset(nodeOffset);
			final int line = doc.getLineOfOffset(nodeOffset);
			node.setLineIndent(node.getOffset() - doc.getLineOffset(line));
			}catch (BadLocationException e) {
				node.setOffset(0);
			}
			

			// create attributes
			for (int i = 0; i < attributes.getLength(); i++) {
				final String attName = attributes.getQName(i);
				String attValue = attributes.getValue(i);
				final IDocumentAttributeNode attribute = this
						.getDocumentAttribute(attName, attValue, node);
				if (attribute != null) {
					IRegion region = this.getAttributeRegion(attName, attValue,
							nodeOffset);
					if (region == null) {
						attValue = PDEXMLHelper.getWritableString(attValue);
						region = this.getAttributeRegion(attName, attValue,
								nodeOffset);
					}
					if (region != null) {
						attribute.setNameOffset(region.getOffset());
						attribute.setNameLength(attName.length());
						attribute.setValueOffset(region.getOffset()
								+ region.getLength() - 1 - attValue.length());
						attribute.setValueLength(attValue.length());
					}
					node.setXMLAttribute(attribute);
				}
			}
			this.removeOrphanAttributes(node);
		} catch (final BadLocationException e) {
		}
		if ((parent != null) && (node != null)
				&& (node.getParentNode() == null)) {
			if (this.fReconciling) {
				// find right place for the child
				// this is necessary to save as much as possible from the model
				// we do not want an xml element with one tag to overwrite an
				// element
				// with a different tag
				int position = 0;
				final IDocumentElementNode[] children = parent.getChildNodes();
				for (; position < children.length; position++) {
					if (children[position].getOffset() == -1) {
						break;
					}
				}
				parent.addChildNode(node, position);
			} else {
				parent.addChildNode(node);
			}
		}
		this.fDocumentNodeStack.push(node);
	}

	protected abstract IDocumentElementNode getDocumentNode(String name,
			IDocumentElementNode parent);

	protected abstract IDocumentAttributeNode getDocumentAttribute(String name,
			String value, IDocumentElementNode parent);

	protected abstract IDocumentTextNode getDocumentTextNode(String content,
			IDocumentElementNode parent);

	private int getStartOffset(String elementName) throws BadLocationException {
		final int line = this.fLocator.getLineNumber();
		int col = this.fLocator.getColumnNumber();
		final IDocument doc = this.getDocument();
		if (col < 0) {
			col = doc.getLineLength(line);
		}
		final String text = doc.get(this.fHighestOffset + 1, doc
				.getLineOffset(line)
				- this.fHighestOffset - 1);

		final ArrayList commentPositions = new ArrayList();
		for (int idx = 0; idx < text.length();) {
			idx = text.indexOf("<!--", idx); //$NON-NLS-1$
			if (idx == -1) {
				break;
			}
			final int end = text.indexOf("-->", idx); //$NON-NLS-1$
			if (end == -1) {
				break;
			}

			commentPositions.add(new Position(idx, end - idx));
			idx = end + 1;
		}

		int idx = 0;
		for (; idx < text.length(); idx += 1) {
			idx = text.indexOf("<" + elementName, idx); //$NON-NLS-1$
			if (idx == -1) {
				break;
			}
			boolean valid = true;
			for (int i = 0; i < commentPositions.size(); i++) {
				final Position pos = (Position) commentPositions.get(i);
				if (pos.includes(idx)) {
					valid = false;
					break;
				}
			}
			if (valid) {
				break;
			}
		}
		if (idx > -1) {
			this.fHighestOffset += idx + 1;
		}
		return this.fHighestOffset;
	}

	private int getElementLength(IDocumentElementNode node, int line, int column)
			throws BadLocationException {
		int endIndex = node.getOffset();
		final IDocument doc = this.getDocument();
		final int start = Math.max(doc.getLineOffset(line), node.getOffset());
		column = doc.getLineLength(line);
		final String lineText = doc.get(start, column - start
				+ doc.getLineOffset(line));

		int index = lineText.indexOf("</" + node.getXMLTagName() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		if (index == -1) {
			index = lineText.indexOf(">"); //$NON-NLS-1$
			if (index == -1) {
				endIndex = column;
			} else {
				endIndex = index + 1;
			}
		} else {
			endIndex = index + node.getXMLTagName().length() + 3;
		}
		return start + endIndex - node.getOffset();
	}

	private IRegion getAttributeRegion(String name, String value, int offset)
			throws BadLocationException {
		final IRegion nameRegion = this.fFindReplaceAdapter.find(offset, name
				+ "\\s*=\\s*\"", true, true, false, true); //$NON-NLS-1$
		if (nameRegion != null) {
			if (this.getDocument().get(
					nameRegion.getOffset() + nameRegion.getLength(),
					value.length()).equals(value)) {
				return new Region(nameRegion.getOffset(), nameRegion
						.getLength()
						+ value.length() + 1);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (this.fDocumentNodeStack.isEmpty()) {
			return;
		}

		final IDocumentElementNode node = (IDocumentElementNode) this.fDocumentNodeStack
				.pop();
		try {
			node.setLength(this.getElementLength(node, this.fLocator
					.getLineNumber() - 1, this.fLocator.getColumnNumber()));
			this.setTextNodeOffset(node);
		} catch (final BadLocationException e) {
		}
		this.removeOrphanElements(node);
	}

	protected void setTextNodeOffset(IDocumentElementNode node)
			throws BadLocationException {
		final IDocumentTextNode textNode = node.getTextNode();
		if ((textNode != null) && (textNode.getText() != null)) {
			if (textNode.getText().trim().length() == 0) {
				node.removeTextNode();
				return;
			}
			final IDocument doc = this.getDocument();
			final String text = doc.get(node.getOffset(), node.getLength());
			// 1st char of text node
			int relativeStartOffset = text.indexOf('>') + 1;
			// last char of text node
			int relativeEndOffset = text.lastIndexOf('<') - 1;

			if ((relativeStartOffset < 0)
					|| (relativeStartOffset >= text.length())) {
				return;
			} else if ((relativeEndOffset < 0)
					|| (relativeEndOffset >= text.length())) {
				return;
			}

			// trim whitespace
			while (Character.isWhitespace(text.charAt(relativeStartOffset))) {
				relativeStartOffset += 1;
			}
			while (Character.isWhitespace(text.charAt(relativeEndOffset))) {
				relativeEndOffset -= 1;
			}

			textNode.setOffset(node.getOffset() + relativeStartOffset);
			textNode.setLength(relativeEndOffset - relativeStartOffset + 1);
			textNode.setText(textNode.getText().trim());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException
	 * )
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		this.generateErrorElementHierarchy();
	}

	/**
	 * 
	 */
	private void generateErrorElementHierarchy() {
		while (!this.fDocumentNodeStack.isEmpty()) {
			final IDocumentElementNode node = (IDocumentElementNode) this.fDocumentNodeStack
					.pop();
			node.setIsErrorNode(true);
			this.removeOrphanAttributes(node);
			this.removeOrphanElements(node);
			if (this.fLastError == null) {
				this.fLastError = node;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException e) throws SAXException {
		this.generateErrorElementHierarchy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator
	 * )
	 */
	public void setDocumentLocator(Locator locator) {
		this.fLocator = locator;
	}

	protected abstract IDocument getDocument();

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException {
		// Prevent the resolution of external entities in order to
		// prevent the parser from accessing the Internet
		// This will prevent huge workbench performance degradations and hangs
		return new InputSource(new StringReader("")); //$NON-NLS-1$
	}

	public IDocumentElementNode getLastErrorNode() {
		return this.fLastError;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (!this.fReconciling || this.fDocumentNodeStack.isEmpty()) {
			return;
		}

		final IDocumentElementNode parent = (IDocumentElementNode) this.fDocumentNodeStack
				.peek();
		final StringBuffer buffer = new StringBuffer();
		buffer.append(ch, start, length);
		this.getDocumentTextNode(buffer.toString(), parent);
	}

	private void removeOrphanAttributes(IDocumentElementNode node) {
		// when typing by hand, one element may overwrite a different existing
		// one
		// remove all attributes from previous element, if any.
		if (this.fReconciling) {
			final IDocumentAttributeNode[] attrs = node.getNodeAttributes();
			for (int i = 0; i < attrs.length; i++) {
				if (attrs[i].getNameOffset() == -1) {
					node.removeDocumentAttribute(attrs[i]);
				}
			}
		}
	}

	private void removeOrphanElements(IDocumentElementNode node) {
		// when typing by hand, one element may overwrite a different existing
		// one
		// remove all excess children elements, if any.
		if (this.fReconciling) {
			final IDocumentElementNode[] children = node.getChildNodes();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getOffset() == -1) {
					node.removeChildNode(children[i]);
				}
			}
		}
	}

	protected boolean isReconciling() {
		return this.fReconciling;
	}

}
