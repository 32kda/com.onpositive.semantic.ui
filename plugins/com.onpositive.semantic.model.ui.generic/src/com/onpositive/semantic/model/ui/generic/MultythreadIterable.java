package com.onpositive.semantic.model.ui.generic;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class MultythreadIterable<T> implements Serializable {
	
	private LinkedHashSet<T> hashSet ;
	private T[] array ;
	private Class<?> objectClass ;

	public MultythreadIterable( Class<?> objectClass ) {
		hashSet = new LinkedHashSet<T>() ;
		this.objectClass = objectClass ;
	}
	
	public T[] getArray()
	{
		T[] arrayTmp = array ;
		if( arrayTmp == null )
		{
			arrayTmp = hashSet.toArray( (T[])Array.newInstance( objectClass, 0 ) ) ;
			array = arrayTmp ;
		}
		return arrayTmp ;
	}
	
	public boolean add( T e )
	{
		if( hashSet.add(e) ){
			array = null ;
			return true ;
		}		
		return false ;		
	}
	public boolean remove( T o )
	{
		if( hashSet.remove(o) ){
			array = null ;
			return true ;
		}		
		return false ;		
	}
	
	
	
}
