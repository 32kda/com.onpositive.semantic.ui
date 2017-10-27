package com.onpositive.internal.ui.text.spelling;

import org.eclipse.jface.text.IDocument;

public class ContentAssistInvocationContext {

	IDocument document;
	int offset;

	public ContentAssistInvocationContext(IDocument document, int offset) {
		super();
		this.document = document;
		this.offset = offset;
	}

	public IDocument getDocument() {
		return this.document;
	}

	public int getInvocationOffset() {
		return this.offset;
	}

}
