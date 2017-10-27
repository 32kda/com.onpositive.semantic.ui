package com.onpositive.commons.platform.configuration;

import java.text.MessageFormat;
import java.util.HashSet;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.onpositive.commons.xml.language.Activator;


public class EclipsePreferencesConfiguration implements IAbstractConfiguration {

	Preferences preferences;
	private String baseKey;

	static HashSet<Preferences> prefs = new HashSet<Preferences>();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				for (final Preferences p : prefs) {
					try {
						p.flush();
					} catch (final BackingStoreException e) {
						Activator.log(e);
					}
				}
			}
		});
	}

	public EclipsePreferencesConfiguration(Preferences node) {
		this.preferences = node;

		prefs.add(node);
	}

	public EclipsePreferencesConfiguration(Preferences node, String key) {
		prefs.add(node);
		this.preferences = node;
		this.baseKey = key;
	}

	public IAbstractConfiguration createSubConfiguration(String name) {
		return new EclipsePreferencesConfiguration(this.preferences, this.getKey(name));
	}

	private String getKey(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		if ((this.baseKey == null) || (this.baseKey.length() == 0)) {
			return name;
		}
		return MessageFormat.format("{0}.{1}", new Object[] { this.baseKey, name }); //$NON-NLS-1$
	}

	public boolean getBooleanAttribute(String name) {
		return this.preferences.getBoolean(this.getKey(name), false);
	}

	public double getDoubleAttribute(String name) {
		return this.preferences.getDouble(this.getKey(name), 0.0);
	}

	public int getIntAttribute(String name) {
		return this.preferences.getInt(this.getKey(name), 0);
	}

	public long getLongAttribute(String name) {
		return this.preferences.getLong(this.getKey(name), 0);
	}

	public String[] getStringArrayAttribute(String name) {
		final String object = this.getStringAttribute(name);
		if ((object == null) || (object.length() == 0)) {
			return NO_VALUES;
		}
		return object.split(","); //$NON-NLS-1$
	}

	public String getStringAttribute(String name) {
		return this.preferences.get(this.getKey(name), ""); //$NON-NLS-1$
	}

	public IAbstractConfiguration getSubConfiguration(String name) {
		return this.createSubConfiguration(name);
	}

	public String[] propertyNames() {
		try {
			return this.preferences.keys();
		} catch (final BackingStoreException e) {
			throw new IllegalArgumentException();
		}
	}

	public void removeAttribute(String name) {
		this.preferences.remove(this.getKey(name));
	}

	public void setBooleanAttribute(String name, boolean value) {
		this.preferences.putBoolean(this.getKey(name), value);
	}

	public void setDoubleAttribute(String name, double value) {
		this.preferences.putDouble(this.getKey(name), value);
	}

	public void setIntAttribute(String name, int value) {
		this.preferences.putInt(this.getKey(name), value);
	}

	public void setLongAttribute(String name, long value) {
		this.preferences.putLong(this.getKey(name), value);
	}

	private static final String[] NO_VALUES = new String[0];

	public void setStringArrayAttribute(String name, String[] value) {
		final StringBuilder bld = new StringBuilder();
		for (int a = 0; a < value.length; a++) {
			bld.append(value[a]);
			if (a != value.length - 1) {
				bld.append(',');
			}
		}
		this.setStringAttribute(name, bld.toString());
	}

	public void setStringAttribute(String name, String value) {
		this.preferences.put(this.getKey(name), value);
	}

	public void setSubConfiguration(String name,
			IAbstractConfiguration configuration) {
		throw new UnsupportedOperationException();
	}

	public static EclipsePreferencesConfiguration getPluginPreferences(String id) {
		return new EclipsePreferencesConfiguration(Platform
				.getPreferencesService().getRootNode()
				.node("/configuration").node(id)); //$NON-NLS-1$
	}

	public static EclipsePreferencesConfiguration getPluginPreferences(Plugin id) {
		return new EclipsePreferencesConfiguration(Platform
				.getPreferencesService().getRootNode()
				.node("/configuration").node( //$NON-NLS-1$
						id.getBundle().getSymbolicName()));
	}

	public static EclipsePreferencesConfiguration getPluginPreferences(Bundle id) {
		return new EclipsePreferencesConfiguration(Platform
				.getPreferencesService().getRootNode()
				.node("/configuration").node( //$NON-NLS-1$
						id.getSymbolicName()));
	}

	public void flush() {
		try {
			this.preferences.flush();
		} catch (final BackingStoreException e) {
			throw new IllegalStateException(e);
		}
	}

}