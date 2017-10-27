package com.onpositive.commons;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	private static Activator defaultA;

	public static Activator getDefault() {
		return defaultA;
	}

	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		//com.onpositive.semantic.model.Activator.checkInit();
		defaultA = this;
		final Bundle bundle2 = Platform
				.getBundle("com.onpositive.semantic.ui.workbench"); //$NON-NLS-1$
		if ((bundle2 != null) && (bundle2.getState() == Bundle.RESOLVED)) {
			bundle2.start();
		}
		
		super.start(context);
	}

	public static void log(Exception e) {
		final String symbolicName = Activator.getDefault().getBundle()
				.getSymbolicName();
		Activator.getDefault().getLog().log(
				new Status(IStatus.ERROR, symbolicName, IStatus.ERROR, e
						.getMessage(), e));
	}

}
