package com.onpositive.core.runtime;

import com.onpositive.commons.platform.configuration.empty.PlatformConfigurationProvider;


public class Platform {

	static IPlatform platform;

	public static void checkPlatform() {
		if (platform == null) {

			try {
				platform = ((IPlatformProvider) Class
						.forName(
								"com.onpositive.commons.platform.configuration.PlatformConfigurationProvider")
						.newInstance()).getPlatform();
			} catch (InstantiationException e) {
				throw new LinkageError("Platform is not found in class path");
			} catch (IllegalAccessException e) {
				throw new LinkageError("Platform is not found in class path");
			} catch (ClassNotFoundException e) {
				platform=new PlatformConfigurationProvider().getPlatform();
			}
		}
	}

	public static IExtensionRegistry getExtensionRegistry() {
		checkPlatform();
		return platform.getExtensionRegistry();
	}

	static IAdapterManager getAdapterManager() {
		checkPlatform();
		return platform.getAdapterManager();
	}

	public static Bundle getBundle(String name2) {
		checkPlatform();
		return platform.getBundle(name2);
	}

	public static String getOS() {
		checkPlatform();
		return platform.getOS();
	}

	protected static AdapterRegistry areg = null;

	public static <T> T getAdapter(Object object, Class<T> class1) {
		if (object == null) {
			return null;
		}
		if (areg == null) {
			areg = new AdapterRegistry("com.onpositive.commons.platform.adapters");
		}
		IAdapterFactory iAdapterFactory = areg.get(object.getClass(), class1);
		if (iAdapterFactory != null) {
			return class1.cast(iAdapterFactory.getAdapter(object, class1));
		}
		return (T) class1.cast(getAdapterManager().getAdapter(object, class1));
	}	
	
	public static void log(Throwable e) {
		checkPlatform();
		platform.log(e);
	}

	public static void log(String message) {
		//System.err.println(message);
	}

	public static boolean isDebug() {
		return platform.isDebug();
	}

	public static IResourceFinder getFinder() {
		checkPlatform();
		if (platform!=null){
			return platform.getFinder();
		}
		return null;
	}

	public static IPlatform getPlatform() {
		return platform;
	}
}
