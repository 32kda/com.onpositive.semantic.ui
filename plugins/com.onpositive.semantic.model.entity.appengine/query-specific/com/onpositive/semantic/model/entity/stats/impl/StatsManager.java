package com.onpositive.semantic.model.entity.stats.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.property.java.annotations.Id;
import com.onpositive.semantic.model.entity.stats.IEntityStats;
import com.onpositive.semantic.model.entity.stats.IStatsManager;

public class StatsManager implements IStatsManager {

	private static final String HOLDER_DATA = "dta";

	public static class StatsHolder implements Serializable {

		@Id
		Long id;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected HashMap<String, IEntityStats> stats = new HashMap<String, IEntityStats>();
	}

	private StatsManager() {
	}

	StatsHolder holder;
	private final static Key createKey = KeyFactory.createKey("$$stats$$", 0);
	private static final int MAX_LENGTH = 1024 * 512;

	@Override
	public void processDelta(ISetDelta<Object> objectDelta) {
		aquireHolder();
	}

	private void aquireHolder() {
		if (holder == null) {
			try {

				Entity entity = DatastoreServiceFactory.getDatastoreService()
						.get(createKey);
				Blob property = (Blob) entity.getProperty(HOLDER_DATA);
				byte[] bytes = property.getBytes();
				Hessian2Input is = new Hessian2Input(new ByteArrayInputStream(
						bytes));
				try {
					holder = (StatsHolder) is.readObject();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			} catch (EntityNotFoundException e) {
				holder = new StatsHolder();
			}
		}
	}

	protected void storeHolder() {
		Entity em = new Entity(createKey);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Hessian2Output m = new Hessian2Output(byteArrayOutputStream);
		try {
			m.writeObject(holder);
			m.close();

			byte[] byteArray = byteArrayOutputStream.toByteArray();
			if (byteArray.length > MAX_LENGTH) {
				optimize();
			}
			em.setProperty(HOLDER_DATA, new Blob(byteArray));
			DatastoreServiceFactory.getDatastoreService().put(em);
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	private void optimize() {
		for (IEntityStats s: holder.stats.values()){			
		}
	}

	@Override
	public IEntityStats getStats(String kind) {
		aquireHolder();
		return holder.stats.get(kind);
	}

	static StatsManager manager;

	public static synchronized StatsManager getManager() {
		if (manager != null) {
			return manager;
		}
		manager = new StatsManager();
		return manager;
	}

}
