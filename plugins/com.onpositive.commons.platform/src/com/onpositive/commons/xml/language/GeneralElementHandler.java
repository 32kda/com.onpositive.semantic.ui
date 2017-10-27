package com.onpositive.commons.xml.language;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.onpositive.core.runtime.Bundle;

public class GeneralElementHandler implements IElementHandlerWithValidation
{
	protected static final String ERROR_HANDLER_MISSING_FOR_THE_ATTRIBUTE = "Error, missing handler for the attribute.";
	protected static final String ERROR_CHILD_SETTER_CANNOT_BE_FOUND = "Error, cannot find the child setter.";
	protected static final String ERROR_CHILD_SETTER = "Error, cannot set the child.";
	protected static final String ERROR_CANNOT_CAST_CHILD_TO_THE_SETTER_PARAMETER = "Error, Unable to cast child to the setter parameter:";
	protected static final String ERROR_NO_SUPPORT_FOR_THE_ATTRIBUTE = "Error, no support for the attribute. " ;
	
	HashMap<String, IAttributeHandler> attributeHandlerMap;
	HashMap<String, Method> childrenHandlingMap;
	HashSet<String> attributesIgnoredOnValidation ;
	
	ElementDefinition elementDefinition;
	ArrayList<String> invalidAttributes = new ArrayList<String>();
	ArrayList<String> errorMessages = new ArrayList<String>();

	ArrayList<Method> parentHandler;
	ArrayList<Class<?>> pHandlerClass;
	protected Class<?> inputClass ;
	protected Class<?> objectClass ;

	public GeneralElementHandler(Bundle bundleContext, String className) {
		if( bundleContext != null && className != null )
		{
			className = className.trim();
			try {
				this.inputClass = bundleContext.loadClass(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				Activator.log(e);
				System.err.print("Error, cannot create instance of class " + className );
			}
		}
		setObjectClass( this.inputClass ) ;		
	}
	
	protected void setObjectClass( Class<?> clazz )
	{
		this.objectClass = clazz ;
	}
	
	public Object handleElement(Element element, Object parentContext, Context context)
	{
		// this.className = elementDefinition.modelClass ;
		invalidAttributes.clear();
		errorMessages.clear();		
		Object newInstance = produceNewInstance( element, parentContext, context) ;
		if( newInstance == null )
			return null ;
		
		//extract attribute handling methods and fields, child setters and parent handler
		checkMapConstructed();
		
		int s = parentHandler.size() ;
		for( int i = 0 ; i < s ; i++ )
		{
			Class<?> argClass = pHandlerClass.get(i) ; 
			if( argClass.isInstance(parentContext)){		
				try {
					parentHandler.get(i).invoke( newInstance, argClass.cast( parentContext ) );
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		}		
		
		NamedNodeMap attributes = element.getAttributes();
		int l = attributes.getLength();
		
		for (int i = 0; i < l; i++) {
			String attributeName = attributes.item(i).getLocalName();			
			if (attributeName.equals("xmlns") || attributeName.equals("xmlns:"))
				continue;

			IAttributeHandler attributeHandler = attributeHandlerMap.get(attributeName);

			if (attributeHandler != null)
			{				
				String value = attributes.item(i).getNodeValue();
				if ( hasExpressions(value) ) {
					IExpressionController expressionController = DOMEvaluator.getExpressionController( newInstance.getClass() );
					expressionController.setExpressionString(value);
					expressionController.setObject(newInstance);
					expressionController.setAttributeHandler(attributeHandler);
				}
				else {
					String errorMessage = attributeHandler.handleAttribute(	newInstance, value, context );
					if (errorMessage != null) {
						invalidAttributes.add(attributeName);
						errorMessages.add(errorMessage);
					}
				}
			} else {
				if( !this.attributesIgnoredOnValidation.contains( attributeName ) )
				{
					String individualErrorStringPath = " Element: " + elementDefinition.name + "." + " Attribute: " ;
					invalidAttributes.add(attributeName);
					errorMessages.add(ERROR_HANDLER_MISSING_FOR_THE_ATTRIBUTE + individualErrorStringPath + attributeName + ".");
				}
			}
		}
		if (errorMessages.size() != 0 || invalidAttributes.size() != 0)
			printErrors();

		evaluateChildren(element, newInstance, context);
		return returnedObject( newInstance );
	}
	protected Object returnedObject( Object newInstance ){
		return newInstance ;
	}

	protected Object produceNewInstance(Element element, Object parentContext, Context context) {
		
		try {			
			return this.objectClass.newInstance();
		} catch (final InstantiationException e) {
			Activator.log(e);
			System.err.print("Cannot create instance of class " + this.objectClass.getName()+'\n' );
			return null ;
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
			Activator.log(e);
			System.err.print("Cannot create instance of class " + this.objectClass.getName()+'\n' );
			return null ;
		}
		catch ( ExceptionInInitializerError e ){
			e.printStackTrace();
			System.err.print("Cannot create instance of class " + this.objectClass.getName()+'\n' );
			return null ;
		}		
	}


	protected void checkMapConstructed() {

		if (attributeHandlerMap == null) {
			
			attributesIgnoredOnValidation = new HashSet<String>() ;
			
			ArrayList<AttributeDefinition> attributesList = extarctAttributes() ;
			GeneralAttributeHandlerProvider attributeProvider = new GeneralAttributeHandlerProvider();

//			if (elementDefinition.allowedChildren != null && elementDefinition.allowedChildren.length() > 0)
			childrenHandlingMap = new HashMap<String, Method>();
			attributeHandlerMap = new HashMap<String, IAttributeHandler>() ;

			attributeProvider.constructAttributeHandlers( attributesList, attributeHandlerMap, objectClass, childrenHandlingMap );
			
			{
				parentHandler = new ArrayList<Method>() ;
				pHandlerClass = new ArrayList<Class<?>>() ;
				int i = 0 ;
				Method m = childrenHandlingMap.get( GeneralAttributeHandlerProvider.PARENT_ATR_KEY+i );
				for(  ; m != null ; )
				{				
					parentHandler.add(m) ; 
					pHandlerClass.add( m.getParameterTypes()[0] );
	
					m = childrenHandlingMap.get( GeneralAttributeHandlerProvider.PARENT_ATR_KEY + ++i );
				}
			}
			if ( attributeHandlerMap == null) {

				String[] invalidAttributes = attributeProvider.getInvalidAttributes();
				String[] errorMessageas = attributeProvider.getErrorMessages();

				for (int i = 0; i < invalidAttributes.length; i++) {

					String errorMessage = i < errorMessageas.length ? errorMessageas[i]	: "Error:";
					errorMessage += (' ' + invalidAttributes[i] + ";\n" );
					System.err.print(errorMessage);
				}
			}
		}
	}

	protected ArrayList<AttributeDefinition> extarctAttributes()
	{
		ArrayList<AttributeDefinition> attributesList = new ArrayList<AttributeDefinition>();
		HashSet<String> presentAttributes = new HashSet<String>() ; 
		
		for ( AttributeDefinition ad : elementDefinition.supportedAttrs )
		{
			String attributeName = ad.name ;
			if( !presentAttributes.contains( attributeName ) )
			{
				if( ad.ignoreOnValidation == true )
					this.attributesIgnoredOnValidation.add( attributeName ) ;
				
				presentAttributes.add( attributeName ) ;
				attributesList.add( ad );
			}
		}

		ElementDefinition[] extendedElements = elementDefinition.getExtendedElements();
		for (int i = 0; i < extendedElements.length; i++)
			for ( AttributeDefinition ad : extendedElements[i].supportedAttrs )
			{
				String attributeName = ad.name ;
				if( !presentAttributes.contains( attributeName ) )
				{
					if( ad.ignoreOnValidation == true )
						this.attributesIgnoredOnValidation.add( attributeName ) ;
					
					presentAttributes.add( attributeName ) ;
					attributesList.add( ad );
				}
			}
		
		return attributesList ;
	}

	protected void evaluateChildren( Element element, Object newInstance,Context context )
	{
		final NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			final Node item = childNodes.item(i);
			if (item instanceof Element) {
				Element childElement = (Element) item;
				Object childObject = DOMEvaluator.getInstance().evaluate( childElement, newInstance, context );
				if( childObject!= null ){
					final String namespaceURI = childElement.getNamespaceURI();
					final String childName = childElement.getLocalName();
					ElementDefinition childeElementDefinition = DOMEvaluator.getInstance().getElement(childName, namespaceURI) ;
					attachChild( newInstance, childObject, childeElementDefinition );
				}
			}
		}
	}

	protected void attachChild(Object newInstance, Object childObject, ElementDefinition childeElementDefinition )
	{
		String childElementName = childeElementDefinition.name ;
		String individualErrorStringPath = " Parent: " + elementDefinition.name + "." +
		   								   " Child: " + childElementName + "." ;
		Method method = null;
		{
			method = childrenHandlingMap.get( childElementName ) ;
			if( method == null )
			{
				ElementDefinition[] extendedElements = childeElementDefinition.getExtendedElements();
				for ( int i = 0 ; i < extendedElements.length ; i++ ) {
					method = childrenHandlingMap.get(extendedElements[i].name);
					if (method != null)
						break;
				}
			}
		}
		if (method != null) {
			try {
				try {
					ChildSetter cs = method.getAnnotation(GeneralAttributeHandlerProvider.childSetterAnnotation);
					Object arg = cs.needCasting() ? method.getParameterTypes()[0].cast(childObject) : childObject;
					method.invoke(newInstance, arg);
				} catch (ClassCastException e) {
					e.printStackTrace();
					errorMessages.add(ERROR_CANNOT_CAST_CHILD_TO_THE_SETTER_PARAMETER + individualErrorStringPath  ); 
				}
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				errorMessages.add(ERROR_CHILD_SETTER + individualErrorStringPath );
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				errorMessages.add(ERROR_CHILD_SETTER + individualErrorStringPath );
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				errorMessages.add(ERROR_CHILD_SETTER + individualErrorStringPath );
			}
		} else
			errorMessages.add( ERROR_CHILD_SETTER_CANNOT_BE_FOUND + individualErrorStringPath ) ; 
	}

	protected void printErrors() {
		for( String s : errorMessages )
			System.err.print(s+'\n') ;
	}

	public void setElementDefiniton(ElementDefinition elementDefinition) {
		this.elementDefinition = elementDefinition;
	}

	public String checkAttr(String attributeName)
	{//TODO add type check here
		checkMapConstructed();
		
		if( attributesIgnoredOnValidation.contains( attributeName ) )
			return null ;
		
		IAttributeHandler ah = attributeHandlerMap.get(attributeName);
		return ah != null ? ah.validate( elementDefinition.name, attributeName ) :
					 ERROR_NO_SUPPORT_FOR_THE_ATTRIBUTE + "Element: " + elementDefinition.name + ". Attribute: " + attributeName + ";\n" ;
	}
	
	private static boolean hasExpressions( String s ){
		
		int l = s.length() ;
		for( int i = 0 ; i < l ; i++ )
			if ( s.charAt(i) == '{' ) return true;

		return false ;
	}

	public Method getChildSetter(String childName) {
		checkMapConstructed();
		return childrenHandlingMap.get(childName);
	}
}
