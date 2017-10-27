package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

import java.io.InputStream;

import org.eclipse.jface.text.IDocument;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.IModelChangeProvider;
import org.eclipse.pde.core.IModelChangedEvent;
import org.eclipse.pde.core.ModelChangedEvent;
import org.eclipse.pde.internal.core.NLResourceHelper;
import org.xml.sax.helpers.DefaultHandler;

import com.onpositive.commons.namespace.ide.ui.internal.core.text.DocumentAttributeNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.DocumentElementNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.DocumentTextNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentAttributeNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentElementNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentNodeFactory;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentTextNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.NodeDocumentHandler;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.XMLEditingModel;

@SuppressWarnings("restriction")
public class DomainEditingModel extends XMLEditingModel {

	DomainEditingModelObject root;

	public DomainEditingModel(IDocument document, boolean isReconciling) {
		super(document, isReconciling);
	}

	
	public void load(InputStream source, boolean outOfSync) {
		super.load(source, outOfSync);
		if (this.root != null) {
			this.root.calculateNamespaces();
		}
	}

	
	public void reload() {
		super.reload();
		if (this.root != null) {
			this.root.calculateNamespaces();
		}
	}

	protected void fireStructureChanged(DocumentElementNode node, int changeType) {
		final IModel model = this;
		if (model.isEditable() && (model instanceof IModelChangeProvider)) {
			final IModelChangedEvent e = new ModelChangedEvent(this,
					changeType, new Object[] { node }, null);
			this.fireModelChanged(e);
		}
	}

	
	protected DefaultHandler createDocumentHandler(IModel model,
			boolean reconciling) {
		return new NodeDocumentHandler(reconciling, new IDocumentNodeFactory() {

			public IDocumentAttributeNode createAttribute(String name,
					String value, IDocumentElementNode enclosingElement) {
				final DocumentAttributeNode documentAttributeNode = new DomainAttributeNode();
				documentAttributeNode.setEnclosingElement(enclosingElement);
				documentAttributeNode.setAttributeName(name);
				documentAttributeNode.setAttributeValue(value);
				enclosingElement.setXMLAttribute(documentAttributeNode);
				return documentAttributeNode;
			}

			public IDocumentElementNode createDocumentNode(String name,
					IDocumentElementNode parent) {
				final DomainEditingModelObject domainEditingModelObject = new DomainEditingModelObject(
						DomainEditingModel.this, name);
				if (DomainEditingModel.this.root == null) {
					DomainEditingModel.this.root = domainEditingModelObject;
				}
				if (parent != null) {
					parent.addChildNode(domainEditingModelObject);
				}
				return domainEditingModelObject;
			}

			public IDocumentTextNode createDocumentTextNode(String content,
					IDocumentElementNode parent) {
				final DocumentTextNode documentTextNode = new DocumentTextNode();
				documentTextNode.setText(content);
				if (parent != null) {
					parent.addTextNode(documentTextNode);
				}
				return documentTextNode;
			}

		}) {

			
			protected IDocument getDocument() {
				return DomainEditingModel.this.getDocument();
			}

			
			protected IDocumentElementNode getRootNode() {
				return DomainEditingModel.this.root;
			}

		};
	};

	
	public DomainEditingModelObject getRoot() {
		return this.root;
	}

	@SuppressWarnings("restriction")
	
	protected NLResourceHelper createNLResourceHelper() {
		return null;
	}
}