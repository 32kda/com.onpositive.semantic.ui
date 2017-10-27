package com.onpositive.semantic.model.api.meta;

public interface IWritableMeta extends IMeta{

	//public <T, A extends T> void putMeta(T serviceClass, A service) ;
	public void putMeta(String key, Object object) ;
	
	public <T, A extends T> void registerService(Class<T>servClazz, A object) ;
	public <T> void registerService(Class<T>servClazz, IServiceProvider<T> object) ;
	
	public void setParentMeta(IMeta meta);
	
	public void setDefaultMeta(IMeta meta);
	
	public void copyFrom(IMeta meta); 
	
	public void overrideService(Class<?>cl);
	
	public void setDefaultServiceProvider(IServiceProvider<?> defaultServiceProvider);
}
