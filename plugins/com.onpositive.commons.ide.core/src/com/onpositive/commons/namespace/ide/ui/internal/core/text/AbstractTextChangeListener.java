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
package com.onpositive.commons.namespace.ide.ui.internal.core.text;

import java.util.HashMap;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.pde.internal.core.text.IModelTextChangeListener;

public abstract class AbstractTextChangeListener implements
		IModelTextChangeListener {

	protected HashMap fOperationTable = new HashMap();
	protected IDocument fDocument;
	protected String fSep;

	public AbstractTextChangeListener(IDocument document) {
		this.fDocument = document;
		this.fSep = TextUtilities.getDefaultLineDelimiter(this.fDocument);
	}

}
