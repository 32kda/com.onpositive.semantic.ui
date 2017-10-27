package com.onpositive.commons.xml.language;

import java.util.HashMap;
import java.util.regex.Pattern;

import com.onpositive.commons.platform.registry.ServiceMap;
import com.onpositive.commons.platform.registry.ServiceObject;

public class HandlingTypeConvertor {
	
	static{
		StaticTypeConvertor = new HandlingTypeConvertor() ;
	}
	
	static HandlingTypeConvertor StaticTypeConvertor ;
	
	public static HandlingTypeConvertor getInstance(){
		
		if ( StaticTypeConvertor == null )
			StaticTypeConvertor = new HandlingTypeConvertor() ;
		
		return StaticTypeConvertor ;
	}
	
	HashMap<String, AbstractPrimitivesConvertor> primitiveStringConversionMap ;
	HashMap<String, Integer> numberConversionMap ;
	HashMap<String, Class<?>> wrappingMap ;
	ServiceMap<ServiceObject<AbstractUnitConvertor>> stringConversionServiceMap ;
	
	private HandlingTypeConvertor(){

		primitiveStringConversionMap  = new HashMap<String, HandlingTypeConvertor.AbstractPrimitivesConvertor>() ;
		numberConversionMap  = new HashMap<String, Integer>() ;
		stringConversionServiceMap = new ServiceMap<ServiceObject<AbstractUnitConvertor>>(
							   "com.onpositive.commons.platform.typeConversion", ServiceObject.class ) ;
		
		wrappingMap = new HashMap<String, Class<?>>() ;

		numberConversionMap.put( Byte.class.getName(),		10 ) ;
		numberConversionMap.put( Character.class.getName(), 20 ) ;
		numberConversionMap.put( Short.class.getName(),		30 ) ;
		numberConversionMap.put( Integer.class.getName(),	40 ) ;		
		numberConversionMap.put( Long.class.getName(),		50 ) ;
		numberConversionMap.put( Float.class.getName(),		60 ) ;
		numberConversionMap.put( Double.class.getName(),	70 ) ;		

		primitiveStringConversionMap.put( Integer.class.getName(), new IntegerConvertor() ) ;
		primitiveStringConversionMap.put( Short.class.getName(), new ShortConvertor() ) ;
		primitiveStringConversionMap.put( Byte.class.getName(), new ByteConvertor() ) ;
		primitiveStringConversionMap.put( Long.class.getName(), new LongConvertor() ) ;
		primitiveStringConversionMap.put( Double.class.getName(), new DoubleConvertor() ) ;
		primitiveStringConversionMap.put( Float.class.getName(), new FloatConvertor() ) ;
		primitiveStringConversionMap.put( Character.class.getName(), new CharConvertor() ) ;
		primitiveStringConversionMap.put( Boolean.class.getName(), new BooleanConvertor() ) ;
		primitiveStringConversionMap.put( String.class.getName(), new StringConvertor() ) ;
		primitiveStringConversionMap.put( int[].class.getName(), new IntArrayConvertor() ) ;
		//primitiveStringConversionMap.put( Integer[].class.getName(), new IntArrayConvertor() ) ;
		
		wrappingMap.put( int.class.getName()    , Integer.class	  ) ;
		wrappingMap.put( short.class.getName()  , Short.class	  ) ;
		wrappingMap.put( byte.class.getName()   , Byte.class	  ) ;
		wrappingMap.put( long.class.getName()   , Long.class	  ) ;	
		wrappingMap.put( double.class.getName() , Double.class	  ) ;
		wrappingMap.put( float.class.getName()  , Float.class     ) ;
		wrappingMap.put( char.class.getName()   , Character.class ) ;
		wrappingMap.put( boolean.class.getName(), Boolean.class   ) ;				
	}
	public boolean convertorIsPresent( Class<?> clazz )
	{
		return clazz == String.class || checkPrimitive( clazz ) || this.stringConversionServiceMap.get(clazz) != null ;
	}
	
	public Object convert( Object value, Class<?> targetClass, Context context ){
		
		if( targetClass.isPrimitive() )
			targetClass = wrappingMap.get( targetClass.getName() ) ;			
		
		Object result = doConvert( value, targetClass, context ) ;
		
		if( result == null&&value!=null )
			System.err.print( "Cannot convert type \'" + targetClass.getName() + "\' to type \'" + (value!=null?value.getClass().getName():"null") +"\';\n" ) ;			
		try{
			return targetClass.cast(result);
		}
		catch(  ClassCastException e){
			e.printStackTrace();
			Activator.log(e);
			System.err.print( "Cannot convert type \'" + targetClass.getName() + "\' to type \'" + value.getClass().getName() +"\';\n" ) ;
			return null ;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object doConvert( Object value, Class<?> targetClass, Context context )
	{
		if (targetClass.isInstance( value ))
			return value;
		if (value==null){
			return null;
		}
		if( checkPrimitive( value.getClass() ) && checkPrimitive( targetClass ) )
			return convertPrimitive( value, targetClass ) ;
		
		AbstractUnitConvertor convertor = null ;
		
		if( targetClass.equals(String.class) )
		{
			convertor = primitiveStringConversionMap.get( value.getClass().getName() ) ;
			if( convertor == null ){
				ServiceObject<AbstractUnitConvertor> serviceObject = stringConversionServiceMap.get( value.getClass() );
				if (serviceObject!=null){
					convertor = serviceObject.getService() ;
				}
			}
			return convertor != null ? convertor.convertToString( value ) : null ;
		}
		
		if (Enum.class.isAssignableFrom(targetClass)) {
			return new EnumConverter((Class<? extends Enum>) targetClass).convertToTargetClass(value);
		}
		
		if( value instanceof String )
			convertor = primitiveStringConversionMap.get( targetClass.getName() ) ;
		
		if( convertor == null ) {
			ServiceObject<AbstractUnitConvertor> serviceObject = stringConversionServiceMap.get( targetClass );	
			if (serviceObject != null)
				convertor = serviceObject.getService() ;
		}
		if (convertor == null && value instanceof String) {
			if (Pattern.matches("([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*",(CharSequence) value)) { //If value looks like Java class name
				try {
					Class<?> clazz = null;
					if (context != null) {
						ClassLoader classLoader = context.getClassLoader();
						if (classLoader != null)
							clazz = classLoader.loadClass((String) value);
					}
					if (clazz == null)
						clazz = Class.forName((String) value);
					if (targetClass.isAssignableFrom(clazz)) {
						return clazz.newInstance();
					}
				} catch (ClassNotFoundException e) {
					// Do nothing
				} catch (InstantiationException e) {
					// Do nothing
				} catch (IllegalAccessException e) {
					// Do nothing
				}
			}
		}
		
		return convertor != null ? convertor.convertToTargetClass( value ) : null ;		
	}
	private boolean checkPrimitive( Class<?> clazz )
	{
		Class<?> targetSuperClass = clazz.getSuperclass() ;
		return ( clazz.isPrimitive() || targetSuperClass == Number.class ||
				 clazz == Boolean.class || clazz == Character.class ) ;
	}
	
	private Object convertPrimitive( Object value, Class<?> targetClass ){
		
		if( targetClass == Boolean.class || targetClass == boolean.class ){
			
			if( value instanceof Boolean )
				return value ;
			
			if( value instanceof Double )				
				return (Double)value > Double.MIN_VALUE * 100 ;
			
			if( value instanceof Float )
				return (Float)value > Float.MIN_VALUE * 100 ;			
						
			if( value instanceof Number )				
				return ((Number)value).intValue() == 0 ;
			
			return (Character)value == 0 ;		
		}
		
		if( value instanceof Boolean )
			return targetClass.cast( (Boolean)value ? 1 : 0 ) ;
		
		if( targetClass == Character.class || targetClass == char.class )
			return ((Number)value).shortValue() ;
			
		if( value instanceof Character )
			return targetClass.cast( Character.getNumericValue( (Character)value) ) ;
		
		return targetClass.cast( value ) ;

		
		//if( numberConversionMap.get( value.getClass().getName() ) <= numberConversionMap.get( targetClass.getName() ) )
		//	return targetClass.cast( value ) ;		
		//return null ;
	}
	boolean checkValidity( Class<?> clazz )
	{
		Class<?> superclazz = clazz.getSuperclass() ;
		if( !( clazz == Character.class || clazz == String.class ||  clazz == Boolean.class || clazz.isPrimitive() || superclazz == Number.class ) )
			return false ;	
		
		return true ;				
	}
	
	Object convertValue( Object value, Class<?> targetClass )
	{
		if( value.getClass() == String.class )
		{
			String string = (String)value ;
			if( targetClass == int.class )
				return Integer.parseInt(string) ;			
		}
		return null ;
	}

	public abstract static class AbstractUnitConvertor{
		
		abstract protected Object convertToTargetClass( Object obj ) throws IllegalArgumentException;
		abstract protected String convertToString( Object obj );
	}
	
	public abstract static class AbstractPrimitivesConvertor extends AbstractUnitConvertor{
		abstract protected Object convertToTargetClass( Object obj );
		@Override
		protected String convertToString( Object obj ){
			return obj.toString() ;
		}
	}
	
	class IntegerConvertor extends HandlingTypeConvertor.AbstractPrimitivesConvertor{

		@Override
		protected Object convertToTargetClass(Object obj) {
			return Integer.parseInt((String)obj) ;
		}
	}
	
	class ShortConvertor extends HandlingTypeConvertor.AbstractPrimitivesConvertor{

		@Override
		protected Object convertToTargetClass(Object obj) {
			return Short.parseShort((String)obj) ;
		}
	}
	class ByteConvertor extends HandlingTypeConvertor.AbstractPrimitivesConvertor{

		@Override
		protected Object convertToTargetClass(Object obj) {
			return Byte.parseByte((String)obj) ;
		}
	}	
	class LongConvertor extends HandlingTypeConvertor.AbstractPrimitivesConvertor{

		@Override
		protected Object convertToTargetClass(Object obj) {
			return Long.parseLong((String)obj) ;
		}
	}
	class DoubleConvertor extends HandlingTypeConvertor.AbstractPrimitivesConvertor{

		@Override
		protected Object convertToTargetClass(Object obj) {
			return Double.parseDouble((String)obj) ;
		}
	}
	class FloatConvertor extends HandlingTypeConvertor.AbstractPrimitivesConvertor{

		@Override
		protected Object convertToTargetClass(Object obj) {
			return Float.parseFloat((String)obj) ;
		}
	}
	class CharConvertor extends HandlingTypeConvertor.AbstractPrimitivesConvertor{

		@Override
		protected Object convertToTargetClass(Object obj) {
			String string = (String)obj ;
			return string.length() == 1 ? string.charAt(0) : null ;
		}
	}
	class StringConvertor extends HandlingTypeConvertor.AbstractPrimitivesConvertor{

		@Override
		protected Object convertToTargetClass(Object obj) {
			return obj ;
		}
	}
	class BooleanConvertor extends HandlingTypeConvertor.AbstractPrimitivesConvertor{

		@Override
		protected Object convertToTargetClass(Object obj) {
			return Boolean.parseBoolean( (String)obj ) ;
		}
	}
	class IntArrayConvertor extends HandlingTypeConvertor.AbstractPrimitivesConvertor{

		@Override
		protected Object convertToTargetClass(Object obj) {
			
			if( !(obj instanceof String) )
				return null ;
			
			String str = (String) obj;
			
			String[] strArr = str.split(",") ;
			if( strArr.length == 1 )
				strArr = str.split(";") ;
			
			if( strArr.length == 1 )
				strArr = str.split(":") ;
			
			int[] intArr = new int[strArr.length] ;
			for( int i = 0 ; i < strArr.length ; i++)
				intArr[i] = Integer.parseInt( strArr[i] ) ;
			
			return intArr ;
		}
	}
	
}


