package com.onpositive.semantic.model.api.property;

import java.io.Serializable;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.IWritableMeta;

public abstract class DefaultProperty implements IProperty,Serializable {

	private static final long serialVersionUID = 1L;
	protected final String id;
	protected final IWritableMeta metadata;

	@Override
	public String toString() {
		return id;
	}

	public DefaultProperty(String id, IMeta metadata) {
		this.id = id;
		if (metadata instanceof IWritableMeta) {
			this.metadata = (IWritableMeta) metadata;
		} else {
			this.metadata = metadata.getWritableCopy();
		}
	}

	public DefaultProperty(String id) {
		super();
		this.id = id;
		BaseMeta baseMeta = new BaseMeta();
		this.metadata = baseMeta;
		baseMeta.putMeta(DefaultMetaKeys.ID_KEY, id);
		baseMeta.registerService(IProperty.class, this);
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public IMeta getMeta() {
		return metadata;
	}

	@Override
	public abstract Object getValue(Object obj);
	
	
	
}
