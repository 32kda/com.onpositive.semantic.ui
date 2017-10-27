package com.onpositive.semantic.model.api.property.java.annotations.meta;

import java.lang.reflect.Constructor;

import com.onpositive.semantic.model.api.labels.ExpressionBasedLabelProvider;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;

public class TextLabelAnnotationHandler implements CustomHandler<TextLabel> {

	
	public void handle(TextLabel annotation, IWritableMeta meta) {
		String value = annotation.value();
		Class<? extends ITextLabelProvider> provider = annotation.provider();
		if (provider != ITextLabelProvider.class) {
			try {
				if (value.length() > 0) {
					try {
						Constructor<? extends ITextLabelProvider> constructor = provider
								.getConstructor(String.class, String.class);
						meta.registerService(
								ITextLabelProvider.class,
								constructor.newInstance(value,
										annotation.description()));
					} catch (NoSuchMethodException e) {
						try{
						Constructor<? extends ITextLabelProvider> constructor = provider
								.getConstructor(String.class);
						meta.registerService(ITextLabelProvider.class,
								constructor.newInstance(value));
						}catch (NoSuchMethodException ex) {						
						}
					}
				}
				meta.registerService(ITextLabelProvider.class,
						provider.newInstance());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		if (value.length() > 0) {
			meta.registerService(
					ITextLabelProvider.class,
					new ExpressionBasedLabelProvider(value, annotation
							.description()));
		}

	}

}
