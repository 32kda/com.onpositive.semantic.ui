package com.onpositive.semantic.model.platform.contributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;

import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.api.globals.IFullKeyResolver;
import com.onpositive.semantic.model.api.globals.IKey;
import com.onpositive.semantic.model.api.globals.IKeyResolver;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMetaContributor;
import com.onpositive.semantic.model.api.meta.IServiceProvider;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

public class PlatformMetaContributorProvider implements
		IServiceProvider<Object>, IMetaContributor {

	private static final class ProxyResolver implements IKeyResolver ,IFullKeyResolver{
		private final IConfigurationElement e;
		IKeyResolver actual;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private ProxyResolver(IConfigurationElement e) {
			this.e = e;
		}

		public Object resolveKey(String key) {
			if (actual==null){
				initActual();
			}
			return actual.resolveKey(key);
		}

		private void initActual() {
			actual=(IKeyResolver) e.createExecutableExtension("class");
		}

		public IKey getKey(Object obj) {
			if (actual==null){
				initActual();
			}								
			return null;
		}

		public Object resolveKey(IKey orig) {
			if (actual==null){
				initActual();
			}
			IFullKeyResolver f=(IFullKeyResolver) actual;
			return f.resolveKey(orig);
		}

		public boolean isReallyFullKey() {
			if (actual==null){
				initActual();
			}
			return (actual instanceof IFullKeyResolver);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static HashMap<String, IConfigurationElement> serviceMap = new HashMap<String, IConfigurationElement>();
	@SuppressWarnings("rawtypes")
	protected static IdentityHashMap<Class, IServiceProvider> serviceClassMap = new IdentityHashMap<Class, IServiceProvider>();

	// initialize contributors
	static {
		boolean platformLaunched = true;
		try {
			Platform.checkPlatform();
		} catch (Throwable e) {
			platformLaunched = false;
		}
		if (platformLaunched) {
			IConfigurationElement[] configurationElementsFor = Platform
					.getExtensionRegistry().getConfigurationElementsFor(
							"com.onpositive.semantic.model.metaContributor");
			for (final IConfigurationElement e : configurationElementsFor) {
				String name = e.getName();
				if (name.equals("keyResolver")) {
					GlobalAccess.addResolver(e.getAttribute("rootkey"),
							new ProxyResolver(e));
				}
				if (name.equals("contributor")) {
					String attribute = e.getAttribute("key");
					IKey stringToKey = GlobalAccess.stringToKey(attribute);
	
					MetaAccess.registerMetaContributor(stringToKey,
							new IMetaContributor() {
	
								IMetaContributor actual;
								boolean inited;
	
								public void contribute(IWritableMeta target) {
									if (actual != null) {
										actual.contribute(target);
									}
									if (!inited) {
										try {
											actual = (IMetaContributor) e
													.createExecutableExtension("contributorClass");
										} finally {
											inited = true;
										}
									}
								}
							});
	
				}
				if (name.equals("serviceProvider")) {
					String attribute = e.getAttribute("serviceClass");
					serviceMap.put(attribute, e);
				}
			}
		}
	}

	ArrayList<IServiceProvider<?>> prs = new ArrayList<IServiceProvider<?>>();

	public PlatformMetaContributorProvider() {
		MetaAccess.registerMetaContributor(
				GlobalAccess.stringToKey("class://"), this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getService(IHasMeta meta, Class<Object> serv,
			IHasMeta original) {
		
		if (serviceClassMap.containsKey(serv)) {
			IServiceProvider iServiceProvider = serviceClassMap.get(serv);
			if (iServiceProvider != null) {
				return iServiceProvider.getService(meta, serv, original);
			}
			return null;
		}
		if (serviceMap != null) {
			IConfigurationElement object = serviceMap.remove(serv.getName());
			if (object != null) {
				IServiceProvider<?> pr = (IServiceProvider<?>) object
						.createExecutableExtension("providerClass");
				serviceClassMap.put(serv, pr);
				if (pr != null) {
					return pr.getService(meta, (Class) serv, original);
				}
			}
			if (serviceMap.isEmpty()) {
				serviceMap = null;
			}
		}
		return null;
	}

	@SuppressWarnings("serial")
	public void contribute(IWritableMeta target) {
		final IServiceProvider<?> defaultServiceProvider = target
				.getDefaultServiceProvider();
		if (defaultServiceProvider != null && defaultServiceProvider != this) {
			target.setDefaultServiceProvider(new IServiceProvider<Object>() {

				@SuppressWarnings({ "unchecked", "rawtypes" })
				public Object getService(IHasMeta meta, Class<Object> serv,
						IHasMeta original) {
					Object service = this.getService(meta, serv, original);
					if (service != null) {
						return service;
					}
					return defaultServiceProvider.getService(meta,
							(Class) serv, original);
				}
			});
			return;
		}
		target.setDefaultServiceProvider(this);
	}

}
