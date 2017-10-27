package com.onpositive.commons.platform.configuration;

import java.util.HashMap;

/**
 * implementation of {@link IAbstractConfiguration} which stores data in
 * {@link HashMap}
 * 
 * @author Pavel Petrochenko
 */

public class Configuration implements IAbstractConfiguration {

	private static final String[] NO_VALUES = new String[0];

	private final HashMap<Object, Object> map = new HashMap<Object, Object>();

	public IAbstractConfiguration createSubConfiguration(String name) {
		this.checkName(name);
		final Configuration cm = new Configuration();
		this.map.put(name, cm);
		return cm;
	}	

	public boolean getBooleanAttribute(String name) {
		this.checkName(name);
		final Object object = this.map.get(name);
		if (object == null) {
			return false;
		} else if (object instanceof Boolean) {
			final Boolean bl = (Boolean) object;
			return bl.booleanValue();
		} else {
			return false;
		}
	}

	public int getIntAttribute(String name) {
		this.checkName(name);
		final Object object = this.map.get(name);
		if (object == null) {
			return 0;
		} else if (object instanceof Integer) {
			final Integer bl = (Integer) object;
			return bl.intValue();
		} else {
			try {
				return Integer.parseInt(object.toString());
			} catch (final NumberFormatException e) {
				return 0;
			}
		}
	}

	public String[] getStringArrayAttribute(String name) {
		this.checkName(name);
		final Object object = this.map.get(name);
		if (object == null) {
			return NO_VALUES;
		} else if (object instanceof String[]) {
			return (String[]) object;
		}
		return null;
	}

	public String getStringAttribute(String name) {
		this.checkName(name);
		final Object object = this.map.get(name);
		if (object == null) {
			return ""; //$NON-NLS-1$
		} else {
			return object.toString();
		}
	}

	public IAbstractConfiguration getSubConfiguration(String name) {
		this.checkName(name);
		final Object object = this.map.get(name);
		if (object == null) {
			return null;
		} else if (object instanceof IAbstractConfiguration) {
			return (IAbstractConfiguration) object;
		}
		return null;
	}

	public void removeAttribute(String name) {
		this.checkName(name);
		this.map.remove(name);
	}

	private void checkName(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
	}

	public void setBooleanAttribute(String name, boolean value) {
		this.checkName(name);
		this.map.put(name, value ? Boolean.TRUE : Boolean.FALSE);
	}

	public void setIntAttribute(String name, int value) {
		this.checkName(name);
		this.map.put(name, new Integer(value));
	}

	public void setStringArrayAttribute(String name, String[] value) {
		this.checkName(name);
		this.map.put(name, value);
	}

	public void setStringAttribute(String name, String value) {
		this.checkName(name);
		this.map.put(name, value);
	}

	public void setSubConfiguration(String name,
			IAbstractConfiguration configuration) {
		this.checkName(name);
		this.map.put(name, configuration);
	}

	public String[] propertyNames() {
		final String[] set = new String[this.map.size()];
		this.map.keySet().toArray(set);
		return set;
	}

	public double getDoubleAttribute(String name) {
		this.checkName(name);
		final Object object = this.map.get(name);
		if (object == null) {
			return 0;
		} else if (object instanceof Number) {
			final Number bl = (Number) object;
			return bl.doubleValue();
		} else {
			try {
				return Double.parseDouble(object.toString());
			} catch (final NumberFormatException e) {
				return 0;
			}
		}
	}

	public long getLongAttribute(String name) {
		this.checkName(name);
		final Object object = this.map.get(name);
		if (object == null) {
			return 0;
		} else if (object instanceof Number) {
			final Number bl = (Number) object;
			return bl.longValue();
		} else {
			try {
				return Long.parseLong(object.toString());
			} catch (final NumberFormatException e) {
				return 0;
			}
		}
	}

	public void setDoubleAttribute(String name, double value) {
		this.checkName(name);
		this.map.put(name, value);

	}

	public void setLongAttribute(String name, long value) {
		this.checkName(name);
		this.map.put(name, value);
	}

	public void flush() {

	}

}
