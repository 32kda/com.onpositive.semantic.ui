package com.onpositive.semantic.model.api.realm;

public interface IPotentiallyIncomplete {

	Integer potentialSize();

	boolean isIncompleteDataHere();
	
	void loadMoreData();

	int size();
}
