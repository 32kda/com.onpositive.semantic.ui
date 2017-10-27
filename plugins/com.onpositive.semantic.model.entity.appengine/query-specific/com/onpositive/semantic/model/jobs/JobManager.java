package com.onpositive.semantic.model.jobs;

public final class JobManager {

	private JobManager(){
		
	}
	
	public static IJobManager getJobManager(){
		return JobManagerImpl.instance;
	}
}
