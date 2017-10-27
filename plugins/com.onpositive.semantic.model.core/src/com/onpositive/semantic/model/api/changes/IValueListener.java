package com.onpositive.semantic.model.api.changes;

import java.io.Serializable;

public interface IValueListener<T> extends Serializable,IObjectListener{

	void valueChanged(T oldValue, T newValue);
}
