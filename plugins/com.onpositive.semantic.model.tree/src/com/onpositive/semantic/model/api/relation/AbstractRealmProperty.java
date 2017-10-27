package com.onpositive.semantic.model.api.relation;

import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.RealmCommandFactory;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.IterableValidator;

public abstract class AbstractRealmProperty implements IProperty {

	private static final long serialVersionUID = 1L;
	protected IProperty baseProperty;
	protected IPropertyProvider provider;
	protected ICommandFactory factory;
	protected BaseMeta meta;

	public AbstractRealmProperty(IProperty baseProperty,
			IPropertyProvider provider) {
		super();
		this.baseProperty = baseProperty;
		this.provider = provider;
		meta = new BaseMeta(baseProperty.getMeta());
		meta.registerService(ICommandFactory.class, new RealmCommandFactory(DefaultMetaKeys.getService(baseProperty,ICommandFactory.class)));
	}

	public IMeta getMeta() {
		return meta;
	}

	public String getId() {
		return this.baseProperty.getId();
	}

	public IValidator<?> getValidator(Object object) {

		return new IterableValidator();
	}

	public IPropertyProvider getPropertyProvider() {
		return this.provider;
	}

	public boolean isCollection() {
		return false;
	}
	

}
