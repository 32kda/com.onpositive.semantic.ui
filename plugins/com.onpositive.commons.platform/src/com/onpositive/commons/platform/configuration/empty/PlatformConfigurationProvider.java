package com.onpositive.commons.platform.configuration.empty;

import java.io.InputStream;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.xml.language.IResourceLink;
import com.onpositive.core.runtime.Bundle;
import com.onpositive.core.runtime.IAdapterManager;
import com.onpositive.core.runtime.IExtensionRegistry;
import com.onpositive.core.runtime.IPlatform;
import com.onpositive.core.runtime.IPlatformProvider;
import com.onpositive.core.runtime.IResourceFinder;

public class PlatformConfigurationProvider implements IPlatformProvider {

	ManualExtensionRegistry registry=new ManualExtensionRegistry();
	
	private static final class BundleLink implements IResourceLink {
		private String attribute;

		
		public InputStream openStream() {
			return PlatformConfigurationProvider.class.getResourceAsStream(attribute);
		}

		
		public String location() {
			return attribute;
		}

		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((attribute == null) ? 0 : attribute.hashCode());
			return result;
		}

		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BundleLink other = (BundleLink) obj;
			if (attribute == null) {
				if (other.attribute != null)
					return false;
			} else if (!attribute.equals(other.attribute))
				return false;
			return true;
		}
	}

	public IPlatform getPlatform(){
		return new IPlatform() {
			
			
			public void log(Throwable e) {
				e.printStackTrace();
			}
			
			
			public boolean isDebug() {
				return false;
			}
			
			
			public String getOS() {
				return "";
			}
			
			
			public IExtensionRegistry getExtensionRegistry() {
				return registry;
			}
			
			
			public Bundle getBundle(String id) {
				return new Bundle() {
					
					
					public Class<?> loadClass(String className) throws ClassNotFoundException {
						return PlatformConfigurationProvider.class.getClassLoader().loadClass(className);
					}
					
					
					public String getSymbolicName() {
						return "";	
					}
					
					
					public InputStream getResourceAsStream(String attribute) {
						return PlatformConfigurationProvider.class.getResourceAsStream(attribute);
					}
					
					
					public IAbstractConfiguration getPreferences() {
						return null;
					}
					
					
					public IResourceLink getEntry(String stringAttribute) {
						return new BundleLink();
					}
				};
			}
			
			
			public IAdapterManager getAdapterManager() {
				return new IAdapterManager() {
					
					@SuppressWarnings("rawtypes")
					
					public Object getAdapter(Object genericRegistryObject, Class adapter) {
						return null;
					}
				};
			}

			
			public IResourceFinder getFinder() {
				return null;
			}
		};
	}
}
