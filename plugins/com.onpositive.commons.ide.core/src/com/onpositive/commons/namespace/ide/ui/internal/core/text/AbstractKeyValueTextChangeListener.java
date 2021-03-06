/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
import org.eclipse.pde.internal.core.text.AbstractTextChangeListener;
import org.eclipse.pde.internal.core.text.IDocumentKey;
import org.eclipse.pde.internal.core.util.PropertiesUtil;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

public abstract class AbstractKeyValueTextChangeListener extends
		AbstractTextChangeListener {

	protected HashMap fReadableNames = null;

	public AbstractKeyValueTextChangeListener(IDocument document,
			boolean generateReadableNames) {
		super(document);
		if (generateReadableNames) {
			this.fReadableNames = new HashMap();
		}
	}

	public TextEdit[] getTextOperations() {
		if (this.fOperationTable.size() == 0) {
			return new TextEdit[0];
		}
		return (TextEdit[]) this.fOperationTable.values().toArray(
				new TextEdit[this.fOperationTable.size()]);
	}

	protected void insertKey(IDocumentKey key, String name) {
		final int offset = PropertiesUtil.getInsertOffset(this.fDocument);
		final InsertEdit edit = new InsertEdit(offset, key.write());
		this.fOperationTable.put(key, edit);
		if (this.fReadableNames != null) {
			this.fReadableNames.put(edit, name);
		}
	}

	protected void deleteKey(IDocumentKey key, String name) {
		if (key.getOffset() >= 0) {
			final DeleteEdit edit = new DeleteEdit(key.getOffset(), key.getLength());
			this.fOperationTable.put(key, edit);
			if (this.fReadableNames != null) {
				this.fReadableNames.put(edit, name);
			}
		}
	}

	protected void modifyKey(IDocumentKey key, String name) {
		if (key.getOffset() == -1) {
			this.insertKey(key, name);
		} else {
			final ReplaceEdit edit = new ReplaceEdit(key.getOffset(),
					key.getLength(), key.write());
			this.fOperationTable.put(key, edit);
			if (this.fReadableNames != null) {
				this.fReadableNames.put(edit, name);
			}
		}
	}

	public String getReadableName(TextEdit edit) {
		if ((this.fReadableNames != null) && this.fReadableNames.containsKey(edit)) {
			return (String) this.fReadableNames.get(edit);
		}
		return null;
	}
}
