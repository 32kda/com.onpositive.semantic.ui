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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ListIterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.graphics.Image;

/**
 * TypeContentProposalProvider
 * 
 */
public class TypeContentProposalProvider extends TypePackageCompletionProcessor
		implements IContentProposalProvider {

	public static final char F_DOT = '.';

	private final IProject fProject;

	private final int fTypeScope;

	private ArrayList fInitialContentProposals;

	private String fInitialContent;

	private final Comparator fComparator;

	/**
	 * 
	 */
	public TypeContentProposalProvider(IProject project, int scope) {
		this.fProject = project;
		this.fTypeScope = scope;
		this.fComparator = new TypeComparator();

		this.reset();
	}

	/**
	 * TypeComparator
	 * 
	 */
	private static class TypeComparator implements Comparator {

		/**
		 * 
		 */
		public TypeComparator() {
			// NO-OP
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object arg0, Object arg1) {
			final String proposalSortKey1 = ((IContentProposal) arg0)
					.getLabel();
			final String proposalSortKey2 = ((IContentProposal) arg1)
					.getLabel();
			return proposalSortKey1.compareToIgnoreCase(proposalSortKey2);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.fieldassist.IContentProposalProvider#getProposals(java
	 * .lang.String, int)
	 */
	public IContentProposal[] getProposals(String contents, int position) {
		// Generate a list of proposals based on the current contents
		ArrayList currentContentProposals = null;
		// Determine method to obtain proposals based on current field contents
		if (position == 0) {
			// If the document offset is at the 0 position (i.e. no input
			// entered),
			// do not perform content assist. The operation is too expensive
			// because all classes and interfaces (depending on the specified
			// scope)
			// will need to be resolved as proposals
			currentContentProposals = null;
		} else if ((this.fInitialContentProposals == null)
				|| (contents.length() < this.fInitialContent.length())
				|| (this.endsWithDot(contents))) {
			// Generate new proposals if the content assist session was just
			// started
			// Or generate new proposals if the current contents of the field
			// is less than the initial contents of the field used to
			// generate the original proposals; thus, widening the search
			// scope. This can occur when the user types backspace
			// Or generate new proposals if the current contents ends with a
			// dot
			currentContentProposals = this.generateContentProposals(contents);
		} else {
			// Filter existing proposals from a prevous search; thus, narrowing
			// the search scope. This can occur when the user types additional
			// characters in the field causing new characters to be appended to
			// the initial field contents
			currentContentProposals = this.filterContentProposals(contents);
		}

		return this.convertResultsToSortedProposals(currentContentProposals);
	}

	/**
	 * 
	 */
	public void reset() {
		this.fInitialContentProposals = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.pde.internal.ui.editor.contentassist.
	 * TypePackageCompletionProcessor
	 * #addProposalToCollection(java.util.Collection, int, int,
	 * java.lang.String, java.lang.String, org.eclipse.swt.graphics.Image)
	 */
	protected void addProposalToCollection(Collection collection,
			int startOffset, int length, String label, String content,
			Image image) {
		// Create content proposals for field assist
		// start offset and length not required
		final IContentProposal proposal = new TypeContentProposal(label,
				content, null, image);
		// Add the proposal to the list of proposals
		collection.add(proposal);
	}

	/**
	 * @param string
	 * @return
	 */
	private boolean endsWithDot(String string) {
		final int index = string.lastIndexOf(F_DOT);
		if ((index + 1) == string.length()) {
			return true;
		}
		return false;
	}

	/**
	 * @param currentContent
	 * @return
	 */
	private ArrayList generateContentProposals(String currentContent) {
		this.fInitialContentProposals = new ArrayList();
		// Store the initial field contents to determine if we need to
		// widen the scope later
		this.fInitialContent = currentContent;
		this.generateTypePackageProposals(currentContent, this.fProject,
				this.fInitialContentProposals, 0, this.fTypeScope, true);
		return this.fInitialContentProposals;
	}

	/**
	 * @param list
	 * @return
	 */
	private IContentProposal[] convertResultsToSortedProposals(ArrayList list) {
		IContentProposal[] proposals = null;
		if ((list != null) && (list.size() != 0)) {
			// Convert the results array list into an array of completion
			// proposals
			proposals = (IContentProposal[]) list
					.toArray(new IContentProposal[list.size()]);
			// Sort the proposals alphabetically
			Arrays.sort(proposals, this.fComparator);
		} else {
			proposals = new IContentProposal[0];
		}
		return proposals;
	}

	/**
	 * @param currentContent
	 * @return
	 */
	private ArrayList filterContentProposals(String currentContent) {
		final String lowerCaseCurrentContent = currentContent.toLowerCase();
		final ListIterator iterator = this.fInitialContentProposals
				.listIterator();
		// Maintain a list of filtered search results
		final ArrayList filteredContentProposals = new ArrayList();
		// Iterate over the initial search results
		while (iterator.hasNext()) {
			final Object object = iterator.next();
			final IContentProposal proposal = (IContentProposal) object;
			String compareString = null;
			if (lowerCaseCurrentContent.indexOf(F_DOT) == -1) {
				// Use only the type name
				compareString = proposal.getLabel().toLowerCase();
			} else {
				// Use the fully qualified type name
				compareString = proposal.getContent().toLowerCase();
			}
			// Filter out any proposal not matching the current contents
			// except for the edge case where the proposal is identical to the
			// current contents
			if (compareString.startsWith(lowerCaseCurrentContent, 0)) {
				filteredContentProposals.add(proposal);
			}
		}
		return filteredContentProposals;
	}

}
