package com.onpositive.semantic.ui.snippets;

import org.eclipse.jface.preference.JFacePreferences;

import com.onpositive.semantic.model.api.roles.DecorationContext;
import com.onpositive.semantic.model.api.roles.IRichTextDecorator;
import com.onpositive.semantic.model.api.roles.ITextDecorator;
import com.onpositive.semantic.model.api.roles.StyledString;
import com.onpositive.semantic.ui.snippets.Snippet008Club.Person;

public class InterestCountDecorator implements ITextDecorator,
		IRichTextDecorator {

	public InterestCountDecorator() {

	}

	public StyledString decorateRichText(DecorationContext context,
			StyledString text) {
		final Person p = (Person) context.object;
		final StyledString.Style style = new StyledString.Style("group", null); //$NON-NLS-1$
		final StyledString.Style style2 = new StyledString.Style(
				JFacePreferences.HYPERLINK_COLOR, null);
		style2.setUnderline(true);
		text.colorize(0, text.length(), style2);
		final int size = p.participates.size();
		if (size > 1) {
			return text.append("(" + size + " groups)", style); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (size == 1) {
			return text.append("(one group)", style); //$NON-NLS-1$
		}
		return text;
	}

	public String decorateText(DecorationContext parameterObject, String text) {
		final Person p = (Person) parameterObject.object;
		final int size = p.participates.size();
		if (size > 1) {
			return text + "(" + size + " groups)"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (size == 1) {
			return text + "(one group)"; //$NON-NLS-1$
		}
		return text;
	}

}
