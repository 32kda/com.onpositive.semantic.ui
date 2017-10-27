package com.onpositive.semantic.model.java.tests;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IServiceProvider;

@SuppressWarnings("rawtypes")
public class TestServiceProvider implements IServiceProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TestServiceProvider() {

	}

	@Override
	public Object getService(IHasMeta meta, Class serv, IHasMeta original) {
		if (serv != ITestServiceClass.class) {
			throw new IllegalStateException();
		}
		if (DefaultMetaKeys.getSubjectClass(original) == BasicPlatformExtensionTest.class) {
			return new ITestServiceClass() {

				@Override
				public String sayHello() {
					return "Hello";
				}
			};
		}
		return null;
	}

}
