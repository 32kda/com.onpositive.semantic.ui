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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.IModelChangeProvider;
import org.eclipse.pde.core.IModelChangedEvent;
import org.eclipse.pde.core.ModelChangedEvent;

/**
 * DocumentObject
 * 
 */
public abstract class DocumentObject extends DocumentElementNode implements
		IDocumentObject {

	// TODO: MP: TEO: LOW: Integrate with plugin model?
	// TODO: MP: TEO: LOW: Investigate document node to see if any methods to
	// pull down

	private static final long serialVersionUID = 1L;

	private transient IModel fModel;

	private transient boolean fInTheModel;

	/**
	 * @param model
	 * @param tagName
	 */
	public DocumentObject(IModel model, String tagName) {
		super();

		this.fModel = model;
		this.fInTheModel = false;
		this.setXMLTagName(tagName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentObject#setSharedModel(org
	 * .eclipse.pde.core.IModel)
	 */
	public void setSharedModel(IModel model) {
		this.fModel = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.core.text.IDocumentObject#getSharedModel()
	 */
	public IModel getSharedModel() {
		return this.fModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.core.text.IDocumentObject#reset()
	 */
	public void reset() {
		// TODO: MP: TEO: LOW: Reset parent fields? or super.reset?
		this.fModel = null;
		this.fInTheModel = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.core.text.IDocumentObject#isInTheModel()
	 */
	public boolean isInTheModel() {
		return this.fInTheModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentObject#setInTheModel(boolean)
	 */
	public void setInTheModel(boolean inModel) {
		this.fInTheModel = inModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.core.text.IDocumentObject#isEditable()
	 */
	public boolean isEditable() {
		// Convenience method
		return this.fModel.isEditable();
	}

	/**
	 * @return
	 */
	protected boolean shouldFireEvent() {
		if (this.isInTheModel() && this.isEditable()) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IDocumentObject#getLineDelimiter()
	 */
	protected String getLineDelimiter() {
		if (this.fModel instanceof IEditingModel) {
			final IDocument document = ((IEditingModel) this.fModel).getDocument();
			return TextUtilities.getDefaultLineDelimiter(document);
		}
		return super.getLineDelimiter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.plugin.PluginDocumentNode#reconnect
	 * (org.eclipse.pde.core.plugin.ISharedPluginModel,
	 * org.eclipse.pde.internal.core.ischema.ISchema,
	 * org.eclipse.pde.internal.core.text.IDocumentElementNode)
	 */
	public void reconnect(IDocumentElementNode parent, IModel model) {
		super.reconnect(parent, model);
		// Transient field: In The Model
		// Value set to true when added to the parent; however, serialized
		// children's value remains unchanged. Since, reconnect and add calls
		// are made so close together, set value to true for parent and all
		// children
		this.fInTheModel = true;
		// Transient field: Model
		this.fModel = model;
	}

	/**
	 * @param property
	 * @param oldValue
	 * @param newValue
	 */
	protected void firePropertyChanged(String property, Object oldValue,
			Object newValue) {
		this.firePropertyChanged(this, property, oldValue, newValue);
	}

	/**
	 * @param object
	 * @param property
	 * @param oldValue
	 * @param newValue
	 */
	private void firePropertyChanged(Object object, String property,
			Object oldValue, Object newValue) {
		if (this.fModel.isEditable() && (this.fModel instanceof IModelChangeProvider)) {
			final IModelChangeProvider provider = (IModelChangeProvider) this.fModel;
			provider.fireModelObjectChanged(object, property, oldValue,
					newValue);
		}
	}

	/**
	 * @param child
	 * @param changeType
	 */
	protected void fireStructureChanged(Object child, int changeType) {
		this.fireStructureChanged(new Object[] { child }, changeType);
	}

	/**
	 * @param children
	 * @param changeType
	 */
	protected void fireStructureChanged(Object[] children, int changeType) {
		if (this.fModel.isEditable() && (this.fModel instanceof IModelChangeProvider)) {
			final IModelChangeProvider provider = (IModelChangeProvider) this.fModel;
			final IModelChangedEvent event = new ModelChangedEvent(provider,
					changeType, children, null);
			provider.fireModelChanged(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.plugin.PluginDocumentNode#addChildNode
	 * (org.eclipse.pde.internal.core.text.IDocumentElementNode)
	 */
	public void addChildNode(IDocumentElementNode child) {
		if (child instanceof IDocumentObject) {
			((IDocumentObject) child).setInTheModel(true);
		}
		super.addChildNode(child);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.plugin.PluginDocumentNode#addChildNode
	 * (org.eclipse.pde.internal.core.text.IDocumentElementNode, int)
	 */
	public void addChildNode(IDocumentElementNode child, int position) {
		// Ensure the position is valid
		// 0 <= position <= number of children
		if ((position < 0) || (position > this.getChildCount())) {
			return;
		}
		if (child instanceof IDocumentObject) {
			((IDocumentObject) child).setInTheModel(true);
		}
		super.addChildNode(child, position);
	}

	/**
	 * @param child
	 * @param position
	 * @param fireEvent
	 */
	public void addChildNode(IDocumentElementNode child, int position,
			boolean fireEvent) {
		this.addChildNode(child, position);
		// Fire event
		if (fireEvent && this.shouldFireEvent()) {
			this.fireStructureChanged(child, IModelChangedEvent.INSERT);
		}
	}

	/**
	 * @param child
	 * @param fireEvent
	 */
	public void addChildNode(IDocumentElementNode child, boolean fireEvent) {
		this.addChildNode(child);
		// Fire event
		if (fireEvent && this.shouldFireEvent()) {
			this.fireStructureChanged(child, IModelChangedEvent.INSERT);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.plugin.PluginDocumentNode#removeChildNode
	 * (org.eclipse.pde.internal.core.text.IDocumentElementNode)
	 */
	public IDocumentElementNode removeChildNode(IDocumentElementNode child) {
		final IDocumentElementNode node = super.removeChildNode(child);
		if ((node != null) && (node instanceof IDocumentObject)) {
			((IDocumentObject) node).setInTheModel(false);
		}
		return node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.plugin.PluginDocumentNode#removeChildNode
	 * (int)
	 */
	public IDocumentElementNode removeChildNode(int index) {
		final IDocumentElementNode node = super.removeChildNode(index);
		if ((node != null) && (node instanceof IDocumentObject)) {
			((IDocumentObject) node).setInTheModel(false);
		}
		return node;
	}

	/**
	 * @param child
	 * @param fireEvent
	 * @return
	 */
	public IDocumentElementNode removeChildNode(IDocumentElementNode child,
			boolean fireEvent) {
		final IDocumentElementNode node = this.removeChildNode(child);
		// Fire event
		if (fireEvent && this.shouldFireEvent()) {
			this.fireStructureChanged(child, IModelChangedEvent.REMOVE);
		}
		return node;
	}

	/**
	 * @param index
	 * @param clazz
	 * @param fireEvent
	 * @return
	 */
	public IDocumentElementNode removeChildNode(int index, Class clazz,
			boolean fireEvent) {
		final IDocumentElementNode node = this.removeChildNode(index, clazz);
		// Fire event
		if (fireEvent && this.shouldFireEvent()) {
			this.fireStructureChanged(node, IModelChangedEvent.REMOVE);
		}
		return node;
	}

	public IDocumentElementNode removeChildNode(int index, Class clazz) {
		// Validate index
		if ((index < 0) || (index >= this.getChildCount())
				|| (clazz.isInstance(this.getChildAt(index)) == false)) {
			// 0 <= index < child element count
			// Cannot remove a node that is not the specified type
			return null;
		}
		// Remove the node
		final IDocumentElementNode node = this.removeChildNode(index);
		return node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.cheatsheet.simple.SimpleCSObject#write
	 * (java.lang.String, java.io.PrintWriter)
	 */
	public void write(String indent, PrintWriter writer) {
		// Used for text transfers for copy, cut, paste operations
		writer.write(this.write(true));
	}

	/**
	 * @param newNode
	 * @param oldNode
	 */
	public void setChildNode(IDocumentElementNode newNode, Class clazz) {
		// Determine whether to fire the event
		final boolean fireEvent = this.shouldFireEvent();
		// Get the old node
		final IDocumentElementNode oldNode = this.getChildNode(clazz);

		if ((newNode == null) && (oldNode == null)) {
			// NEW = NULL, OLD = NULL
			// If the new and old nodes are not defined, nothing to do
			return;
		} else if (newNode == null) {
			// NEW = NULL, OLD = DEF
			// Remove the old node
			this.removeChildNode(oldNode, fireEvent);
		} else if (oldNode == null) {
			// NEW = DEF, OLD = NULL
			// Add the new node to the end of the list
			this.addChildNode(newNode, fireEvent);
		} else {
			// NEW = DEF, OLD = DEF
			this.replaceChildNode(newNode, oldNode, fireEvent);
		}
	}

	/**
	 * @param newNode
	 * @param oldNode
	 * @param fireEvent
	 */
	protected void replaceChildNode(IDocumentElementNode newNode,
			IDocumentElementNode oldNode, boolean fireEvent) {
		// Get the index of the old node
		final int position = this.indexOf(oldNode);
		// Validate position
		if (position < 0) {
			return;
		}
		// Add the new node to the same position occupied by the old node
		this.addChildNode(newNode, position, fireEvent);
		// Remove the old node
		this.removeChildNode(oldNode, fireEvent);
	}

	/**
	 * @param clazz
	 * @return
	 */
	public IDocumentElementNode getChildNode(Class clazz) {
		// Linear search O(n)
		final ArrayList children = this.getChildNodesList();
		final Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			final IDocumentElementNode node = (IDocumentElementNode) iterator.next();
			if (clazz.isInstance(node)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * @param clazz
	 * @return
	 */
	public int getChildNodeCount(Class clazz) {
		// Linear search O(n)
		int count = 0;
		final ArrayList children = this.getChildNodesList();
		final Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			final IDocumentElementNode node = (IDocumentElementNode) iterator.next();
			if (clazz.isInstance(node)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @param clazz
	 * @return
	 */
	public ArrayList getChildNodesList(Class clazz, boolean match) {
		return this.getChildNodesList(new Class[] { clazz }, match);
	}

	/**
	 * @param classes
	 * @return
	 */
	public ArrayList getChildNodesList(Class[] classes, boolean match) {
		final ArrayList filteredChildren = new ArrayList();
		final ArrayList children = this.getChildNodesList();
		final Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			final IDocumentElementNode node = (IDocumentElementNode) iterator.next();
			for (int i = 0; i < classes.length; i++) {
				final Class clazz = classes[i];
				if (clazz.isInstance(node) == match) {
					filteredChildren.add(node);
					break;
				}
			}
		}
		return filteredChildren;
	}

	/**
	 * @param node
	 * @param clazz
	 * @return
	 */
	public IDocumentElementNode getNextSibling(IDocumentElementNode node,
			Class clazz) {
		final int position = this.indexOf(node);
		final int lastIndex = this.getChildCount() - 1;
		if ((position < 0) || (position >= lastIndex)) {
			// Either the node was not found or the node was found but it is
			// at the last index
			return null;
		}
		// Get the next node of the given type
		for (int i = position + 1; i <= lastIndex; i++) {
			final IDocumentElementNode currentNode = this.getChildAt(i);
			if (clazz.isInstance(currentNode)) {
				return currentNode;
			}
		}
		return null;
	}

	/**
	 * @param node
	 * @param clazz
	 * @return
	 */
	public IDocumentElementNode getPreviousSibling(IDocumentElementNode node,
			Class clazz) {
		final int position = this.indexOf(node);
		if ((position <= 0) || (position >= this.getChildCount())) {
			// Either the item was not found or the item was found but it is
			// at the first index
			return null;
		}
		// Get the previous node of the given type
		for (int i = position - 1; i >= 0; i--) {
			final IDocumentElementNode currentNode = this.getChildAt(i);
			if (clazz.isInstance(currentNode)) {
				return currentNode;
			}
		}
		return null;
	}

	/**
	 * @param clazz
	 * @return
	 */
	public boolean hasChildNodes(Class clazz) {
		final ArrayList children = this.getChildNodesList();
		final Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			final IDocumentElementNode node = (IDocumentElementNode) iterator.next();
			if (clazz.isInstance(node)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param node
	 * @param clazz
	 * @return
	 */
	public boolean isFirstChildNode(IDocumentElementNode node, Class clazz) {
		final int position = this.indexOf(node);
		// Check to see if node is found
		if ((position < 0) || (position >= this.getChildCount())) {
			// Node not found
			return false;
		} else if (position == 0) {
			// Node found in the first position
			return true;
		}
		// Check to see if there is any node before the specified node of the
		// same type
		// Assertion: Position > 0
		for (int i = 0; i < position; i++) {
			if (clazz.isInstance(this.getChildAt(i))) {
				// Another node of the same type found before the specified node
				return false;
			}
		}
		// All nodes before the specified node were of a different type
		return true;
	}

	/**
	 * @param node
	 * @param clazz
	 * @return
	 */
	public boolean isLastChildNode(IDocumentElementNode node, Class clazz) {
		final int position = this.indexOf(node);
		final int lastIndex = this.getChildCount() - 1;
		// Check to see if node is found
		if ((position < 0) || (position > lastIndex)) {
			// Node not found
			return false;
		} else if (position == lastIndex) {
			// Node found in the last position
			return true;
		}
		// Check to see if there is any node after the specified node of the
		// same type
		// Assertion: Position < lastIndex
		for (int i = position + 1; i <= lastIndex; i++) {
			if (clazz.isInstance(this.getChildAt(i))) {
				// Another node of the same type found after the specified node
				return false;
			}
		}
		// All nodes after the specified node were of a different type
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.plugin.PluginDocumentNode#swap(org
	 * .eclipse.pde.internal.core.text.IDocumentElementNode,
	 * org.eclipse.pde.internal.core.text.IDocumentElementNode)
	 */
	public void swap(IDocumentElementNode child1, IDocumentElementNode child2,
			boolean fireEvent) {
		super.swap(child1, child2);
		// Fire event
		if (fireEvent && this.shouldFireEvent()) {
			this.firePropertyChanged(this,
					IDocumentElementNode.F_PROPERTY_CHANGE_TYPE_SWAP, child1,
					child2);
		}
	}

	/**
	 * @param node
	 * @param newRelativeIndex
	 */
	public void moveChildNode(IDocumentElementNode node, int newRelativeIndex,
			boolean fireEvent) {

		// TODO: MP: TEO: MED: Test Problem, if generic not viewable, may appear
		// that node did not move
		// TODO: MP: TEO: MED: Test relative index > 1 or < -1
		// TODO: MP: TEO: MED: BUG: Add item to end, move existing item before
		// it down, teo overwrites new item

		if (newRelativeIndex == 0) {
			// Nothing to do
			return;
		}
		// Get the current index of the node
		final int currentIndex = this.indexOf(node);
		// Ensure the node exists
		if (currentIndex == -1) {
			return;
		}
		// Calculate the new index
		final int newIndex = newRelativeIndex + currentIndex;
		// Validate the new index
		// 0 <= newIndex < child element count
		if ((newIndex < 0) || (newIndex >= this.getChildCount())) {
			return;
		}
		// If we are only moving a node up and down one position use a swap
		// operation. Otherwise, delete the node, clone it and then re-insert
		// the node
		if ((newRelativeIndex == -1) || (newRelativeIndex == 1)) {
			final IDocumentElementNode sibling = this.getChildAt(newIndex);
			// Ensure sibling exists
			if (sibling == null) {
				return;
			}
			this.swap(node, sibling, fireEvent);
		} else {
			// Remove the node
			this.removeChildNode(node, fireEvent);
			// Clone the node
			// Needed to create a text edit operation that inserts a new element
			// rather than replacing the old one
			final IDocumentElementNode clone = this.clone(node);
			// Removing the node and moving it to a positive relative index
			// alters
			// the indexing for insertion; however, this pads the new relative
			// index by 1, allowing it to be inserted one position after as
			// desired
			// Add the node back at the specified index
			this.addChildNode(clone, newIndex, fireEvent);
		}
	}

	/**
	 * @param node
	 * @return
	 */
	public IDocumentElementNode clone(IDocumentElementNode node) {
		IDocumentElementNode clone = null;
		try {
			// Serialize
			final ByteArrayOutputStream bout = new ByteArrayOutputStream();
			final ObjectOutputStream out = new ObjectOutputStream(bout);
			out.writeObject(node);
			out.flush();
			out.close();
			final byte[] bytes = bout.toByteArray();
			// Deserialize
			final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
			final ObjectInputStream in = new ObjectInputStream(bin);
			clone = (IDocumentElementNode) in.readObject();
			in.close();
			// Reconnect
			clone.reconnect(this, this.fModel);
		} catch (final IOException e) {
			clone = null;
		} catch (final ClassNotFoundException e) {
			clone = null;
		}

		return clone;
	}

	/**
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public boolean getBooleanAttributeValue(String name, boolean defaultValue) {
		final String value = this.getXMLAttributeValue(name);
		if (value == null) {
			return defaultValue;
		} else if (value.equalsIgnoreCase(ATTRIBUTE_VALUE_TRUE)) {
			return true;
		} else if (value.equalsIgnoreCase(ATTRIBUTE_VALUE_FALSE)) {
			return false;
		}
		return defaultValue;
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public boolean setBooleanAttributeValue(String name, boolean value) {
		final String newValue = Boolean.valueOf(value).toString();
		return this.setXMLAttribute(name, newValue);
	}

	/**
	 * @param name
	 * @param newValue
	 */
	public boolean setXMLAttribute(String name, String newValue) {
		final String oldValue = this.getXMLAttributeValue(name);
		final boolean changed = super.setXMLAttribute(name, newValue);
		// Fire an event if in the model
		if (changed && this.shouldFireEvent()) {
			this.firePropertyChanged(name, oldValue, newValue);
		}
		return changed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.plugin.PluginDocumentNode#setXMLContent
	 * (java.lang.String)
	 */
	public boolean setXMLContent(String text) {
		String oldText = null;
		// Get old text node
		final IDocumentTextNode node = this.getTextNode();
		if (node == null) {
			// Text does not exist
			oldText = ""; //$NON-NLS-1$
		} else {
			// Text exists
			oldText = node.getText();
		}
		final boolean changed = super.setXMLContent(text);

		// Fire an event
		if (changed && this.shouldFireEvent()) {
			this.firePropertyChanged(node,
					IDocumentTextNode.F_PROPERTY_CHANGE_TYPE_PCDATA, oldText,
					text);
		}
		return changed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.plugin.PluginDocumentNode#getFileEncoding
	 * ()
	 */
	protected String getFileEncoding() {
		if ((this.fModel != null) && (this.fModel instanceof IEditingModel)) {
			return ((IEditingModel) this.fModel).getCharset();
		}
		return super.getFileEncoding();
	}

}
