package com.onpositive.semantic.model.api.meta;

public interface IServiceProvider<T> extends IService{
	
	T getService(IHasMeta meta,Class<T>serv, IHasMeta original);
	
}
