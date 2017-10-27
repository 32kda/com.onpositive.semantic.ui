package com.onpositive.semantic.model.api.property.java;

import com.onpositive.semantic.model.api.globals.IKeyResolver;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IServiceProvider;

final class DefaultKeyResolverProvider implements
		IServiceProvider<IKeyResolver> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public IKeyResolver getService(final IHasMeta meta,
			Class<IKeyResolver> serv, IHasMeta original) {
		return new DefaultKeyResolver(meta);
	}
}