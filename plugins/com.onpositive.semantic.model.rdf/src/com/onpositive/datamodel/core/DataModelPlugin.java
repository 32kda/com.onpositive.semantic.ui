package com.onpositive.datamodel.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class DataModelPlugin extends Plugin {

	static DataModelPlugin instance;

	public static DataModelPlugin getInstance() {
		return instance;
	}

	public void start(BundleContext context) throws Exception {
		instance = this;
		//Activator.checkInit();
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}

	public DataModelPlugin() {
	}

}
