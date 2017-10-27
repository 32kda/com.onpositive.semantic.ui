/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.onpositive.commons.namespace.ide.ui.completion;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class TypeCompletionProposal implements ICompletionProposal,
		ICompletionProposalExtension3, ICompletionProposalExtension5 {

	protected String fReplacementString;
	protected Image fImage;
	protected String fDisplayString;
	protected int fBeginInsertPoint;
	protected int fLength;
	protected String fAdditionalInfo;
	private IInformationControlCreator fCreator;

	public TypeCompletionProposal(String replacementString, Image image,
			String displayString) {
		this(replacementString, image, displayString, 0, 0);
	}

	public TypeCompletionProposal(String replacementString, Image image,
			String displayString, int startOffset, int length) {
		Assert.isNotNull(replacementString);

		this.fReplacementString = replacementString;
		this.fImage = image;
		this.fDisplayString = displayString;
		this.fBeginInsertPoint = startOffset;
		this.fLength = length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse
	 * .jface.text.IDocument)
	 */
	public void apply(IDocument document) {
		if (this.fLength == -1) {
			final String current = document.get();
			this.fLength = current.length();
		}
		try {
			document.replace(this.fBeginInsertPoint, this.fLength, this.fReplacementString);
		} catch (final BadLocationException e) {
			// DEBUG
			// Activator.log(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jface.text.contentassist.ICompletionProposal#
	 * getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		// No additional proposal information
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jface.text.contentassist.ICompletionProposal#
	 * getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		// No context information
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString
	 * ()
	 */
	public String getDisplayString() {
		return this.fDisplayString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return this.fImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection
	 * (org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document) {
		if (this.fReplacementString.equals("\"\"")) {
			return new Point(this.fBeginInsertPoint + 1, 0);
		}
		return new Point(this.fBeginInsertPoint + this.fReplacementString.length(), 0);
	}

	/**
	 * @return
	 */
	public String getReplacementString() {
		return this.fReplacementString;
	}

	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		return this.fAdditionalInfo;
	}

	public void setAdditionalProposalInfo(String info) {
		this.fAdditionalInfo = info;
	}

	public IInformationControlCreator getInformationControlCreator() {		

		if (this.fCreator == null) {
			this.fCreator = new AbstractReusableInformationControlCreator() {

				/*
				 * @seeorg.eclipse.jdt.internal.ui.text.java.hover.
				 * AbstractReusableInformationControlCreator
				 * #doCreateInformationControl(org.eclipse.swt.widgets.Shell)
				 */
				public IInformationControl doCreateInformationControl(
						Shell parent) {
					return new BrowserInformationControl(parent,JFaceResources.DEFAULT_FONT,true);
				}
			};
		}
		return this.fCreator;
	}

	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return this.fBeginInsertPoint;
	}

	public CharSequence getPrefixCompletionText(IDocument document,
			int completionOffset) {
		return this.fReplacementString;
	}

	
	public String toString()
	{
		return fDisplayString;
	}
}