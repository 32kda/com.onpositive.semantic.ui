package com.onpositive.semantic.model.expressions.operatorimplementations;

import java.util.ArrayList;
import java.util.Collection;

import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.OrRealm;
import com.onpositive.semantic.model.api.realm.OrderedRealm;

public class CommaOperator extends BinaryOperator<Object, Object>{

	public CommaOperator() {
		super(CommaOperator.COMMA,Object.class, Object.class);		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object doGetValue(Object arg1, Object arg2) {
		if (arg1 instanceof IRealm<?>||arg2 instanceof IRealm<?>){
			OrRealm<Object>rm=new OrRealm<Object>();
			if (arg1 instanceof IRealm){
				rm.addRealm((IRealm) arg1);
			}
			else{
				Collection<Object> collection = ValueUtils.toCollection(arg1);
				rm.addRealm(new OrderedRealm<Object>(collection));
			}
			if (arg2 instanceof IRealm){
				rm.addRealm((IRealm) arg2);
			}
			else{
				Collection<Object> collection = ValueUtils.toCollection(arg2);
				rm.addRealm(new OrderedRealm<Object>(collection));
			}
			return rm;
		}
		Collection<Object> collection = ValueUtils.toCollection(arg1);
		Collection<Object> collection1 = ValueUtils.toCollection(arg2);
		ArrayList<Object>r=new ArrayList<Object>();
		r.addAll(collection);
		r.addAll(collection1);
		return r;
	}

}
