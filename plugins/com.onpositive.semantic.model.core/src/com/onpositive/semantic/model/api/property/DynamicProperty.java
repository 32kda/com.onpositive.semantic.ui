package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.command.DefaultCommandFactory;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.IValidatorProvider;
import com.onpositive.semantic.model.api.validation.ValidatorProviderValidator;

public class DynamicProperty implements IProperty {

	private static final long serialVersionUID = 1L;

	private String name;

	public DynamicProperty(String intern) {
		this.name = intern;
	}

	static class DynamicCommandFactory extends DefaultCommandFactory {

		private static final long serialVersionUID = 1L;

		@Override
		public ICommand createCommand(Object type, Object value, String kind,
				IHasCommandExecutor property) {
			return PropertyAccess.createSetValueCommand(kind, type, value);
		}
	}
	@Override
	public String getId() {
		return name;
	}

	Class<?>lastClass;
	IProperty lastProp;
	
	@Override
	public Object getValue(Object obj) {
		if (obj==null){
			return null;
		}
		Class<? extends Object> class1 = obj.getClass();
		if (lastClass!=class1){
			lastProp=null;
			lastClass=class1;
		}
		if (lastProp==null){
			lastProp=PropertyAccess.getProperty(obj, name);
		}
		if (lastProp==null){
			return null;
		}
		return lastProp.getValue(obj);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DynamicProperty other = (DynamicProperty) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	static BaseMeta dynMeta = new BaseMeta();

	BaseMeta meta;

	static {
		dynMeta.registerService(ICommandFactory.class,
				new DynamicCommandFactory());

	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IMeta getMeta() {
		if (meta == null) {
			meta = new BaseMeta(dynMeta);
			meta.putMeta(DefaultMetaKeys.PROP_ID_KEY, name);
			meta.putMeta(DefaultMetaKeys.CAPTION_KEY, name);
			meta.registerService(IValidator.class,
					new ValidatorProviderValidator(
							new IValidatorProvider<Object>() {

								@Override
								public IValidator<Object> getService(
										Object object) {
									IProperty property = PropertyAccess
											.getProperty(object, name);
									if (property != null) {
										return DefaultMetaKeys.getService(
												property, IValidator.class);
									}
									return null;
								}
							}));			
		}
		return meta;
	}

	public boolean isCollection() {
		return false;
	}

}