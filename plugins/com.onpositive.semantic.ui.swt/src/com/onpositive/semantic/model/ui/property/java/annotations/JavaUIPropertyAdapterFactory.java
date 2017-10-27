package com.onpositive.semantic.model.ui.property.java.annotations;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.action.IAction;

import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.property.java.AbstractReflectionProperty;
import com.onpositive.semantic.model.api.property.java.annotations.ContentAssist;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;

public class JavaUIPropertyAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == ITextLabelProvider.class) {
			return LabelManager.getInstance().getLabelProvider();
		}
		if (adapterType == Runnable.class) {
			final IAction aa = (IAction) adaptableObject;
			return new Runnable() {

				public void run() {
					aa.run();
				}

			};
		}
		if (adaptableObject instanceof Class) {
			final Class cl = (Class) adaptableObject;
			final ContentAssist annotation = (ContentAssist) cl
					.getAnnotation(ContentAssist.class);
			if (annotation != null) {
				try {
					return annotation.value().newInstance();
				} catch (final InstantiationException e) {
					throw new RuntimeException(e);
				} catch (final IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			return null;
		}
		final AbstractReflectionProperty ps = (AbstractReflectionProperty) adaptableObject;
		if (adapterType == IContentAssistConfiguration.class) {
			final ContentAssist annotation = ps.getAnnotation(ContentAssist.class);
			if (annotation == null) {
				return null;
			}
			try {
				return annotation.value().newInstance();
			} catch (final InstantiationException e) {
				throw new RuntimeException(e);
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { IContentAssistConfiguration.class,
				ITextLabelProvider.class };
	}

}
