package com.onpositive.commons.xml.language;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

public class GeneralAttributeHandlerProvider extends AbstractAttributeHandlerProvider {
	
	public static final String PARENT_ATR_KEY = "$$$$parent";
	static final Class<HandlesAttributeDirectly> handlesAttributeAnnotation = HandlesAttributeDirectly.class ;
	static final Class<HandlesAttributeIndirectly> returnedTypehandlesAttributeAnnotation = HandlesAttributeIndirectly.class ;
	static final Class<ChildSetter> childSetterAnnotation = ChildSetter.class ;
	
	protected final static String ERROR_UNABLE_TO_FIND_MEMBER = "Error, unable to find member for the attribute:" ;
	protected final static String ERROR_INVALID_METHOD_0 = "Error, invalid method received. The method should not have parameters. Attribute cannot be handled:";
	protected final static String ERROR_INVALID_METHOD_1 = "Error, invalid method received. The method should have single parameter. Attribute cannot be handled:";
	protected final static String ERROR_INVALID_METHOD_NO_RETURN_VALUE = "Error, invalid method received. The method should return value. Attribute cannot be handled:";
	
	Stack<AccessibleObject> currentCallSequence = new Stack<AccessibleObject>();
	HashMap<String, IAttributeHandler> resultingMap ;
	HashMap<String, Method> childrenHandlingMap ;
	ArrayList<String> invalidAttributes = new ArrayList<String>() ;
	ArrayList<String> errorMessages = new ArrayList<String>() ;
	
	protected static class HandlingParametr
	{
		public HandlingParametr( AccessibleObject targetMember , boolean isMethod , boolean isDirect )
		{
			this.targetMember = targetMember ;
			this.isMethod = isMethod ;
			this.isDirect = isDirect ;
		}
		AccessibleObject targetMember ;
		boolean isMethod ;
		boolean isDirect ;
		Class<? extends AbstractContextDependentAttributeHandler>actualHandler ;
		
		protected boolean isMethod(){ return isMethod ; }
		protected boolean isDirect(){ return isDirect ; }
		protected AccessibleObject getPropertie(){ return targetMember ; }
	}
	
	protected static class PropertyDistributionParam
	{
		HashMap<String, HandlingParametr> directHandlingMap = new HashMap<String, GeneralAttributeHandlerProvider.HandlingParametr>() ;
		HashMap<HandlingParametr, ArrayList<AttributeDefinition>> indirectHandlingMap = new HashMap<HandlingParametr, ArrayList<AttributeDefinition>>() ;		
	}

	@Override
	public HashMap<String, IAttributeHandler> constructAttributeHandlers( ArrayList<AttributeDefinition> attributes,
																		  HashMap<String, IAttributeHandler> attributesMap ,
																		  Class<?> clazz, 
																		  HashMap<String, Method> childrenHandlingMap )
	{
		this.childrenHandlingMap = childrenHandlingMap ;
		this.resultingMap = attributesMap ;
		ArrayList<String> invalidAttributes = new ArrayList<String>() ;
		
		processMember(  clazz, clazz, attributes ) ;
		
		if( invalidAttributes.size() == 0 )
			return resultingMap ;
		else//TODO handle here the faulty case ;
			return null ;
	}
	
	protected void processMember( Class<?> rootClass,
								  Class<?> clazz,
								  ArrayList<AttributeDefinition> attributes )
	{
		PropertyDistributionParam dParam = constructHandlingMap(clazz); 
				
		for( AttributeDefinition currentAttribute : attributes )
		{
			HandlingParametr param = dParam.directHandlingMap.get( currentAttribute.name ) ;
			
			if( param == null ){
				invalidAttributes.add( currentAttribute.name ) ;
				errorMessages.add(ERROR_UNABLE_TO_FIND_MEMBER);
				continue ;
			}

			if( param.isDirect() ){
				
				if( param.isMethod ){
					Method targetMethod = (Method)(param.targetMember) ;
					if( targetMethod.getParameterTypes().length != 1 ){
						invalidAttributes.add( currentAttribute.name ) ;
						errorMessages.add(ERROR_INVALID_METHOD_1);
						continue ;
					}					
				}
				
				int s = currentCallSequence.size() ;
				AccessibleObject[] handlerMembersArray = new AccessibleObject[ s+1 ] ;
				currentCallSequence.toArray( handlerMembersArray ) ;
				handlerMembersArray[s] = param.targetMember ;
				
				IAttributeHandler generalAttributeHandler = new GeneralAttributeHandler( handlerMembersArray, 
																						 currentAttribute.type,
																						 currentAttribute.type.equals("java") );
				if (param.actualHandler!=null){
					try {
						Constructor<? extends AbstractContextDependentAttributeHandler> constructor = param.actualHandler.getConstructor(IAttributeHandler.class);
						generalAttributeHandler=constructor.newInstance( generalAttributeHandler );
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				resultingMap.put( currentAttribute.name, generalAttributeHandler ) ;
			}
			else{
				if( param.isMethod ){
					Method targetMethod = (Method)param.targetMember ;
					if( targetMethod.getParameterTypes().length != 0 )
					{
						invalidAttributes.add( currentAttribute.name ) ;
						errorMessages.add(ERROR_INVALID_METHOD_0);
						continue ;
					}
					if(targetMethod.getReturnType() == void.class )
					{
						invalidAttributes.add( currentAttribute.name ) ;
						errorMessages.add(ERROR_INVALID_METHOD_NO_RETURN_VALUE);						
						continue ;
					}					
				}				
				dParam.indirectHandlingMap.get( param ).add( currentAttribute ) ;
			}
		}
		Set<HandlingParametr> indirectHandledProperties = dParam.indirectHandlingMap.keySet() ;
		for( HandlingParametr param : indirectHandledProperties )
		{
			AccessibleObject member = param.getPropertie() ;
			Class<?> memberClass = param.isMethod() ? ((Method)member).getReturnType() : ((Field)member).getType() ;
			
			currentCallSequence.push( param.targetMember );
			processMember( rootClass , memberClass, dParam.indirectHandlingMap.get(param) ) ;
			currentCallSequence.pop() ;
		}
	}

	

	protected PropertyDistributionParam constructHandlingMap( Class<?> clazz )
	{
		PropertyDistributionParam result = new PropertyDistributionParam() ;
		
		for( Class<?> cl = clazz ; cl != null ; cl = cl.getSuperclass() )
		{
			includeMembersArray( result, cl.getFields () , false );
			includeMembersArray( result, cl.getMethods() , true  );
			processSuperInterfaces( cl, result );
		}
		
		return result ;
	}

	private void processSuperInterfaces( Class<?> cl, PropertyDistributionParam result )
	{
		Class<?>[] interfaces = cl.getInterfaces() ;
		for( Class<?> i : interfaces ){
			includeMembersArray( result, i.getMethods(), true );
			processSuperInterfaces( i, result) ;
		}
	}

	protected void includeMembersArray( PropertyDistributionParam result, AccessibleObject[] members, boolean isMethod )
	{
		for( AccessibleObject m : members )
		{
			HandlesAttributeDirectly attrDirectHandling = m.getAnnotation( handlesAttributeAnnotation ) ;
			if( attrDirectHandling != null){
				HandlingParametr hParam = new HandlingParametr( m, isMethod, true) ;
				result.directHandlingMap.put( attrDirectHandling.value(), hParam ) ;
				continue ;
			}
			CustomAttributeHandler cannotation = m.getAnnotation(CustomAttributeHandler.class);
			if(cannotation!=null){
				Class<? extends AbstractContextDependentAttributeHandler> handler = cannotation.handler();
				HandlingParametr hParam = new HandlingParametr( m, isMethod, true) ;
				hParam.actualHandler=handler;
				result.directHandlingMap.put( cannotation.value(), hParam ) ;
				continue ;
			}			
			HandlesAttributeIndirectly attrIndirectHandling = m.getAnnotation( returnedTypehandlesAttributeAnnotation ) ;
			if( attrIndirectHandling != null ){
				HandlingParametr hParam = new HandlingParametr( m, isMethod, false) ;
				for( String s : attrIndirectHandling.value() )
					result.directHandlingMap.put( s, hParam ) ;
					
				result.indirectHandlingMap.put( hParam, new ArrayList<AttributeDefinition>()) ;
				continue ;
			}
			
			if( isMethod )
			{
				if( childrenHandlingMap != null ){
					ChildSetter childSetter = m.getAnnotation( childSetterAnnotation ) ;
					if( childSetter != null ){
						childrenHandlingMap.put( childSetter.value(), (Method)m ) ;
						continue ;
					}
				}
				
				HandlesParent annotation = m.getAnnotation(HandlesParent.class);
				if (annotation!=null){
					boolean alreadyExists = false ;
					int i = 0 ;
					for( Method meth = childrenHandlingMap.get(PARENT_ATR_KEY+i) ; meth != null ; )
					{
						if( meth.equals( m ) )
						{
							alreadyExists = true ;
							break ;
						}
						meth = childrenHandlingMap.get(PARENT_ATR_KEY + ++i) ;						
					}
					
					if(  !alreadyExists )
						childrenHandlingMap.put(PARENT_ATR_KEY+i, (Method) m);
				}
			}
		}
	}
	String[] getInvalidAttributes(){		
		return invalidAttributes.toArray( new String[ invalidAttributes.size() ]) ;
	}
	String[] getErrorMessages(){		
		return errorMessages.toArray( new String[ errorMessages.size() ]) ;
	}
}
