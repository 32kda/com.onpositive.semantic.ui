package com.onpositive.commons.platform.configuration;

import com.onpositive.core.runtime.IPlatform;
import com.onpositive.core.runtime.IPlatformProvider;

public class PlatformConfigurationProvider implements IPlatformProvider {

	public IPlatform getPlatform(){
		return new EquinoxPlatformImpl();
	}
}
