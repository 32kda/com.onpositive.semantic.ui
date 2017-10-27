package com.onpositive.datamodel.core;

public class BatchChange {

	public IEntry[] toAdds;
	public IEntry[] toRemoves;
	public DataStoreChange[] changes;

	public BatchChange(IEntry[] toAdds, IEntry[] toRemoves,
			DataStoreChange[] changes) {
		this.toAdds = toAdds;
		this.toRemoves = toRemoves;
		this.changes = changes;
	}
}