package com.onpositive.semantic.realm;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	private static Activator instance;

	public Activator() {
		instance=this;
	}

	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
	}

	
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	public static Activator getDefault(){
		return instance;
	}
}
