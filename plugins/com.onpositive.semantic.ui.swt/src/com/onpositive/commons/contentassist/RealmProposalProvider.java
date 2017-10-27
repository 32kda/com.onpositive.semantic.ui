/**
 * 
 */
package com.onpositive.commons.contentassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.jface.viewers.LabelProvider;

import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.api.roles.TextObject;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.IContentProposal;

/**
 * TODO IMPROVE IT
 * 
 * @author kor
 * 
 * @param <T>
 */
public class RealmProposalProvider<T> implements IContentProposalProvider2 {

	/**
	 * 
	 */
	protected IHasContentAssist oneLineTextElement;

	String sepCharacters = ";,"; //$NON-NLS-1$

	private final IRealm<?> realm;

	public RealmProposalProvider(IHasContentAssist oneLineTextElement,
			IRealm<?> realm) {
		this.oneLineTextElement = oneLineTextElement;
		this.realm = realm;
		if (this.oneLineTextElement.getSeparatorCharacters() != null) {
			this.sepCharacters = this.oneLineTextElement.getSeparatorCharacters();
		}
		
	}
	
	public RealmProposalProvider(IRealm<?>r) {
		this.realm=r;
		
	}

	public IContentProposal[] getProposals(String contents, int position) {
		final Collection<?> contents2 = basicGetCandidates();
		ITextLabelProvider pr = null;
		pr = this.oneLineTextElement.getBinding().getAdapter(
				ITextLabelProvider.class);
		contents = contents.substring(0, position);
		final ArrayList<BasicContentProposal> result = new ArrayList<BasicContentProposal>();
		String disp = contents;
		final HashSet<String> prevs = new HashSet<String>();
		if (this.oneLineTextElement.getBinding().allowsMultiValues()) {
			for (int a = position - 1; a > 0; a--) {
				if (this.sepCharacters.indexOf(contents.charAt(a)) != -1) {
					disp = contents.substring(a + 1, position);
					int b = a - 1;
					for (; b > 0; b--) {
						if (this.sepCharacters.indexOf(contents.charAt(b)) != -1) {
							final String substring = contents.substring(b + 1, a);
							final String trim = substring.trim();
							if (trim.length() > 0) {
								prevs.add(trim);
							}
							a = b;
						}
					}
					final String substring = contents.substring(b, a);
					final String trim = substring.trim();
					if (trim.length() > 0) {
						prevs.add(trim);
					}
					break;
				}
			}
		}
		disp = disp.trim();
		final ContentProposalAdapter adapter = this.oneLineTextElement.getContentAssist();
		final boolean replace = adapter!=null?adapter.getProposalAcceptanceStyle() == ContentProposalAdapter.PROPOSAL_REPLACE:true;
		for (final Object o : contents2) {
			String string = pr != null ? pr.getText(o) : o.toString();
			if (prevs.contains(string)) {
				continue;
			}
			String description = pr != null ? pr.getDescription(o) : null;
			
			final TextObject textObject = LabelManager.getInstance().getTextObject(o,
					this.oneLineTextElement.getContentAssistRole(),
					this.oneLineTextElement.getTheme());
			
			String caption = textObject == null ? string : textObject
					.getSimpleLabel(o);
			if (oneLineTextElement instanceof IHasContentAssist2){
				IHasContentAssist2 t=(IHasContentAssist2) oneLineTextElement;
				LabelProvider pma=t.getContentAssistLabelProvider();
				if (pma!=null){
					caption = pma.getText(o);
					string=caption;
				}				
			}
			description = getActualDescription(o, description, textObject);
			
			// ||)
			if (string.toLowerCase().startsWith(disp.toLowerCase())) {
				if (string.length() != disp.length()) {
					if (replace) {
						final BasicContentProposal basicContentProposal = new BasicContentProposal(
								string, caption, description, string.length(),
								o);
						result.add(basicContentProposal);
					} else {
						final BasicContentProposal basicContentProposal = createProposal(position, disp, o, string, description,
								caption);
						result.add(basicContentProposal);
					}

				}
			} else if (caption.toLowerCase().startsWith(disp.toLowerCase())) {
				final BasicContentProposal basicContentProposal = new BasicContentProposal(
						string, caption, description, position - disp.length(),
						o);
				result.add(basicContentProposal);
			}
		}
		Collections.sort(result);
		final IContentProposal[] rs = new IContentProposal[result.size()];
		result.toArray(rs);
		return rs;
	}

	protected Collection<?> basicGetCandidates() {
		return this.realm.getContents();
	}

	protected BasicContentProposal createProposal(int position, String disp,
			final Object o, String string, String description, String caption) {
		return new BasicContentProposal(
				string.substring(disp.length()), caption,
				description, position + string.length()
						- disp.length(), o);
	}

	protected String getActualDescription(final Object o, String description,
			final TextObject textObject) {
		description = textObject == null ? description : textObject
				.getDescription(o);
		return description;
	}

	public void setContentAssistOwner(IHasContentAssist s) {
		this.oneLineTextElement=s;
		if ( this.oneLineTextElement.getSeparatorCharacters() != null) {
			this.sepCharacters = this.oneLineTextElement.getSeparatorCharacters();
		}
	}
}