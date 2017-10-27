package com.onpositive.semantic.realm;

import com.onpositive.semantic.model.api.roles.ImageDescriptor;


public interface INamedEntity {

	public abstract String id();

	public abstract String name();

	public abstract ImageDescriptor icon();

}