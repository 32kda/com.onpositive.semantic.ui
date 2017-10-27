package com.onpositive.semantic.editactions;

import java.util.Arrays;

import com.onpositive.semantic.model.ui.actions.Action;

public abstract class ObjectAction extends Action {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -8033439834079384269L;
	protected final Object[] baseObjects;

	public ObjectAction(Object[] baseObjects, int style) {
		super(style);
		this.baseObjects = baseObjects;
	}

	public ObjectAction(Object[] baseObjects) {
		super();
		this.baseObjects = baseObjects;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(baseObjects);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectAction other = (ObjectAction) obj;
		if (!Arrays.equals(baseObjects, other.baseObjects))
			return false;
		return true;
	}

}