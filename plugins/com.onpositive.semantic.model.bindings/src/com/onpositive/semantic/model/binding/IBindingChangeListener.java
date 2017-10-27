package com.onpositive.semantic.model.binding;

import java.io.Serializable;

import com.onpositive.semantic.model.api.changes.ISetDelta;

public interface IBindingChangeListener<T> extends Serializable {

	void valueChanged(ISetDelta<T> valueElements);

	void enablementChanged(boolean isEnabled);

	void changed();

}
