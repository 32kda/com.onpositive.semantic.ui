package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.command.FixedTargetCommandFactory;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.property.IContextDependingProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;

public class GetPropertyExpression extends AbstractListenableExpression<Object>
		implements IValueListener<Object>, IEditableExpression<Object>,ISubsitutableExpression<Object>,ICanWriteToQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String id;
	protected IListenableExpression<?> parent;
	private IProperty property;

	IValueListener<Object> propListener = new IValueListener<Object>() {

		
		@Override
		public void valueChanged(Object oldValue, Object newValue) {
			GetPropertyExpression.this.valueChanged(null, null);
			ObjectChangeManager.markChanged(GetPropertyExpression.this);
		}
	};
	private BaseMeta meta;
	private static BaseMeta baseMeta = new BaseMeta();
	private FixedTargetCommandFactory ts;
	static {
		baseMeta.putMeta(DefaultMetaKeys.READ_ONLY_KEY, true);
		baseMeta.lock();
	}
	public GetPropertyExpression(IProperty id, IListenableExpression<?> parent) {
		this.property=id;
		update(parent);
		this.meta.setDefaultMeta(id.getMeta());
	}

	public GetPropertyExpression(String id, IListenableExpression<?> parent) {
		super();
		this.id = id;
		update(parent);
	}

	protected void update(IListenableExpression<?> parent) {
		this.parent = parent;
		this.meta = new BaseMeta(baseMeta);
		ts = new FixedTargetCommandFactory() {
			@Override
			public IHasCommandExecutor getExecutor() {
				if (property != null) {
					IHasCommandExecutor service = DefaultMetaKeys.getService(
							property.getMeta(), IHasCommandExecutor.class);
					if (service != null) {
						return service;
					}
				}
				return null;

			};
		};
		
		valueChanged(null, null);
		parent.addValueListener(this);
		meta.registerService(IHasCommandExecutor.class,
				ts);
		meta.registerService(ICommandFactory.class, ts.getCommandFactory());
	}

	public String getPropertyId(){
		return id;
	}
	
	public IListenableExpression<?>getParent(){
		return parent;
	}
	
	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		if (parent==null){
			return;
		}
		Object value2 = parent.getValue();
		ts.setTarget(value2);
		updateProperty(value2);
		if (this.property != null) {
			if (property instanceof IContextDependingProperty){
				IContextDependingProperty d=(IContextDependingProperty) property;
				if (parent instanceof GetPropertyExpression){
					GetPropertyExpression r=(GetPropertyExpression) parent;
					this.setNewValue(d.getValue(r, r.parent.getValue(), r.getValue()));
				}
				return;
			}
			try{
			this.setNewValue(property.getValue(value2));
			}catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		} else {
			setNewValue(null);
		}
	}

	protected void updateProperty(Object value2) {
		ObjectChangeManager.addWeakListener(value2, this);
		IProperty property2 =id!=null? PropertyAccess.getProperty(value2, id):property;
		if (property2 != this.property) {
			if (property != null) {
				PropertyAccess.removePropertyStructureListener(property,
						propListener);
			}
			property = property2;
			if (property != null) {
				PropertyAccess.addPropertyStructureListener(property,
						propListener);
			}
			if (property2!=null){
				this.meta.setDefaultMeta(property2.getMeta());
			}
			else{
				this.meta.setDefaultMeta(null);
			}
		}
		
	}

	
	@Override
	public void disposeExpression() {
		parent.removeValueListener(this);
		if (property != null) {
			PropertyAccess.removePropertyStructureListener(property,
					propListener);
		}
	}

	
	@Override
	public IMeta getMeta() {
		return meta;
	}

	
	@Override
	public void setValue(Object value) {
		PropertyAccess.setValue(property, parent.getValue(), value);
	}

	
	@Override
	public boolean isReadOnly() {
		return PropertyAccess.isReadonly(property, parent.getValue());
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISubsitutableExpression<Object> substituteAllExcept(
			IListenableExpression<?> ve) {
		if (parent instanceof ISubsitutableExpression){
			ISubsitutableExpression<Object>o=(ISubsitutableExpression<Object>) parent;
			ISubsitutableExpression<Object> substituteAllExcept = o.substituteAllExcept(ve);
			if (substituteAllExcept!=null){
				GetPropertyExpression getPropertyExpression = new GetPropertyExpression(this.id, substituteAllExcept);
				if (getPropertyExpression.isConstant()){
					return new ConstantExpression(getPropertyExpression.getValue());
				}
				return getPropertyExpression;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isConstant() {
		if (parent instanceof ISubsitutableExpression){
			ISubsitutableExpression<Object>o=(ISubsitutableExpression<Object>) parent;
			return o.isConstant();
		}
		return false;
	}

	@Override
	public boolean modify(Query q) {
		q.addFilter(new QueryFilter(getPropertyId(), true,QueryFilter.FILTER_EQUALS));
		return true;
	}
}
