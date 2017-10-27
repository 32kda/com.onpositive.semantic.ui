package com.onpositive.ui.help;

import org.eclipse.swt.widgets.Control;

import com.onpositive.core.runtime.Bundle;
import com.onpositive.core.runtime.Platform;

public class HelpSystem {

	static IHelpSystem system;

	public static IHelpSystem getSystem() {
		return system;
	}

	public static void setSystem(IHelpSystem system) {
		HelpSystem.system = system;
	}

	static {
		try {
			final Bundle bundle = Platform
					.getBundle("com.onpositive.semantic.ui.workbench"); //$NON-NLS-1$
			if (bundle != null) {
				final Class<?> loadClass = bundle
						.loadClass("com.onpositive.semantic.ui.workbench.providers.WorkbenchHelpSystemProvider"); //$NON-NLS-1$

				system = (IHelpSystem) loadClass.newInstance();
			}
		} catch (Throwable e) {
			Platform.log(e);

		}
	}

	public static void displayHelp(String contextId) {

		system.displayHelp(contextId);
	}

	public static void setHelp(Control createDialogArea, String helpContext) {
		system.setHelp(createDialogArea, helpContext);
	}
}
