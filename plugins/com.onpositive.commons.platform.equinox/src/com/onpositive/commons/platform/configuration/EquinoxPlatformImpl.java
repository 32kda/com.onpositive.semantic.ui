package com.onpositive.commons.platform.configuration;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;

import com.onpositive.core.runtime.Bundle;
import com.onpositive.core.runtime.IAdapterManager;
import com.onpositive.core.runtime.IExtensionRegistry;
import com.onpositive.core.runtime.IPlatform;
import com.onpositive.core.runtime.IResourceFinder;

public class EquinoxPlatformImpl implements IPlatform {

	EquinoxAdapterManager manager = new EquinoxAdapterManager(Platform.getAdapterManager());
	EquinoxExtensionRegistry registry = new EquinoxExtensionRegistry(Platform.getExtensionRegistry());

	public IExtensionRegistry getExtensionRegistry() {
		return registry;
	}

	public IAdapterManager getAdapterManager() {
		return manager;
	}

	public Bundle getBundle(String id) {
		org.osgi.framework.Bundle bundle = Platform.getBundle(id);
		if (bundle == null) {
			return null;
		}
		return new EquinoxBundleImpl(bundle);
	}

	public String getOS() {
		return Platform.getOS();
	}

	public void log(Throwable e) {
		Platform.getLog(
				Platform.getBundle("com.onpositive.commons.platform.equinox"))
				.log(new Status(IStatus.ERROR,
						"com.onpositive.commons.platform.equinox", e
								.getMessage(), e));
	}

	public boolean isDebug() {
		return Platform.inDevelopmentMode()||Platform.inDebugMode();
	}

	public IResourceFinder getFinder() {
		return new IResourceFinder() {
			
			public Object find(Class<?> clazz, String id) {
				ClassLoader classLoader = clazz.getClassLoader();
				if (classLoader instanceof org.eclipse.osgi.framework.adaptor.BundleClassLoader) {
					ClassLoaderDelegate delegate = ((org.eclipse.osgi.framework.adaptor.BundleClassLoader) classLoader)
							.getDelegate();
					URL imageResource = delegate.findResource(id);
					return imageResource;
				}
				URL byId = classLoader.getResource(id);
				return byId;				
			}
		};
	}

}
