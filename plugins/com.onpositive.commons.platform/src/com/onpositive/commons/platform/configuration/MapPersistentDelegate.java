package com.onpositive.commons.platform.configuration;

import java.util.Map;

@SuppressWarnings("unchecked")
public class MapPersistentDelegate implements
		IConfigurationPersistenceDelegate<Map> {

	public Map<Object, Object> load(Class<Map> obj,
			IAbstractConfiguration config) {
		try {
			final Map map = obj.newInstance();

			final int intAttribute = config.getIntAttribute("size"); //$NON-NLS-1$
			for (int a = 0; a < intAttribute; a++) {
				IAbstractConfiguration subConfiguration = config
						.getSubConfiguration("key" + Integer.toString(a)); //$NON-NLS-1$
				String stringAttribute = subConfiguration
						.getStringAttribute("class"); //$NON-NLS-1$
				try {
					Class dd = ConfigurationPersistence
							.getPersistingClass(stringAttribute);
					final Object key = ConfigurationPersistence.load(dd,
							subConfiguration);
					subConfiguration = config
							.getSubConfiguration("value" + Integer.toString(a)); //$NON-NLS-1$
					stringAttribute = subConfiguration
							.getStringAttribute("class"); //$NON-NLS-1$
					dd = ConfigurationPersistence
							.getPersistingClass(stringAttribute);
					final Object value = ConfigurationPersistence.load(dd,
							subConfiguration);
					map.put(key, value);
				} catch (final ClassNotFoundException e) {
					throw new IllegalArgumentException(e);
				}
			}
			return map;
		} catch (final InstantiationException e1) {
			throw new IllegalArgumentException();
		} catch (final IllegalAccessException e1) {
			throw new IllegalArgumentException();
		}

	}

	public void store(Map obj, IAbstractConfiguration config) {
		config.setIntAttribute("size", obj.size()); //$NON-NLS-1$
		int i = 0;
		for (final Object o : obj.keySet()) {
			IAbstractConfiguration createSubConfiguration = config
					.createSubConfiguration("key" + Integer.toString(i)); //$NON-NLS-1$
			createSubConfiguration.setStringAttribute(
					"class", o.getClass().getName()); //$NON-NLS-1$
			ConfigurationPersistence.store(o, createSubConfiguration);
			createSubConfiguration = config
					.createSubConfiguration("value" + Integer.toString(i)); //$NON-NLS-1$
			final Object p = obj.get(o);
			ConfigurationPersistence.store(p, createSubConfiguration);
			createSubConfiguration.setStringAttribute(
					"class", p.getClass().getName()); //$NON-NLS-1$
			i++;
		}
	}

}
