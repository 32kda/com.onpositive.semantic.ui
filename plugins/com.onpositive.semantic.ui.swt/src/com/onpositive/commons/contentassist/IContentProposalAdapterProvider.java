package com.onpositive.commons.contentassist;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.widgets.Control;

public interface IContentProposalAdapterProvider {

	public abstract ContentProposalAdapter createContentProposalAdapter(
			Control control, IControlContentAdapter contentAdapter,
			IContentProposalProvider provider, char[] charecters);

}