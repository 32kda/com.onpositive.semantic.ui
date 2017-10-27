package com.onpositive.semantic.model.data;

public class DataStoreChange {

	public final IEntry entry;
	public final String property;
	public final Object[] newValues;
	public final Object[] oldValues;

	public DataStoreChange(IEntry entry, Object[] newValues,
			Object[] oldValues, String property) {
		super();
		this.entry = entry;
		this.newValues = newValues;
		this.oldValues = oldValues;
		this.property = property;
	}

	public DataStoreChange revert() {
		return new DataStoreChange(this.entry, this.oldValues, this.newValues,
				this.property);
	}
}
