package com.onpositive.semantic.model.api.property.java.annotations.meta;

import com.onpositive.semantic.model.api.factory.IFactoryProvider;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.property.java.annotations.FactoryProvider;

public class FactoryAnnotationHandler implements CustomHandler<FactoryProvider>{

	public void handle(FactoryProvider annotation, IWritableMeta meta) {
		String value = annotation.expression();
		Class<? extends IFactoryProvider> provider = annotation.value();
		if (provider != IFactoryProvider.class) {
			try {				
				meta.registerService(IFactoryProvider.class,
						provider.newInstance());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		if (value.length() > 0) {
			meta.registerService(
					IFactoryProvider.class,
					new ExpressionBasedFactoryProvider(value,annotation.caption()));
		}
	}

}
