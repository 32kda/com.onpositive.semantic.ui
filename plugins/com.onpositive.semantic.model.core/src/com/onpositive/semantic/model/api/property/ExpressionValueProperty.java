package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.command.DefaultCommandFactory;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.GetPropertyLookup;
import com.onpositive.semantic.model.api.expressions.IEditableExpression;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.VariableExpression;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IWritableMeta;

public class ExpressionValueProperty extends ComputedProperty implements IValueListener<Object>,ITargetDependentReadonly{

	private static final long serialVersionUID = 1L;


	@SuppressWarnings("rawtypes")
	private final class ReadonlyExp extends
			AbstractListenableExpression<Object> implements IValueListener{
		
		private static final long serialVersionUID = 1L;
		private IListenableExpression<?> binding;

		private ReadonlyExp(IListenableExpression<?> binding) {
			this.binding = binding;
			binding.addValueListener(this);
			setNewValue(isReadonly(null, binding.getValue()));
		}

		
		@Override
		public void valueChanged(Object oldValue, Object newValue) {
			setNewValue(isReadonly(null, binding.getValue()));
		}
		
		@Override
		public void disposeExpression() {
			binding.removeValueListener(this);
			binding.disposeExpression();
			super.disposeExpression();
		}
	}

	private String expressionString;

	public ExpressionValueProperty(IEditableExpression<?>r, IListenableExpression<?>expr) {
		super("","");
		this.expr=r;
		this.expression=expr;
		IWritableMeta meta = (IWritableMeta) getMeta();
		meta.registerService(ITargetDependentReadonly.class, this);
		meta.putMeta(DefaultMetaKeys.READ_ONLY_KEY, null);
		if (expression!=null){
			registerCommandFactory(meta);
		}		
	}
	
	public ExpressionValueProperty(String expression, IClassResolver resolver) {
		super(expression, expression);
		this.expressionString = expression;
		this.resolver = resolver;
		IWritableMeta meta = (IWritableMeta) getMeta();
		meta.registerService(ITargetDependentReadonly.class, this);
		meta.putMeta(DefaultMetaKeys.READ_ONLY_KEY, null);
	}

	public ExpressionValueProperty(String id, String exp,
			IClassResolver resolver) {
		super(id, id);
		this.resolver = resolver;
		this.expressionString = exp;	
		IWritableMeta meta = (IWritableMeta) getMeta();
		meta.registerService(ITargetDependentReadonly.class, this);
		meta.putMeta(DefaultMetaKeys.READ_ONLY_KEY, null);
	}

	protected IListenableExpression<?> expression;
	
	protected IListenableExpression<?>parentContext;

	public IListenableExpression<?> getParentContext() {
		return parentContext;
	}

	public void setParentContext(IListenableExpression<?> parentContext) {
		this.parentContext = parentContext;
		if (parentContext instanceof IClassResolver&&resolver==null){
			resolver=(IClassResolver) parentContext;
		}
		PropertyAccess.firePropertyStructureListener(this);
	}

	public String getExpressionString() {
		return expressionString;
	}

	public void setExpressionString(String expressionString) {
		this.expressionString = expressionString;
		this.expression = null;
		PropertyAccess.firePropertyStructureListener(this);
	}

	protected IEditableExpression expr;
	private IClassResolver resolver;

	
	@Override
	public synchronized Object getValue(Object obj) {
		if (expression == null) {
			VariableExpression p = new VariableExpression();
			expr = p;
			p.setValue(obj);
			GetPropertyLookup environment = new GetPropertyLookup(p, resolver);
			environment.setParentContext(parentContext);
			expression = ExpressionAccess.parse(expressionString,
					environment);
			for (IListenableExpression<?>z:environment.parsed()){
				if (z!=p&&z!=null){
					//TODO review me
					ObjectChangeManager.addWeakListener(z, this);
				}
			}
			for (IListenableExpression<?>z:environment.fromParent()){
				if (z!=p&&z!=null){
					z.addValueListener(this);
				}
			}
			IWritableMeta ma = updateMeta();
			registerCommandFactory(ma);
		} else {
			if (expr!=null){
				if (obj==expr.getValue()){
					expr.setValue(null);
				}
				expr.setValue(obj);
			}
		}
		if (expression == null) {
			return getId();
		}
		return expression.getValue();
	}

	public void registerCommandFactory(IWritableMeta ma) {
		ma.registerService(ICommandFactory.class, new DefaultCommandFactory(){
			
			@Override
			public ICommand createCommand(Object type, Object value,
					String kind, IHasCommandExecutor property) {
				//if (type!=null){
					getValue(type);
					updateMeta();
				//}						
				property=DefaultMetaKeys.getService(ExpressionValueProperty.this.getMeta(),
						IHasCommandExecutor.class);
				if (property!=null){
				return property.getCommandFactory().createCommand(type, value, kind, property);
				}
				throw new IllegalStateException("executor is null "+getId());
			}
			
		});
	}

	public IWritableMeta updateMeta() {
		IWritableMeta ma= (IWritableMeta) getMeta();
		ma.putMeta(DefaultMetaKeys.READ_ONLY_KEY, isReadOnly());
		if (expression instanceof IEditableExpression){
			ma.setDefaultMeta(((IHasMeta) expression).getMeta());
		}
		else{
			ma.setDefaultMeta(null);
		}
		return ma;
	}

	public boolean isReadOnly() {
		if (expression instanceof IEditableExpression){
			IEditableExpression<?>x=(IEditableExpression<?>) expression;
			return x.isReadOnly();
		}
		return true;
	}

	
	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		PropertyAccess.firePropertyStructureListener(this);
	}

	
	@Override
	public boolean isReadonly(IHasMeta meta, Object object) {
		getValue(object);
		updateMeta();
		return isReadOnly();
	}

	public void setExpression(IListenableExpression<?> parse) {
		this.expression = parse;
		PropertyAccess.firePropertyStructureListener(this);
	}

	
	@Override
	public IListenableExpression<?> buildReadonlyExpression(IHasMeta meta,
			IExpressionEnvironment env) {
		final IListenableExpression<?> binding = env.getBinding("this");
		return new ReadonlyExp(binding);
	}

}
