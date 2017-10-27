package com.onpositive.semantic.model.platform.registry;

//TODO IN PROGRESS
public class LabelRegistry extends AbstractPlatformServiceProvider<LabelObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LabelRegistry() {
		super("com.onpositive.semantic.model.labels", LabelObject.class);
	}

}
