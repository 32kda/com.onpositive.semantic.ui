package com.onpositive.businessdroids.ui.dataview.persistence;

public interface ISaveable {

	public void save(IStore store);

	public void load(IStore store) throws NoSuchElement;

}
