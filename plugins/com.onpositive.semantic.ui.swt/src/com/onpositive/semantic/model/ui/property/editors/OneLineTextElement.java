package com.onpositive.semantic.model.ui.property.editors;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.onpositive.commons.contentassist.ContentProposalAdapter;
import com.onpositive.commons.contentassist.ContentProposalProvider;
import com.onpositive.commons.contentassist.IHasContentAssist2;
import com.onpositive.semantic.model.binding.IBinding;

public class OneLineTextElement<T> extends AbstractTextElement<T, Text> implements IHasContentAssist2{

	private boolean isPassword;
	int textLimit=-1;

	public int getTextLimit() {
		return textLimit;
	}

	public void setTextLimit(int textLimit) {
		this.textLimit = textLimit;
	}

	public OneLineTextElement() {
		super();
	}

	public OneLineTextElement(IBinding binding) {
		super(binding);
		
	}

	public OneLineTextElement(String caption) {
		super(caption);		
	}

	protected Text internalCreateControl(Composite conComposite) {
		Text text2 = new Text(conComposite, (!this.parentDrawsBorder() ? SWT.BORDER
				: SWT.NONE)
				| SWT.SINGLE
				| (this.isReadOnly() ? SWT.READ_ONLY : SWT.NONE)
				| (isPassword ? SWT.PASSWORD : SWT.NONE));
		if (textLimit!=-1){
			text2.setTextLimit(textLimit);
		}
		return text2;
	}

	protected ContentProposalAdapter createContentAssistAdapter(Text text) {
		if (this.contentAssist != null) {
			return this.contentAssist;
		}
		text.setData(ContentProposalAdapter.POPUP_CONTENT_CREATOR,
				this.infoContentAssist);
		final ContentProposalAdapter createContentProposalAdapter = ContentProposalProvider
				.createContentProposalAdapter(text, new TextContentAdapter(),
						this.proposalProvider, null);
		return createContentProposalAdapter;
	}

	public void setIsPassword(boolean b) {
		this.isPassword = b;
	}

	public boolean isPassword() {
		return this.isPassword;
	}
}
