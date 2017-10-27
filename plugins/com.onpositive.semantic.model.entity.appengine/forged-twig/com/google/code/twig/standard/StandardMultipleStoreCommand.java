package com.google.code.twig.standard;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.code.twig.StoreCommand.MultipleStoreCommand;

public class StandardMultipleStoreCommand<T> extends StandardCommonStoreCommand<T, StandardMultipleStoreCommand<T>> implements MultipleStoreCommand<T, StandardMultipleStoreCommand<T>>
{
	StandardMultipleStoreCommand(StandardStoreCommand command, Collection<? extends T> instances)
	{
		super(command);
		this.instances = instances;
	}

	public Future<Map<T, Key>> later()
	{
		return storeInstancesLater();
	}

	public Map<T, Key> now()
	{
		HashSet<T>ms=new HashSet<T>();
		// convert into entities ready to store
		Map<T, Entity> entities = instancesToEntities(ms);

		// actually put the entities in the datastore
		List<Key> keys = entitiesToKeys(entities.values());
		
		// make a map to return
		Map<T, Key> createKeyMapAndUpdateCache = createKeyMapAndUpdateCache(entities, keys);
		if (!ms.isEmpty()){
			StandardStoreCommand command2 = new StandardStoreCommand(datastore);
			command2.update=true;
			StandardMultipleStoreCommand<T> standardMultipleStoreCommand = new StandardMultipleStoreCommand<T>(command2, ms);
			
			standardMultipleStoreCommand.now();
		}
		return createKeyMapAndUpdateCache;
	}

	@Override
	public StandardMultipleStoreCommand<T> ids(String... ids)
	{
		return ids(Arrays.asList(ids));
	}

	@Override
	public StandardMultipleStoreCommand<T> ids(Long... ids)
	{
		return ids(Arrays.asList(ids));
	}

	@Override
	public StandardMultipleStoreCommand<T> ids(List<?> ids)
	{
		this.ids = ids;
		return this;
	}
}
