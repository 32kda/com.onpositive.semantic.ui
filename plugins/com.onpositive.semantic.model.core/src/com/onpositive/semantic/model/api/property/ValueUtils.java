package com.onpositive.semantic.model.api.property;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.realm.IRealm;


public class ValueUtils {

	public static boolean hasValue(IProperty prop, Object obj) {
		if (prop instanceof IPropertyWithHasValue) {
			IPropertyWithHasValue pw = (IPropertyWithHasValue) prop;
			return pw.hasValue(obj);
		}
		Object value = prop.getValue(obj);
		return hasValue(value);
	}

	@SuppressWarnings("rawtypes")
	public static boolean hasValue(IProperty prop, Object obj, Object value) {
		if (prop instanceof IPropertyWithHasValue) {
			IPropertyWithHasValue pw = (IPropertyWithHasValue) prop;
			return pw.hasValue(obj, value);
		}
		Object va=prop.getValue(obj);
		if (va != null) {
			if (va instanceof Collection) {
				Collection c = (Collection) va;
				return c.contains(value);
			}
			if (va instanceof Object[]) {
				Object[] vl = (Object[]) va;
				for (int a = 0; a < vl.length; a++) {
					if (vl[a] != null) {
						if (vl[a] == value || vl[a].equals(value)) {
							return true;
						}
					} else {
						if (value == null) {
							return true;
						}
					}
				}
				return false;
			}
			if (va==value){
				return true;
			}
			return va.equals(value);			
		}
		return va==value;
	}

	@SuppressWarnings("rawtypes")
	public static boolean hasValue(Object value) {
		if (value != null) {
			if (value instanceof Collection) {
				Collection c = (Collection) value;
				return !c.isEmpty();
			}
			if (value instanceof Object[]) {
				Object[] vl = (Object[]) value;
				return vl.length != 0;
			}
			if (value instanceof String){
				return value.toString().trim().length()>0;
			}
			return true;
		}
		return false;
	}
	
	public static boolean toBoolean(Object value){
		if (value instanceof Boolean){
			return (Boolean) value;
		}
		if (value instanceof String){
			String str=(String) value;
			return Boolean.parseBoolean(str);
		}
		return false;		
	}
	
	public static Collection<Object> toCollection(Object value){
		Collection<Object> result = toCollectionIfCollection(value);
		if (result!=null){
			return result;
		}
		if (value!=null){
		return Collections.singleton(value);		
		}
		return Collections.emptySet();
	}

	@SuppressWarnings("unchecked")
	public static Collection<Object>  toCollectionIfCollection(Object value) {
		if (value==null){
			return null;
		}
		if (value instanceof Object[]){
			return Arrays.asList((Object[]) value);
		}
		if (value instanceof Collection){
			Collection<Object>m=(Collection<Object>) value;
			return new ArrayList<Object>(m);
		}
		if (value instanceof IRealm<?>){
			IRealm m=(IRealm) value;
			return m.getContents();
		}
		
		if (value instanceof Iterable){
			ArrayList<Object> result = new ArrayList<Object>() ;
			for( Object o : (Iterable)value )
				result.add(o) ;
			
			return result;
		}
		if (value.getClass().isArray()){
			int length = Array.getLength(value);
			ArrayList<Object>m=new ArrayList<Object>(length);
			for (int a=0;a<length;a++){
				m.add(Array.get(value, a));
			}
			return m;
		}
		return null;
	}

	

	
}
