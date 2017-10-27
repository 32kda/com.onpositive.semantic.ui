package com.onpositive.semantic.model.jobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.caucho.hessian.io.Hessian2Input;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.DeferredTaskContext;

public class DT implements DeferredTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	byte[] data;
	String key;

	@Override
	public void run() {
		IJob j = null;
		if (key != null) {
			try {
				Entity e = DatastoreServiceFactory.getDatastoreService().get(
						KeyFactory.stringToKey(key));
				Blob bl = (Blob) e.getProperty(JobManagerImpl.DATA);
				Hessian2Input i = new Hessian2Input(new ByteArrayInputStream(
						bl.getBytes()));
				j = (IJob) i.readObject();
			} catch (EntityNotFoundException e) {
				DeferredTaskContext.setDoNotRetry(true);
				throw new IllegalStateException();
			} catch (IOException e) {
				throw new IllegalStateException();
			}
		} else {
			Hessian2Input i = new Hessian2Input(new ByteArrayInputStream(data));
			try {
				j = (IJob) i.readObject();
			} catch (IOException e) {
				DeferredTaskContext.setDoNotRetry(true);
				throw new IllegalStateException();
			}
		}
		if (j.perform().isInProgress()) {
			JobManagerImpl.instance.schedule(j);
		}
	}

}
