package com.onpositive.commons.platform.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.onpositive.commons.xml.language.IResourceLink;
import com.onpositive.core.runtime.Bundle;

public class EquinoxBundleImpl implements Bundle{

	private static final class URLLink implements IResourceLink {
		private final URL entry;

		private URLLink(URL entry) {
			this.entry = entry;
		}

		public InputStream openStream() {
			try {
				return entry.openStream();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((entry == null) ? 0 : entry.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			URLLink other = (URLLink) obj;
			if (entry == null) {
				if (other.entry != null)
					return false;
			} else if (!entry.equals(other.entry))
				return false;
			return true;
		}

		public String location() {
			return entry.toExternalForm();
		}
	}

	org.osgi.framework.Bundle bundle;

	public EquinoxBundleImpl(org.osgi.framework.Bundle bundle) {
		super();
		this.bundle = bundle;
	}

	public Class<?> loadClass(String className) throws ClassNotFoundException {
		return bundle.loadClass(className);
	}

	public IResourceLink getEntry(String stringAttribute) {
		final URL entry = bundle.getEntry(stringAttribute);
		if (entry!=null){
			return new URLLink(entry);
		}
		return null;
	}	

	public IAbstractConfiguration getPreferences() {
		return EclipsePreferencesConfiguration.getPluginPreferences(bundle);
	}

	

	public String getSymbolicName() {
		return bundle.getSymbolicName();
	}

	public InputStream getResourceAsStream(String attribute) {
		try {
			return bundle.getResource(attribute).openStream();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
