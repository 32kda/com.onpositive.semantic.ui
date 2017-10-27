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
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.pde.core.IModelChangedEvent;
import org.eclipse.pde.core.IModelChangedListener;
import org.eclipse.pde.core.ModelChangedEvent;
import org.eclipse.pde.internal.core.IModelChangeProviderExtension;
import org.eclipse.pde.internal.core.IModelChangedListenerFilter;
import org.eclipse.pde.internal.core.NLResourceHelper;
import org.eclipse.pde.internal.core.text.IEditingModel;
import org.eclipse.pde.internal.core.text.IModelTextChangeListener;

public abstract class AbstractEditingModel extends PlatformObject implements
		IEditingModel, IModelChangeProviderExtension {
	private final ArrayList fListeners = new ArrayList();
	protected boolean fReconciling;
	protected boolean fInSync = true;
	protected boolean fLoaded = false;
	protected boolean fDisposed;
	protected long fTimestamp;
	private transient NLResourceHelper fNLResourceHelper;
	private final IDocument fDocument;
	private boolean fDirty;
	private String fCharset;
	private IResource fUnderlyingResource;
	private String fInstallLocation;
	private boolean fStale;

	public AbstractEditingModel(IDocument document, boolean isReconciling) {
		this.fDocument = document;
		this.fReconciling = isReconciling;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#dispose()
	 */
	public void dispose() {
		if (this.fNLResourceHelper != null) {
			this.fNLResourceHelper.dispose();
			this.fNLResourceHelper = null;
		}
		this.fDisposed = true;
		this.fListeners.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#getResourceString(java.lang.String)
	 */
	public String getResourceString(String key) {
		if ((key == null) || (key.length() == 0)) {
			return ""; //$NON-NLS-1$
		}

		if (this.fNLResourceHelper == null) {
			this.fNLResourceHelper = this.createNLResourceHelper();
		}

		return (this.fNLResourceHelper == null) ? key : this.fNLResourceHelper
				.getResourceString(key);
	}

	protected abstract NLResourceHelper createNLResourceHelper();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#isDisposed()
	 */
	public boolean isDisposed() {
		return this.fDisposed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#isEditable()
	 */
	public boolean isEditable() {
		return this.fReconciling;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#isLoaded()
	 */
	public boolean isLoaded() {
		return this.fLoaded;
	}

	/**
	 * @param loaded
	 */
	public void setLoaded(boolean loaded) {
		// TODO: MP: TEO: LOW: Set as API?
		this.fLoaded = loaded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#isInSync()
	 */
	public boolean isInSync() {
		return this.fInSync;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#isValid()
	 */
	public boolean isValid() {
		return this.isLoaded();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#getTimeStamp()
	 */
	public final long getTimeStamp() {
		return this.fTimestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#load()
	 */
	public final void load() throws CoreException {
		try {
			final IDocument document = this.getDocument();
			if (document != null) {
				this.load(this.getInputStream(document), false);
			}
		} catch (final UnsupportedEncodingException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#reload(java.io.InputStream, boolean)
	 */
	public final void reload(InputStream source, boolean outOfSync)
			throws CoreException {
		this.load(source, outOfSync);
		this.fireModelChanged(new ModelChangedEvent(this,
				IModelChangedEvent.WORLD_CHANGED, new Object[] { this }, null));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#isReconcilingModel()
	 */
	public boolean isReconcilingModel() {
		return this.fReconciling;
	}

	public IDocument getDocument() {
		return this.fDocument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.ui.neweditor.text.IReconcilingParticipant#reconciled
	 * (org.eclipse.jface.text.IDocument)
	 */
	public final void reconciled(IDocument document) {
		if (this.isReconcilingModel()) {
			try {
				if (this.isStale()) {
					this.adjustOffsets(document);
					this.setStale(false);
				} else {
					this.reload(this.getInputStream(document), false);
				}
			} catch (final UnsupportedEncodingException e) {
			} catch (final CoreException e) {
			}
			if (this.isDirty()) {
				this.setDirty(false);
			}
		}
	}

	public abstract void adjustOffsets(IDocument document) throws CoreException;

	protected InputStream getInputStream(IDocument document)
			throws UnsupportedEncodingException {
		return new BufferedInputStream(new ByteArrayInputStream(document.get()
				.getBytes(this.getCharset())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.core.text.IEditingModel#getCharset()
	 */
	public String getCharset() {
		return this.fCharset != null ? this.fCharset : "UTF-8"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.internal.core.text.IEditingModel#setCharset(java.lang
	 * .String)
	 */
	public void setCharset(String charset) {
		this.fCharset = charset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.core.IModelChangeProvider#addModelChangedListener(org
	 * .eclipse.pde.core.IModelChangedListener)
	 */
	public void addModelChangedListener(IModelChangedListener listener) {
		if (!this.fListeners.contains(listener)) {
			this.fListeners.add(listener);
		}
	}

	public void transferListenersTo(IModelChangeProviderExtension target,
			IModelChangedListenerFilter filter) {
		final List oldList = (List) this.fListeners.clone();
		for (int i = 0; i < oldList.size(); i++) {
			final IModelChangedListener listener = (IModelChangedListener) oldList
					.get(i);
			if ((filter == null) || filter.accept(listener)) {
				// add the listener to the target
				target.addModelChangedListener(listener);
				// remove the listener from our list
				this.fListeners.remove(listener);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.core.IModelChangeProvider#fireModelChanged(org.eclipse
	 * .pde.core.IModelChangedEvent)
	 */
	public void fireModelChanged(IModelChangedEvent event) {
		if ((event.getChangeType() == IModelChangedEvent.CHANGE)
				&& (event.getOldValue() != null)
				&& event.getOldValue().equals(event.getNewValue())) {
			return;
		}
		this.setDirty(event.getChangeType() != IModelChangedEvent.WORLD_CHANGED);
		for (int i = 0; i < this.fListeners.size(); i++) {
			((IModelChangedListener) this.fListeners.get(i)).modelChanged(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.core.IModelChangeProvider#fireModelObjectChanged(java
	 * .lang.Object, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void fireModelObjectChanged(Object object, String property,
			Object oldValue, Object newValue) {
		this.fireModelChanged(new ModelChangedEvent(this, object, property,
				oldValue, newValue));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.core.IModelChangeProvider#removeModelChangedListener(
	 * org.eclipse.pde.core.IModelChangedListener)
	 */
	public void removeModelChangedListener(IModelChangedListener listener) {
		this.fListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IEditable#isDirty()
	 */
	public boolean isDirty() {
		return this.fDirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IEditable#save(java.io.PrintWriter)
	 */
	public void save(PrintWriter writer) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IEditable#setDirty(boolean)
	 */
	public void setDirty(boolean dirty) {
		this.fDirty = dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.ui.model.IEditingModel#isStale()
	 */
	public boolean isStale() {
		return this.fStale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.ui.model.IEditingModel#setStale(boolean)
	 */
	public void setStale(boolean stale) {
		this.fStale = stale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#getUnderlyingResource()
	 */
	public IResource getUnderlyingResource() {
		return this.fUnderlyingResource;
	}

	public void setUnderlyingResource(IResource resource) {
		this.fUnderlyingResource = resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.plugin.ISharedPluginModel#getInstallLocation()
	 */
	public String getInstallLocation() {
		if ((this.fInstallLocation == null) && (this.fUnderlyingResource != null)) {
			final IPath path = this.fUnderlyingResource.getProject().getLocation();
			return path != null ? path.addTrailingSeparator().toString() : null;
		}
		return this.fInstallLocation;
	}

	public void setInstallLocation(String location) {
		this.fInstallLocation = location;
	}

	public IModelTextChangeListener getLastTextChangeListener() {
		for (int i = this.fListeners.size() - 1; i >= 0; i--) {
			final Object obj = this.fListeners.get(i);
			if (obj instanceof IModelTextChangeListener) {
				return (IModelTextChangeListener) obj;
			}
		}
		return null;
	}

}