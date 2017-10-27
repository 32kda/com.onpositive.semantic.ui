/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.onpositive.commons.namespace.ide.ui.internal.core.text;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.IWritable;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.util.SAXParserWrapper;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class XMLEditingModel extends AbstractEditingModel {

	public XMLEditingModel(IDocument document, boolean isReconciling) {
		super(document, isReconciling);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#load(java.io.InputStream, boolean)
	 */
	public void load(InputStream source, boolean outOfSync) {
		try {
			this.fLoaded = true;
			final SAXParserWrapper parser = new SAXParserWrapper();
			parser.parse(source, this.createDocumentHandler(this, false));
		} catch (final SAXException e) {
			// Activator.log(e);
			this.fLoaded = false;
		} catch (final IOException e) {
		} catch (final ParserConfigurationException e) {
		} catch (final FactoryConfigurationError e) {
		}
	}

	protected abstract DefaultHandler createDocumentHandler(IModel model,
			boolean reconciling);

	public void adjustOffsets(IDocument document) {
		try {
			final SAXParserWrapper parser = new SAXParserWrapper();
			parser.parse(this.getInputStream(document), this.createDocumentHandler(this,
					false));
		} catch (final SAXException e) {
		} catch (final IOException e) {
		} catch (final ParserConfigurationException e) {
		} catch (final FactoryConfigurationError e) {
		}
	}

	/**
	 * @return
	 */
	private boolean isResourceFile() {
		if (this.getUnderlyingResource() == null) {
			return false;
		} else if ((this.getUnderlyingResource() instanceof IFile) == false) {
			return false;
		}
		return true;
	}

	public void save() {
		if (this.isResourceFile() == false) {
			return;
		}
		try {
			final IFile file = (IFile) this.getUnderlyingResource();
			final String contents = this.getContents();
			final ByteArrayInputStream stream = new ByteArrayInputStream(contents
					.getBytes("UTF8")); //$NON-NLS-1$
			if (file.exists()) {
				file.setContents(stream, false, false, null);
			} else {
				file.create(stream, false, null);
			}
			stream.close();
		} catch (final CoreException e) {
			PDECore.logException(e);
		} catch (final IOException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.core.IWorkspaceModel#reload()
	 */
	public void reload() {
		if (this.isResourceFile() == false) {
			return;
		}
		final IFile file = (IFile) this.getUnderlyingResource();
		// Underlying file has to exist in order to reload the model
		if (file.exists()) {
			InputStream stream = null;
			try {
				// Get the file contents
				stream = new BufferedInputStream(file.getContents(true));
				// Load the model using the last saved file contents
				this.reload(stream, false);
				// Remove the dirty (*) indicator from the editor window
				this.setDirty(false);
			} catch (final CoreException e) {
				// Ignore
			}
		}
	}

	public void reload(IDocument document) {
		// Get the document's text
		final String text = document.get();
		InputStream stream = null;

		try {
			// Turn the document's text into a stream
			stream = new ByteArrayInputStream(text.getBytes("UTF8")); //$NON-NLS-1$
			// Reload the model using the stream
			this.reload(stream, false);
			// Remove the dirty (*) indicator from the editor window
			this.setDirty(false);
		} catch (final UnsupportedEncodingException e) {
			PDECore.logException(e);
		} catch (final CoreException e) {
			// Ignore
		}
	}

	public String getContents() {
		final StringWriter swriter = new StringWriter();
		final PrintWriter writer = new PrintWriter(swriter);
		this.setLoaded(true);
		this.save(writer);
		writer.flush();
		try {
			swriter.close();
		} catch (final IOException e) {
		}
		return swriter.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.AbstractEditingModel#save(java.io.
	 * PrintWriter)
	 */
	public void save(PrintWriter writer) {
		if (this.isLoaded()) {
			this.getRoot().write("", writer); //$NON-NLS-1$
		}
		this.setDirty(false);
	}

	protected abstract IWritable getRoot();
}