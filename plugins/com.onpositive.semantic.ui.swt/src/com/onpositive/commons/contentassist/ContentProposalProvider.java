package com.onpositive.commons.contentassist;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.widgets.Control;

import com.onpositive.core.runtime.Bundle;
import com.onpositive.core.runtime.Platform;

public class ContentProposalProvider {

	private static IContentProposalAdapterProvider defaultProvider = new IContentProposalAdapterProvider() {

		public ContentProposalAdapter createContentProposalAdapter(
				Control control, IControlContentAdapter contentAdapter,
				IContentProposalProvider provider, char[] charecters) {
			return new ContentAssistCommandAdapter(control, contentAdapter,
					provider, null, charecters, true);
		}

	};

	static {
		final Bundle bundle = Platform
				.getBundle("com.onpositive.semantic.ui.workbench"); //$NON-NLS-1$
		if (bundle != null) {
			try {
				final Class<?> loadClass = bundle
						.loadClass("com.onpositive.semantic.ui.workbench.providers.WorkbenchContentAssistProvider"); //$NON-NLS-1$
				try {
					defaultProvider = (IContentProposalAdapterProvider) loadClass
							.newInstance();
				} catch (final InstantiationException e) {
					throw new RuntimeException();
				} catch (final IllegalAccessException e) {
					throw new RuntimeException();
				}
			} catch (final ClassNotFoundException e) {
				throw new RuntimeException();
			}
		}
	}

	/**
	 * TODO REWRITE IT
	 * 
	 * @param control
	 * @param contentAdapter
	 * @param provider
	 * @param charecters
	 * @return
	 */
	public static ContentProposalAdapter createContentProposalAdapter(
			Control control, IControlContentAdapter contentAdapter,
			IContentProposalProvider provider, char[] charecters) {
		return defaultProvider.createContentProposalAdapter(control,
				contentAdapter, provider, charecters);
	}

}
