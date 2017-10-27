package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;


public interface ITextElement<T> extends ICanBeReadOnly<T>, IUseLabelsForNull{

	
	public String getSeparatorCharacters() ;

	@HandlesAttributeDirectly("separatorCharacters")
	public void setSeparatorCharacters(String separatorCharacters) ;
	
	
	public String getContentAssistRole();

	@HandlesAttributeDirectly("contentAssistRole")
	public void setContentAssistRole(String contentAssistRole);
}
