package com.onpositive.semantic.model.api.command;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.validation.DefaultValidationContext;
import com.onpositive.semantic.model.api.validation.IHasValidationContext;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.ValidationAccess;
import com.onpositive.semantic.model.api.validation.ValidatorAdapter;

public class SimpleOneArgCommand extends BaseMeta implements ICommand,IHasValidationContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final BaseMeta defaultCommandMeta = new BaseMeta();

	static {
		ValidatorAdapter<SimpleOneArgCommand> object = new ValidatorAdapter<SimpleOneArgCommand>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public CodeAndMessage isValid(IValidationContext context,
					SimpleOneArgCommand value) {
				if (value.kind == null) {
					return CodeAndMessage.errorMessage("kind is null " + this);
				}
				IHasMeta meta = MetaAccess
						.getMeta(value.extra != null ? value.extra
								: value.target);
				if (PropertyAccess.isReadonly(meta, value.getTarget())) {
					return CodeAndMessage.errorMessage(DefaultMetaKeys
							.getCaption(context) + " is readonly");
				}

				if (value.kind.equals(ICommand.SET_VALUE)
						|| value.kind.equals(ICommand.SET_VALUES)
						|| value.kind.equals(ICommand.ADD_VALUE)) {
					CodeAndMessage value2 = ValidationAccess.validateSubjectClass(value.value,
							meta);
					if (!value2.isError()) {
						BaseMeta baseMeta = new BaseMeta();
						baseMeta.setParentMeta(meta.getMeta());
						//FIXME
						baseMeta.putMeta(IValidationContext.DEEP_VALIDATION, false);
						DefaultValidationContext cm = new DefaultValidationContext(
								value.value, value.target, baseMeta, context);
						CodeAndMessage validate = ValidationAccess.validate(cm);
						return validate;
					}
					return value2;
				}
				if (value.kind.equals(ICommand.REMOVE_VALUE)) {
					return CodeAndMessage.OK_MESSAGE;
				}				
				return CodeAndMessage.OK_MESSAGE;
			}

			

		};
		defaultCommandMeta.registerService(IValidator.class, object);
		defaultCommandMeta.putMeta(IValidationContext.DEEP_VALIDATION, false);
		defaultCommandMeta.lock();

	}

	private final Object target;
	private final Object value;
	private final String kind;
	private final IHasCommandExecutor extra;

	public SimpleOneArgCommand(Object target, Object toAdd, String kind,
			IHasCommandExecutor extra) {
		super();
		this.target = target;
		this.value = toAdd;
		this.kind = kind;
		this.extra = extra;		
		setParentMeta(defaultCommandMeta);
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return this.kind;
	}

	public Object getTarget() {
		return this.target;
	}

	public Object getValue() {
		return this.value;
	}

	@Override
	public String getKind() {
		return this.kind;
	}

	@Override
	public ICommandExecutor getCommandExecutor() {
		if (extra != null) {
			return extra.getCommandExecutor();
		}
		if (target instanceof ICommandExecutor) {
			return (ICommandExecutor) target;
		}
		return null;
	}

	@Override
	public IHasCommandExecutor getOwner() {
		return extra;
	}

	@Override
	public IValidationContext getValidationContext() {
		return new DefaultValidationContext(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((extra == null) ? 0 : extra.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleOneArgCommand other = (SimpleOneArgCommand) obj;
		if (extra == null) {
			if (other.extra != null)
				return false;
		} else if (!extra.equals(other.extra))
			return false;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	
	@Override
	public IMeta getMeta() {		
		return this;
	}

	public SimpleOneArgCommand createUndoCommand(){
		if (kind.equals(SimpleOneArgCommand.UP_VALUE)){
			return new SimpleOneArgCommand(target, value, SimpleOneArgCommand.DOWN_VALUE, extra);
		}
		if (kind.equals(SimpleOneArgCommand.DOWN_VALUE)){
			return new SimpleOneArgCommand(target, value, SimpleOneArgCommand.UP_VALUE, extra);
		}
		if (kind.equals(SimpleOneArgCommand.ADD_VALUE)||kind.equals(SimpleOneArgCommand.ADD)){
			return new SimpleOneArgCommand(target, value, SimpleOneArgCommand.REMOVE_VALUE, extra);
		}
		if (kind.equals(SimpleOneArgCommand.REMOVE_VALUE)||kind.equals(SimpleOneArgCommand.DELETE)){
			return new SimpleOneArgCommand(target, value, SimpleOneArgCommand.ADD, extra);
		}
		if (kind.equals(SimpleOneArgCommand.SET_VALUE)){
			IProperty property=(IProperty) extra;
			Object value2 = property.getValue(target);
			return new SimpleOneArgCommand(target, value2, SimpleOneArgCommand.SET_VALUE, extra);
		}
		throw new IllegalStateException();		
	}
}
