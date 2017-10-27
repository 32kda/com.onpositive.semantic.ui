package com.onpositive.semantic.generator.model;

import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;
import com.onpositive.semantic.model.realm.ValidatorAdapter;


public class CanditateValidator extends ValidatorAdapter<CandidateConfigurationListElement>
{
	
	public CodeAndMessage isValid(IBinding context,
			CandidateConfigurationListElement object)
	{		
		if (object.isUsed() && (object.getCurCandidate().getCaption().trim().equals("") || object.getCreator() == null)) return CodeAndMessage.errorMessage("Invalid caption or creator");
		return CodeAndMessage.OK_MESSAGE;
	}

}
