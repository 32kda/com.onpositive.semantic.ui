package com.onpositive.commons.platform.configuration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * implementation of IAbstractConfiguration wich stores data in
 * {@link Preferences}
 * 
 * @author Pavel Petrochenko
 */
public class PreferencesConfiguration implements IAbstractConfiguration {

	private static final String[] EMPTY_ARRAY = new String[0];
	Preferences prefs;
	String baseKey;

	/**
	 * @param prefs
	 * @param key
	 */
	public PreferencesConfiguration(Preferences prefs, String key) {
		this.prefs = prefs;
		this.baseKey = key;
	}

	public IAbstractConfiguration createSubConfiguration(String name) {
		return new PreferencesConfiguration(this.prefs, this.getKey(name));
	}

	private String getKey(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		return MessageFormat.format("{0}.{1}", new Object[] { this.baseKey, name }); //$NON-NLS-1$
	}

	public boolean getBooleanAttribute(String name) {
		return this.prefs.getBoolean(this.getKey(name), false);
	}

	public int getIntAttribute(String name) {
		return this.prefs.getInt(this.getKey(name), 0);
	}

	public String[] getStringArrayAttribute(String name) {
		final String string = this.prefs.get(this.getKey(name),"");
		if (string.length() == 0) {
			return EMPTY_ARRAY;
		}
		return string.split(","); //$NON-NLS-1$
	}

	public void setStringArrayAttribute(String name, String[] value) {
		final StringBuffer bf = new StringBuffer();
		for (int a = 0; a < value.length; a++) {
			bf.append(value[a]);
			if (a != value.length - 1) {
				bf.append(',');
			}
		}
		this.prefs.put(this.getKey(name), bf.toString());
	}

	public String getStringAttribute(String name) {
		return this.prefs.get(this.getKey(name),null);
	}

	public IAbstractConfiguration getSubConfiguration(String name) {
		return this.createSubConfiguration(name);
	}

	public void removeAttribute(String name) {
		this.prefs.remove(this.getKey(name)); //$NON-NLS-1$
	}

	public void setBooleanAttribute(String name, boolean value) {
		this.prefs.putBoolean(this.getKey(name), value);
	}

	public void setIntAttribute(String name, int value) {
		this.prefs.putInt(this.getKey(name), value);
	}

	public void setStringAttribute(String name, String value) {
		this.prefs.put(this.getKey(name), value);
	}

	public void setSubConfiguration(String name,
			IAbstractConfiguration configuration) {
		final String[] propertyNames = configuration.propertyNames();
		for (int a = 0; a < propertyNames.length; a++) {
			this.setStringAttribute(this.getKey(propertyNames[a]), configuration
					.getStringAttribute(propertyNames[a]));
		}
	}

	@SuppressWarnings("unchecked")
	public String[] propertyNames() {
		String[] propertyNames;
		try {
			propertyNames = this.prefs.childrenNames();
			final ArrayList filtered = new ArrayList();
			final String start = this.getKey(""); //$NON-NLS-1$
			for (int a = 0; a < propertyNames.length; a++) {
				if (propertyNames[a].startsWith(start)) {
					filtered.add(propertyNames[a].substring(start.length()));
				}
			}
			final String[] result = new String[filtered.size()];
			filtered.toArray(result);
			return result;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
	}

	public double getDoubleAttribute(String name) {
		return this.prefs.getDouble(this.getKey(name), 0);
	}

	public long getLongAttribute(String name) {
		return this.prefs.getLong(this.getKey(name), 0);
	}

	public void setDoubleAttribute(String name, double value) {
		this.prefs.putDouble(this.getKey(name), value);
	}

	public void setLongAttribute(String name, long value) {
		this.prefs.putDouble(this.getKey(name), value);
	}

	public void flush() {

	}

}
