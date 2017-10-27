package com.onpositive.semantic.model.api.expressions;

import java.util.Collection;
import java.util.HashMap;

import com.onpositive.semantic.model.api.access.IClassResolver;

public class GetPropertyLookup implements IExpressionEnvironment {

	protected IListenableExpression<?> root;
	
	public GetPropertyLookup(IListenableExpression<?> root, IClassResolver resolver) {
		super();
		this.root = root;
		this.resolver = resolver;
	}

	protected IClassResolver resolver;
	@SuppressWarnings("rawtypes")
	protected HashMap<String, IListenableExpression>map=new HashMap<String, IListenableExpression>();
	
	@SuppressWarnings("rawtypes")
	protected HashMap<String, IListenableExpression>fromParent=new HashMap<String, IListenableExpression>();
	private IListenableExpression<?> parentContext;
	protected IExpressionEnvironment penv;
	
	public IExpressionEnvironment getParentEnvironment() {
		return penv;
	}
	public void setParentEnvironment(IExpressionEnvironment penv) {
		this.penv = penv;
	}
	@SuppressWarnings("rawtypes")
	public Collection<IListenableExpression>parsed(){
		return map.values();
	}
	@SuppressWarnings("rawtypes")
	public Collection<IListenableExpression>fromParent(){
		return fromParent.values();
	}

	@Override
	@SuppressWarnings("rawtypes")
	
	public IListenableExpression<?> getBinding(String path) {
		
		IListenableExpression iListenableExpression = map.get(path);
		if (iListenableExpression!=null){
			return iListenableExpression;
		}
		IListenableExpression<?> internalGet = internalGet(path);
		map.put(path, internalGet);
		return internalGet;
	}

	protected IListenableExpression<?> internalGet(String path) {
		IListenableExpression<?> m = root;
		if (path.startsWith("$")){
			if (penv==null){
				if (parentContext instanceof IExpressionEnvironment){
					penv=(IExpressionEnvironment) parentContext;
				}
				else{
				penv=new GetPropertyLookup(parentContext, resolver);
				}
			}
			if (path.length()==1){
				fromParent.put(path,parentContext);
				return parentContext;
			}
			IListenableExpression<?> binding = penv.getBinding(path.substring(2));
			if (binding!=null){
				fromParent.put(path, binding);
			}
			return binding;
		}
		while (path.length() > 0) {
			int indexOf = path.indexOf('.');
			if (indexOf == -1) {
				indexOf = path.length();
			}
			String substring = path.substring(0, indexOf);
			if (!substring.equals("this")){			
			m = new GetPropertyExpression(substring, m);
			}
			if (indexOf!=path.length()){
			path=path.substring(indexOf+1);
			}
			else{
				break;
			}
		}
		return m;
	}

	
	@Override
	public IClassResolver getClassResolver() {
		return resolver;
	}

	public void setParentContext(IListenableExpression<?> parentContext) {
		this.parentContext=parentContext;
	}

}
