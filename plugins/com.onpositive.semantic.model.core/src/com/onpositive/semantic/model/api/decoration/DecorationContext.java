package com.onpositive.semantic.model.api.decoration;

import java.io.Serializable;

import com.onpositive.semantic.model.api.meta.IHasMeta;

public class DecorationContext implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Object object;
	public String role;
	public String theme;
	public IHasMeta binding;

	public DecorationContext(Object object, String role, String theme) {
		this.object = object;
		this.role = role;
		this.theme = theme;
	}

	public DecorationContext() {
	}
}