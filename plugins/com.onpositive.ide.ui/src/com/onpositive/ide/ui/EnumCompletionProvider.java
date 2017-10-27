package com.onpositive.ide.ui;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.onpositive.commons.namespace.ide.ui.completion.TypeCompletionProposal;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeCompletionProvider;

public class EnumCompletionProvider implements ITypeCompletionProvider {

	public EnumCompletionProvider() {
	}

	public void fillProposals(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String startString, int lengthCompletion,
			ArrayList<ICompletionProposal> result, String fullString,
			boolean addBraces, String typeSpecialization) {
		if (startString.startsWith("\""))
			startString = startString.substring(1);
		if (result == null)
			result = new ArrayList<ICompletionProposal>();

		if (typeSpecialization != null && typeSpecialization.length() > 0) {
			startString = startString.toLowerCase().trim();
			String[] standart=typeSpecialization.trim().split(",");
			for (int i = 0; i < standart.length; i++) {
				if (standart[i].startsWith(startString)) {
					int replacementLength;
					if (fullString != null && fullString.length() > 0)
						replacementLength = fullString.length();
					else
						replacementLength = startString.length();
					String replacementString = standart[i];
					if (addBraces)
						replacementString = "\"" + replacementString + "\"";

					result.add(new TypeCompletionProposal(replacementString,
							null, standart[i], offset - startString.length(),
							replacementLength));
				}
			}
			
		}
	}

}
