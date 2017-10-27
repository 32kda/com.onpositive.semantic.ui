package com.onpositive.core.runtime;

public interface IPlatform {

	IExtensionRegistry getExtensionRegistry();
	IAdapterManager getAdapterManager();
	Bundle getBundle(String id);
	IResourceFinder getFinder();
	String getOS();
	void log(Throwable e);
	boolean isDebug();
}
