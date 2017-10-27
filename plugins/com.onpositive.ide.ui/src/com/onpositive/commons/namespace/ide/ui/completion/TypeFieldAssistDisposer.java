/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.onpositive.commons.namespace.ide.ui.completion;

import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

/**
 * TypeFieldAssistDisposer
 * 
 */
public class TypeFieldAssistDisposer {

	private final ContentAssistCommandAdapter fAdapter;

	private final TypeContentProposalListener fListener;

	/**
	 * 
	 */
	public TypeFieldAssistDisposer(ContentAssistCommandAdapter adapter,
			TypeContentProposalListener listener) {
		this.fAdapter = adapter;
		this.fListener = listener;
	}

	/**
	 * 
	 */
	public void dispose() {
		if (this.fAdapter == null) {
			return;
		}
		// Dispose of the label provider
		final ILabelProvider labelProvider = this.fAdapter.getLabelProvider();
		if ((labelProvider != null)) {
			this.fAdapter.setLabelProvider(null);
			labelProvider.dispose();
		}
		// Remove the listeners
		if (this.fListener != null) {
			this.fAdapter
					.removeContentProposalListener((IContentProposalListener) this.fListener);
			this.fAdapter
					.removeContentProposalListener((IContentProposalListener2) this.fListener);
		}
	}

}
