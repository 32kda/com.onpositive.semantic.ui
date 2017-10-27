package com.onpositive.semantic.model.jobs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.hessian.io.Hessian2Output;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class JobManagerImpl extends HttpServlet implements IJobManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final String DATA = "data";
	static final String DATA_KEY = "data_key";
	public static JobManagerImpl instance=new JobManagerImpl();
	
	private JobManagerImpl(){
		
	}

	public void schedule(IJob job) {
		try {
			
			ByteArrayOutputStream os2 = new ByteArrayOutputStream();
			Hessian2Output os = new Hessian2Output(os2);
			os.writeObject(job);
			os.close();
			DT tt=new DT();
			byte[] byteArray = os2.toByteArray();
			if (byteArray.length > 10000) {
				if (byteArray.length > 800000) {
					throw new IllegalStateException("Job data is too big:" + job);
				}
				Entity e = new Entity("$$job_data");
				e.setProperty(DATA,new Blob(byteArray));
				Key c = DatastoreServiceFactory.getDatastoreService().put(e);
				tt.key=KeyFactory.keyToString(c);				
			} else {
				tt.data=byteArray;
			}
			TaskOptions withDefaults = TaskOptions.Builder.withPayload(tt);			
			withDefaults.retryOptions(RetryOptions.Builder.withTaskRetryLimit(
					100).taskAgeLimitSeconds(24 * 3600 * 7));
			//withDefaults.url("/jobmanager");
			withDefaults.method(Method.POST);
			QueueFactory.getDefaultQueue().add(withDefaults);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		super.doPost(req, resp);
	}
}
