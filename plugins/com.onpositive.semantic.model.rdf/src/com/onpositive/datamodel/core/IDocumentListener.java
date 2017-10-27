package com.onpositive.datamodel.core;

public interface IDocumentListener {

	void outcomingChangesAvailable();

	void documentInSyncNow();

	void incomingChangesAvailable();
}
