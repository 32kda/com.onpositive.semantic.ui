package com.onpositive.semantic.model.api.meta;

import java.util.ArrayList;
import java.util.HashMap;

import com.onpositive.semantic.model.api.globals.IKey;

public class MetaAccess {

	private static final String DEFAULT_CONTRIBUTOR_PROVIDER = "com.onpositive.semantic.model.platform.contributor.PlatformMetaContributorProvider";
	private static final String DEFAULT_META_PROVIDER_KEY = "meta.defaultProvider";
	private static final String SERVICE_PROVIDER_PROPERTY_KEY = "meta.defaultServiceProvider";
	private static final String DEFAULT_META_PROVIDER = "com.onpositive.semantic.model.api.property.java.BeanMetaProvider";
	private static final String DEFAULT_SERVICE_PROVIDER = DEFAULT_CONTRIBUTOR_PROVIDER;
	static IMetaProvider defaultMetaProvider;
	static IServiceProvider<?> defaultServiceProvider;
	private static BaseMeta nullMeta = new BaseMeta();
	static {
		nullMeta.lock();
	}

	protected static HashMap<IKey, ArrayList<IMetaContributor>> contributors = new HashMap<IKey, ArrayList<IMetaContributor>>();

	// TODO Fire and handle event
	public static synchronized void registerMetaContributor(IKey key,
			IMetaContributor contributor) {
		ArrayList<IMetaContributor> arrayList = contributors.get(key);
		if (arrayList == null) {
			arrayList = new ArrayList<IMetaContributor>();
			contributors.put(key, arrayList);
		}
		if (!arrayList.contains(contributor)) {
			arrayList.add(contributor);
		}
	}

	// TODO Fire and handle event
	public static synchronized void unregisterMetaContributor(IKey key,
			IMetaContributor contributor) {
		ArrayList<IMetaContributor> arrayList = contributors.get(key);
		if (arrayList == null) {
			return;
		}
		if (!arrayList.contains(contributor)) {
			arrayList.remove(contributor);
		}
		if (arrayList.isEmpty()) {
			contributors.remove(key);
		}
	}

	public static synchronized void contribute(IKey key, IWritableMeta meta) {
		ArrayList<IMetaContributor> arrayList = contributors.get(key);
		if (arrayList != null) {
			for (IMetaContributor c : arrayList) {
				c.contribute(meta);
			}
		}
		IKey parent = key.getParent();
		if (parent != null) {
			contribute(parent, meta);
		}
	}

	public static IServiceProvider<?> getDefaultServiceProvider() {
		if (defaultServiceProvider == null) {
			String property = System.getProperty(SERVICE_PROVIDER_PROPERTY_KEY,
					DEFAULT_SERVICE_PROVIDER);
			try {
				defaultServiceProvider = (IServiceProvider<?>) MetaAccess.class
						.getClassLoader().loadClass(property).newInstance();
			} 
			catch (ClassNotFoundException e) {
				defaultServiceProvider=new IServiceProvider<Object>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public Object getService(IHasMeta meta, Class<Object> serv,
							IHasMeta original) {
						return null;
					}
				};
			}
			catch (Exception e) {
				throw new IllegalStateException(e);
			}

		}
		return defaultServiceProvider;
	}

	public static void setDefaultServiceProvider(
			IServiceProvider<?> defaultServiceProvider) {
		MetaAccess.defaultServiceProvider = defaultServiceProvider;
	}

	public static IMetaProvider getDefaultMetaProvider() {
		return defaultMetaProvider;
	}

	public static void setDefaultMetaProvider(IMetaProvider defaultMetaProvider) {
		MetaAccess.defaultMetaProvider = defaultMetaProvider;
	}

	public static IHasMeta getMeta(Object object) {
		if (object == null) {
			return nullMeta;
		}
		if (object instanceof IHasMeta) {
			return (IHasMeta) object;
		}
		if (defaultMetaProvider == null) {
			String property = System.getProperty(DEFAULT_META_PROVIDER_KEY,
					DEFAULT_META_PROVIDER);
			try {
				defaultMetaProvider = (IMetaProvider) MetaAccess.class
						.getClassLoader().loadClass(property).newInstance();
				getDefaultServiceProvider();
			} catch (Exception e) {
				defaultMetaProvider=new IMetaProvider() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
					BaseMeta baseMeta = new BaseMeta();
					
					{
						baseMeta.lock();
					}
					@Override
					public IMeta getMeta(Object object) {
						
						return baseMeta;
					}
				};
			}
		}
		return defaultMetaProvider.getMeta(object);
	}

	public static IServiceRegistrator getServiceRegistrator(Class<?> servClazz) {
		IHasMeta meta = getMeta(servClazz);
		IServiceRegistrator service = DefaultMetaKeys.getService(meta,
				IServiceRegistrator.class);
		if (service!=null){
			return service;
		}
		return null;
	}

}
