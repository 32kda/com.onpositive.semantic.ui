package com.onpositive.semantic.model.ui.generic;

/**
 * IContentProposalProvider provides an array of IContentProposals that are
 * appropriate for a textual dialog field, given the field's current content and
 * the current cursor position. 
 * 
 * @since 3.2
 * 
 * @see SimpleContentProposalProvider
 */
public interface IContentProposalProvider {

	/**
	 * Return an array of content proposals representing the valid proposals for a
	 * field.
	 * 
	 * @param contents
	 *            the current contents of the text field
	 * @param position
	 *            the current position of the cursor in the contents
	 * 
	 * @return the array of {@link IContentProposal} that represent valid
	 *         proposals for the field.
	 */
	IContentProposal[] getProposals(String contents, int position);
}
