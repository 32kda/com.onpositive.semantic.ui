package com.onpositive.semantic.language.model;

import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;
import com.onpositive.semantic.model.realm.ValidatorAdapter;

public class DeclUrlValidator extends ValidatorAdapter<String>{

	@Override
	public CodeAndMessage isValid(IBinding context, String object) {
		NameSpaceContributionModel object2 = (NameSpaceContributionModel) context.getObject();
		if (!object.equals(object2.getDeclUrl())){
			return CodeAndMessage.errorMessage("Namespace url did not matches to url in extension registry");
		}
		return super.isValid(context, object);
	}
}
