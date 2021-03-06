package com.google.code.twig.standard;

import java.util.Arrays;
import java.util.Collection;

import com.google.code.twig.StoreCommand;

public class StandardStoreCommand extends StandardCommand implements StoreCommand
{
	protected boolean update;

	StandardStoreCommand(TranslatorObjectDatastore datastore)
	{
		super(datastore);
	}

	public <T> StandardSingleStoreCommand<T> instance(T instance)
	{
		return new StandardSingleStoreCommand<T>(this, instance);
	}

	public <T> StandardMultipleStoreCommand<T> instances(Collection<? extends T> instances)
	{
		return new StandardMultipleStoreCommand<T>(this, instances);
	}
	
	public <T> StandardMultipleStoreCommand<T> instances(T... instances) 
	{
		return new StandardMultipleStoreCommand<T>(this, Arrays.asList(instances));
	}

	public StandardStoreCommand update(boolean update)
	{
		this.update = update;
		return this;
	}
}
