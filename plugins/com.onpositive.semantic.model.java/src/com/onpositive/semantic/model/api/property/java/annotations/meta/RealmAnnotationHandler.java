package com.onpositive.semantic.model.api.property.java.annotations.meta;

import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.realm.ExpressionBasedRealmProvider;
import com.onpositive.semantic.model.api.realm.IRealmProvider;

public class RealmAnnotationHandler implements CustomHandler<RealmProvider>{

	@SuppressWarnings("rawtypes")
	
	public void handle(RealmProvider annotation, IWritableMeta meta) {
		String value = annotation.expression();
		Class<? extends IRealmProvider> provider = annotation.value();
		if (provider != IRealmProvider.class) {
			try {				
				meta.registerService(IRealmProvider.class,
						provider.newInstance());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		if (value.length() > 0) {
			meta.registerService(
					IRealmProvider.class,
					new ExpressionBasedRealmProvider(value));
		}
	}

}
