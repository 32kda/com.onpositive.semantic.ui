package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.command.FixedTargetCommandFactory;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

public class VariableExpression extends AbstractListenableExpression<Object> implements IEditableExpression<Object>,ISubsitutableExpression<Object>{

	private static final long serialVersionUID = 1L;
	BaseMeta meta=new BaseMeta();
	FixedTargetCommandFactory ts = new FixedTargetCommandFactory();
	{
		meta.setDefaultMeta(ts);
		ts.setExecutor(new EditableExpressionExecutor(this));
	}
	public VariableExpression() {
	
	}
	
	@Override
	public IMeta getMeta() {
		if (meta.getParentMeta()==null){
			meta.setParentMeta(MetaAccess.getMeta(value).getMeta());			
		}		
		return meta;
	}

	
	@Override
	public void setValue(Object value) {
		meta.setParentMeta(null);
		ts.setTarget(value);
		setNewValue(value);
	}

	
	@Override
	public boolean isReadOnly() {
		return false;
	}


	@Override
	public ISubsitutableExpression<Object> substituteAllExcept(
			IListenableExpression<?> ve) {
		if (ve!=this){
			return new ConstantExpression(this.getValue());
		}
		return this;
	}


	@Override
	public boolean isConstant() {
		return false;
	}

}
