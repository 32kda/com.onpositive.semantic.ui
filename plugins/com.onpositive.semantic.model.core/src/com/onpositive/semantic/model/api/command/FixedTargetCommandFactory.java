package com.onpositive.semantic.model.api.command;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.IServiceProvider;
import com.onpositive.semantic.model.api.meta.IWritableMeta;

public class FixedTargetCommandFactory extends DefaultCommandFactory implements IHasCommandExecutor,IMeta,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object target;
	private IHasCommandExecutor executor;

	public IHasCommandExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(IHasCommandExecutor executor) {
		this.executor = executor;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	@Override
	public ICommand createCommand(Object type, Object value, String kind,
			IHasCommandExecutor property) {
		return new SimpleOneArgCommand(getTarget(), value, kind, getExecutor());
	}

	protected Object getTarget() {
		return target;
	}

	
	@Override
	public ICommandExecutor getCommandExecutor() {
		IHasCommandExecutor executor2 = getExecutor();
		if (executor2!=null){
			return executor2.getCommandExecutor();
		}
		return null;
	}

	
	@Override
	public ICommandFactory getCommandFactory() {
		return this;
	}

	
	@Override
	public <T> T getSingleValue(String key, Class<T> requestedClass, Object ctx) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public <T, A extends T> A getService(Class<T> requestedClass) {
		if (requestedClass==IHasCommandExecutor.class){
			return (A) this;
		}
		return null;
	}

	
	@Override
	public Collection<Object> keys() {
		return null;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	
	public Collection<Class<?>> services() {
		return (Collection)Collections.singleton(IHasCommandExecutor.class);
	}

	
	@Override
	public IMeta getParentMeta() {
		return null;
	}

	
	@Override
	public IMeta getDefaultMeta() {
		return null;
	}

	
	@Override
	public int getRevisionId() {
		return 0;
	}

	
	@Override
	public IWritableMeta getWritableCopy() {
		return null;
	}

	
	@Override
	public IServiceProvider<?> getDefaultServiceProvider() {
		return null;
	}
}
