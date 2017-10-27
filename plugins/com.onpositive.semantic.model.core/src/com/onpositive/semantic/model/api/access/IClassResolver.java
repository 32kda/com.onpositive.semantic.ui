package com.onpositive.semantic.model.api.access;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.onpositive.semantic.model.api.meta.IService;

public interface IClassResolver extends IService{
	
	public Class<?> resolveClass(String className);
	
	public URL resolveResource(String className);

	public InputStream openResourceStream(String path) throws IOException;
}
