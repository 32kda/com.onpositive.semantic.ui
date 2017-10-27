package com.onpositive.semantic.language.model;

import com.onpositive.semantic.model.api.property.adapters.RealmProviderAdapter;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;

public class DeclUrlRealmProvider extends RealmProviderAdapter<String>{

	@Override
	public IRealm<String> getRealm(IBinding model) {
		NameSpaceContributionModel object2 = (NameSpaceContributionModel) model.getObject();
		Realm<String>ss=new Realm<String>();
		if (object2.getDeclUrl()!=null){
			ss.add(object2.getDeclUrl());
		}
		return ss;
	}
}
