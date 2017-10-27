package com.onpositive.commons.xml.language;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GeneralAttributeHandler implements IAttributeHandler{
	static final String ERROR_CANNOT_PERFORM_CONVERSION = "Error, cannot perform conversion."  ;
	static final String ERROR_UNABLE_TO_PASS_MEMBER_CHAIN = "Error, somthing wrong in the member sequence."  ;
	static final String ERROR_ATTRIBUTE_HANDLER_NOT_INITIALIZED = "Error, attribute handler not initialized."  ;
	static final String ERROR_CANNOT_FIND_STRING_CONVERTOR = "Error, cannot find string convertor."  ;
	static final String ERROR_CANNOT_INSTANTIATE_OBJECT = "Error, cannot instantiate object because of the null context."  ;
	
	AccessibleObject[] members ;
	String attributeType ;
	boolean needContext ;
	
	public GeneralAttributeHandler( AccessibleObject[] members, String attributeType, boolean needContext ) {

		this.members = members ;
		this.attributeType = attributeType ;
		this.needContext = needContext ;
	}	
	
	//returns null case success, error message elsewhere
	public String handleAttribute( Object elementObject, Object value, Context context )
	{
		Object memberObject = elementObject ;
		try {
			int i = 0 ;
			for( ; i < members.length-1 ; i++ )
				memberObject = members[i] instanceof Field
							 ? ( (Field )members[i] ).get( memberObject ) 
							 : ( (Method)members[i] ).invoke( memberObject ) ;

			AccessibleObject lastMember = members[i] ;
			boolean lastMemberIsMethod = lastMember instanceof Method ; 
			Class<?> targetClass = lastMemberIsMethod ? ((Method)lastMember).getParameterTypes()[0] : ((Field)lastMember).getType() ;
			
			if( attributeType.equals("java") && targetClass.equals(Class.class)){
				if( context == null )
					return ERROR_CANNOT_INSTANTIATE_OBJECT ;
					
				value = context.newInstance( (String)value ) ;
			}
			
			Object convertedValue ;						
			try{
				convertedValue = HandlingTypeConvertor.getInstance().convert( value, targetClass, context ) ;
			}
			catch(IllegalArgumentException e){
				return null ;
			}
			
			if( convertedValue == null &&value!=null)
				return " " + ERROR_CANNOT_PERFORM_CONVERSION + ' ' + (value!=null?value.getClass().getName():"null") + " to " + targetClass.getName() + '.' ;			
			
			if( lastMemberIsMethod )
				try{
					((Method)lastMember).invoke( memberObject, convertedValue ) ;
				}
			catch(Throwable e){
				return null ;
			}
			else
				((Field )lastMember).set( memberObject, convertedValue ) ;
			
			return null ;
							 
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ERROR_UNABLE_TO_PASS_MEMBER_CHAIN ;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return ERROR_UNABLE_TO_PASS_MEMBER_CHAIN ;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return ERROR_UNABLE_TO_PASS_MEMBER_CHAIN ;
		}
	}

	public String validate( String elementName, String attributeName ) {
		
		String commonErrorMessagePart = " Element: " + elementName + ". Attribute: " + attributeName + "." ;
		if( members == null || members.length == 0 )
			return ERROR_ATTRIBUTE_HANDLER_NOT_INITIALIZED + commonErrorMessagePart +'\n';
		
		AccessibleObject m = members[ members.length-1 ] ;		
		Class<?> c = m instanceof Field ? ((Field)m).getClass() : ((Method)m).getParameterTypes()[0] ;
		
		return HandlingTypeConvertor.getInstance().convertorIsPresent( c ) ? null : 
			ERROR_CANNOT_FIND_STRING_CONVERTOR + commonErrorMessagePart + " Class: " + c.getName() + ".\n" ;
	}
}
