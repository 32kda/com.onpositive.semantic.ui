package com.onpositive.businessdroids.model.impl.pojo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.onpositive.businessdroids.model.IColumnFactory;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.impl.Column;
import com.onpositive.businessdroids.ui.dataview.renderers.IEditableColumn;

public class POJOFactory implements IColumnFactory{

	private Class<?> clazz;

	public POJOFactory(Class<?> cl) {
		this.clazz=cl;
	}
	
	static Class<?> mapType(Class<?> returnType) {
		if (returnType.isPrimitive()){
			if (returnType==double.class){
				return Double.class;
			}
			if (returnType==char.class){
				return Character.class;
			}
			if (returnType==boolean.class){
				return Boolean.class;
			}
			if (returnType==int.class){
				return Integer.class;
			}
			if (returnType==float.class){
				return Float.class;
			}
			if (returnType==short.class){
				return Short.class;
			}
			if (returnType==long.class){
				return Long.class;
			}
		}
		return returnType;
	}

	public IField getField(Class<?> cl,String id){
		
		Field field = getFField(cl, id);
		if (field!=null){
			return new POJOField(field);
		}
		
		Method declaredMethod = getMethod(cl, "get"+id);
		if (declaredMethod==null){
			declaredMethod = getMethod(cl,"is"+id);	
		}
		if (declaredMethod!=null){
			Method setterMethod = getMethod(cl,"set"+id);
			POJOGetSet gs=new POJOGetSet(declaredMethod, setterMethod, id);
			return gs;
		}
		Class<?> superclass = cl.getSuperclass();
		if (superclass!=null&&superclass!=Object.class){
			return getField(superclass, id);
		}
		return null;		
	}

	protected Field getFField(Class<?> cl, String id){
		try{
		return cl.getDeclaredField(id);
		}catch (NoSuchFieldException e) {
			return null;
		}
	}

	protected Method getMethod(Class<?> cl, String id)
			 {
		try{
		return cl.getDeclaredMethod(id);
		}catch (NoSuchMethodException e) {
			try {
				Method method = cl.getMethod(id);
				return method;
			} catch (SecurityException e1) {
			} catch (NoSuchMethodException e1) {
			}
			return null;
		}
	}
	
	public IEditableColumn[] createColumns(String...ids){
		IEditableColumn[] cl=new IEditableColumn[ids.length];
		for (int a=0;a<cl.length;a++){
			IField field = getField(clazz, ids[a]);
			if (field==null){
				throw new IllegalStateException("Can not create column for field:"+ids[a]);
			}
			cl[a]=new Column(field);
			if (a==0){
				cl[a].setCaption(true);
			}
		}
		return cl;		
	}

	@Override
	public IEditableColumn createColumn(String id) {
		IField field = getField(clazz,id);
		if (field==null){
			throw new IllegalStateException("Can not create column for field:"+id);
		}
		return new Column(field);
	}
}
