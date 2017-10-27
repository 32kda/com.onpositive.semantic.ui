package com.onpositive.semantic.model.api.factory;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.property.IFunction;

public abstract class AbstractFactory implements IFunction {

	private static final long serialVersionUID = 1L;
	protected IMeta meta;

	public AbstractFactory(IMeta meta) {
		super();
		this.meta=meta;
	}
	public AbstractFactory(String name,String description) {
		super();
		BaseMeta baseMeta = new BaseMeta();
		baseMeta.putMeta(DefaultMetaKeys.CAPTION_KEY, name);
		baseMeta.putMeta(DefaultMetaKeys.DESCRIPTION_KEY, description);
		this.meta=baseMeta;
	}

	@Override
	public IMeta getMeta() {
		return meta;
	}
}
