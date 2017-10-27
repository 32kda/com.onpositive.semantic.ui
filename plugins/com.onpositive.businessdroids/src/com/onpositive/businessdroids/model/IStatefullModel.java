package com.onpositive.businessdroids.model;

public interface IStatefullModel {

	int STATE_DATA_READY=0;
	
	int STATE_SYNCING=1;
	
	int STATE_MORE_DATA_ON_REQUEST=2;
	
	int STATE_NO_DATA_SYNC_INPROGRESS=1000;	
	
	public int getModelState();
		
}
