package com.onpositive.semantic.ui.core;

import java.io.Serializable;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;

public interface IConfigurable extends Serializable{

	void storeConfiguration(IAbstractConfiguration configuration);

	void loadConfiguration(IAbstractConfiguration configuration);

}
