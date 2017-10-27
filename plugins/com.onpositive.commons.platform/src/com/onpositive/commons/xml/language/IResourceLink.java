package com.onpositive.commons.xml.language;

import java.io.InputStream;

public interface IResourceLink {

	String location();
	InputStream openStream();
}
