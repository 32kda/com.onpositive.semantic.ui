package com.onpositive.semantic.ui.android;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.ui.generic.widgets.ITextElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUseLabelsForNull;
import com.onpositive.semantic.model.ui.generic.widgets.impl.TextDelegate;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class AndroidLabel extends AndroidUIElement implements ITextElement<View> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AndroidLabel() {
		getLayoutHints().setGrabHorizontal(false);
		getLayoutHints().setGrabVertical(false);
	}
	
	@Override
	protected void resetValue() {
		if (getControl()!=null){
			((TextView)getControl()).setText(getCaption());
		}
		
	}

	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		return new TextView(context);
	}

	@Override
	@HandlesAttributeDirectly("useLabelsForNull")
	public void setUseLabelProviderForNull(boolean useLabelProviderForNull) {
		if (delegate != null) {
			((IUseLabelsForNull) delegate).setUseLabelProviderForNull(useLabelProviderForNull);
		}
	}

	@Override
	public String getSeparatorCharacters() {
		if (delegate != null) {
			return ((TextDelegate) delegate).getSeparatorCharacters();
		}
		return null;
	}

	@Override
	@HandlesAttributeDirectly("separatorCharacters")
	public void setSeparatorCharacters(String separatorCharacters) {
		if (delegate != null) {
			((TextDelegate) delegate).setSeparatorCharacters(separatorCharacters);
		}
		
	}

	@Override
	public String getContentAssistRole() {
		// Do nothing
		return null;
	}

	@Override
	@HandlesAttributeDirectly("contentAssistRole")
	public void setContentAssistRole(String contentAssistRole) {
		// Do nothing
	}
}
