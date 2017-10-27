package com.onpositive.commons.xml.language;

import java.lang.reflect.Method;

import org.w3c.dom.Element;

import com.onpositive.core.runtime.Platform;

public class AnnotationHandler implements IElementHandler {

	Class<?> annotation;
	Class<?> defaultValue;
	private Method method;

	public AnnotationHandler(Class<?> annotation ) {
		this.annotation = annotation ;
		try {
			method = annotation.getMethod("value");
			if (method.getReturnType() == Class.class) {
				defaultValue = (Class<?>) method.getDefaultValue();
			}
		} catch (SecurityException e) {

		} catch (NoSuchMethodException e) {
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Object handleElement(Element element, Object parentContext, Context context)
	{
		IHasAdapters pBnd = ( IHasAdapters ) parentContext;
		final AnnotationProxy annotationProxy = new AnnotationProxy(annotation,	context.getClassLoader(), element);
		final Object instance = annotationProxy.getInstance();
		
		pBnd.setAdapter( annotation, instance );
		final Class class2 = defaultValue;
		if (class2 != null)
		{
			Class invoke;
			try {
				invoke = (Class) method.invoke(instance);
				if (invoke != null && invoke != defaultValue) {
					try {
						pBnd.setAdapter(class2, invoke.newInstance());
					} catch (final InstantiationException e) {
						Platform.log(e);
					}
				}
			} catch (final Exception e) {
				Platform.log(e);
			}
		}
		
		return null;
	}
}
