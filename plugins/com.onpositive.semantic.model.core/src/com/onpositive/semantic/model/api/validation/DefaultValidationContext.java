package com.onpositive.semantic.model.api.validation;

import java.util.Collections;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

public class DefaultValidationContext implements IValidationContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Iterable<IValidationContext> NO_CONTEXT = Collections
			.emptySet();
	private final Object value;
	private final IHasMeta meta;
	private final IValidationContext parentContext;
	private Object object;


	public DefaultValidationContext(Object value, IValidationContext parent,
			IHasMeta meta) {
		super();
		this.value = value;
		this.meta = meta;
		this.parentContext = parent;
	}
	public DefaultValidationContext(Object value, Object object,
			IValidationContext meta) {
		super();
		this.value = value;
		this.meta = meta;
		this.object = object;
		this.parentContext=meta;
	}
	
	public DefaultValidationContext(Object value, Object object,
			IHasMeta meta) {
		super();
		this.value = value;
		this.meta = meta;
		this.object = object;
		this.parentContext=null;
	}
	public DefaultValidationContext(Object value, Object object,
			IHasMeta meta,IValidationContext ct) {
		super();
		this.value = value;
		this.meta = meta;
		this.object = object;
		this.parentContext=ct;
	}

	public DefaultValidationContext(Object value) {
		super();
		this.value = value;
		this.meta = MetaAccess.getMeta(value);
		this.parentContext = null;
	}

	public DefaultValidationContext(Object value, IHasMeta meta) {
		super();
		this.value = value;
		this.meta = meta;
		this.parentContext = null;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public Object getObject() {
		if (object!=null){
			return object;
		}
		if (parentContext != null) {
			return parentContext.getValue();
		}
		return object;
	}

	@Override
	public IMeta getMeta() {
		return meta.getMeta();
	}


	@Override
	public Iterable<IValidationContext> getNestedContexts() {
		if (parentContext!=null){
			if (!DefaultMetaKeys.getValue(parentContext, DEEP_VALIDATION)
					&&!DefaultMetaKeys.getValue(parentContext, DefaultMetaKeys.MULTI_VALUE_KEY)){
				return NO_CONTEXT;
			}
		}
		IValidationContextProvider iValidationContextProvider = meta.getMeta()
				.getService(IValidationContextProvider.class);
		if (iValidationContextProvider != null) {
			return iValidationContextProvider.getNestedContexts(this);
		}
		return NO_CONTEXT;
	}


	@Override
	public IValidationContext getParent() {
		return parentContext;
	}


	@Override
	public Object getUnconvertedValue() {
		return value;
	}


	@Override
	public Iterable<Object> getRealm() {		
		return DefaultMetaKeys.getValue(meta, DefaultMetaKeys.REALM__KEY, Iterable.class);
	}

	@Override
	public String toString(){
		if (getParent()!=null){
			return getParent().toString()+"=>"+getValue();
		}
		return ""+getValue();		
	}
}