package com.onpositive.semantic.model.api.meta;

public interface IServiceRegistrator extends IService{

	public void registerService(IWritableMeta meta,Class<?>servClass,Object value);
}
