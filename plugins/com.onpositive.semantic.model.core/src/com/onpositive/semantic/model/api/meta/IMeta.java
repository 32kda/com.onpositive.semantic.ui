package com.onpositive.semantic.model.api.meta;

import java.io.Serializable;
import java.util.Collection;

public interface IMeta extends IHasMeta, Serializable {

	public <T> T getSingleValue(String key, Class<T> requestedClass, Object ctx);

	public <T, A extends T> A getService(Class<T> requestedClass);

	public Collection<Object> keys();

	public Collection<Class<?>> services();

	IMeta getParentMeta();

	IMeta getDefaultMeta();

	int getRevisionId();

	public IWritableMeta getWritableCopy();

	public IServiceProvider<?> getDefaultServiceProvider();
}
