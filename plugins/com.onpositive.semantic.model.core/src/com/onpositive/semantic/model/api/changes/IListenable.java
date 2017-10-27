package com.onpositive.semantic.model.api.changes;

import java.io.Serializable;

public interface IListenable extends Serializable{

	public void addValueListener(IValueListener<?> listener);

	public void removeValueListener(IValueListener<?> listener);
}