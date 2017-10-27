package com.onpositive.commons.xml.language;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.onpositive.core.runtime.Platform;

public class ElementDefinition {

	
	protected static final String ERROR_CANNOT_FIND_ELEMENT_HANDLER_FOR_THE_CHILD = "Error, cannot find element handler for the child. " ;
	protected static final String ERROR_SETTER_PARAMETER_LIST_DOESNOT_FIT_THE_TYPE_OF_CHILD_AND_PARENTS = "Error, the setters parameters list does not fit the type of childe and it's parents. " ;
	protected static final String ERROR_SETTER_PARAMETER_LIST_DOESNOT_FIT_THE_TYPE_OF_CHILD = "Error, the setters parameters list does not fit the child's type. " ;
	protected static final String ERROR_CANNOT_FIND_SETTER_FOR_THE_CHILD =  "Error, cannot find the setter for the child. " ;
	protected static final String ERROR_CHILD_SETTER_PARAM_LIST_IS_EMPTY =  "Error, child setter's param list is empty. " ;
	protected static final String ERROR_CHILD_SETTER_HAS_TOO_MANY_PARAMS =  "Error, child setter has too many params. " ;
	
	
	public ElementDefinition(String elname, boolean parseBoolean, ObjectReference objectReference, String modelClass, String namespace ) {
		this.name = elname;
		this.isAbstract = parseBoolean;
		this.reference = objectReference;
		this.modelClass = modelClass;
		this.namespace = namespace ;
	}

	protected String name;
	protected String extendedElementsString;
	protected String modelClass;
	protected String allowedChildren;
	protected String namespace;
	protected boolean isAbstract;
	protected ArrayList<AttributeDefinition> supportedAttrs = new ArrayList<AttributeDefinition>();
	protected ElementDefinition[] extendsElements ;

	protected ObjectReference reference;

	public void checkAttributeSupport(ArrayList<AttributeDefinition> supportedAttrs2) {
		IElementHandler object = (IElementHandler) reference.getObject();
		
		//in fact here we distinct two types of ElementHandlers: those who need ElementDefinition and those who do not.
		if( object instanceof GeneralElementHandler ){
			for (AttributeDefinition a : supportedAttrs2){
				
				String err = ((GeneralElementHandler)object).checkAttr( a.name ) ;
				if ( err != null )
					System.err.print( err ) ;
			}
			return ;
		}
//		
//		if( object instanceof BasicElementHandler ){
//			for (AttributeDefinition a : supportedAttrs2){
//				
//				String err = ((BasicElementHandler)object).checkAttr( a.name ) ;
//				if ( err != null )
//					System.err.print( err ) ;
//			}
//			return ;
//		}

		for (AttributeDefinition a : supportedAttrs2)
			checkAttr(a, object) ;
	}
	
	public void checkChildrenSupport()
	{
		Class<?> thisClass = getThisClass();
		if (thisClass == null) {
			// Platform.log("Model class can not be loaded for:"+name);
			return;
		}
		
		String[] children = this.allowedChildren.split(",") ;
		for( int i = 0 ; i < children.length ; i++ ){
			
			String s = children[i] ;
			int start = 0 ;
			int end = s.length() ;
			for(  ; start < end && Character.isWhitespace( s.charAt(start) ) ; start++ );
			for(  ; end > 0 && Character.isWhitespace( s.charAt(end-1) ) ; end-- );
			
			children[i] = start < end ? s.substring( start, end ) : null ; 		
		}
		
		for( String s : children ){
			
			String commonErrorMessagePart = "Element: " + this.name + ". Child: " + s + ";\n" ;
			
			if( s == null )	continue ;
			
			int p = s.lastIndexOf('/') ;
			String childElementName = ( p == -1 ) ? s : s.substring( p+1 ) ;
			String childElementNamespace = ( p == -1 ) ? namespace : s.substring( 0, p ) ;
			
			ElementDefinition childElementDefinition = DOMEvaluator.getElement( childElementName , childElementNamespace ) ; 
			if ( childElementDefinition == null )
				System.err.print( ERROR_CANNOT_FIND_ELEMENT_HANDLER_FOR_THE_CHILD + commonErrorMessagePart ) ;
			else
			{
				String childName = childElementDefinition.name ;
				
				if( this.reference instanceof IElementHandlerWithValidation )
				{
					IElementHandlerWithValidation handler = (IElementHandlerWithValidation)this.reference ;
					Method childSetter = handler.getChildSetter( childName ) ;
					if( childSetter == null ){
						System.err.print( ERROR_CANNOT_FIND_SETTER_FOR_THE_CHILD + commonErrorMessagePart ) ;
						break ;
					}					
					
					Class<?>[] methodParams =  childSetter.getParameterTypes() ;					
					if ( methodParams.length != 1 ){
						if( methodParams.length == 0 )
							System.err.print( ERROR_CHILD_SETTER_PARAM_LIST_IS_EMPTY + commonErrorMessagePart ) ;
						else
							System.err.print( ERROR_CHILD_SETTER_HAS_TOO_MANY_PARAMS + commonErrorMessagePart ) ;
					}
					
					String childClassName = childElementDefinition.modelClass ;
					Class<?> childClass = this.reference.load( childClassName ) ;	
					Class<?> methodParamClass = methodParams[0] ; 
					if( methodParamClass != childClass )
					{
						ChildSetter childAnnotation = childSetter.getAnnotation( GeneralAttributeHandlerProvider.childSetterAnnotation ) ;
						if( childAnnotation.needCasting() )
						{// search childClass among parents of methodParamClass							
							boolean foundSetter = false ;
							for( Class<?> cl = methodParamClass ; cl != null ; cl = cl.getSuperclass() )
								if( inspectSuperInterfaces( cl , childClass ) ){
									foundSetter = true ;
									break ;
								}
							if( !foundSetter )
								System.err.print( ERROR_SETTER_PARAMETER_LIST_DOESNOT_FIT_THE_TYPE_OF_CHILD_AND_PARENTS + commonErrorMessagePart ) ;
							break ;
						}
						else{
							System.err.print( ERROR_SETTER_PARAMETER_LIST_DOESNOT_FIT_THE_TYPE_OF_CHILD + " Cannot set the child " + commonErrorMessagePart ) ;
							break;
						}						
					}						
				}
			}
		}
	}
	
	private final boolean inspectSuperInterfaces( Class<?> inspectedClass , Class<?> targetClass )
	{
		if( inspectedClass == targetClass )
			return true ;
		
		Class<?>[] interfaces = inspectedClass.getInterfaces() ;
		for( Class<?> i : interfaces ){
			if( inspectSuperInterfaces( i, targetClass) )
				return true ;			
		}
			
		
		return false ;
	}
	
	

	private void checkAttr(AttributeDefinition a, IElementHandler object)
	{
		Class<?> thisClass = getThisClass();
		if (thisClass == null) {
			// Platform.log("Model class can not be loaded for:"+name);
			return;
		}		
		
		Method[] methods = object.getClass().getMethods();
		for (Method m : methods) {
			if (m.getName().equals("handleElement")) {
				HandlesAttributeIndirectly annotation = m.getAnnotation( HandlesAttributeIndirectly.class );
				if (annotation != null) {
					for (String s : annotation.value()) {
						if (s.equals(a.name)) {
							return;
						}
					}
				}
			}
		}
		boolean checkHandle = checkHandle(a, thisClass);
		if (!checkHandle) {
			Platform.log("Attribute:" + a.name
					+ " is not handled properly for element:" + name);
		}
	}

	private Class<?> getThisClass() {
		modelClass = reference.className ;
		if ( modelClass == null || modelClass.length() == 0) {
			return null;
		}
		return reference.load(modelClass);
	}

	private boolean checkHandle(AttributeDefinition a, Class<?> load) {
		Method[] methods = load.getMethods();
		for (Method m : methods) {
			HandlesAttributeDirectly annotation = m
					.getAnnotation(HandlesAttributeDirectly.class);
			if (annotation != null) {
				if (annotation.value().equals(a.name)) {
					return true;
				}
			}
			HandlesAttributeIndirectly annotation2 = m
					.getAnnotation(HandlesAttributeIndirectly.class);
			if (annotation2 != null) {
				String[] values = annotation2.value();
				for (String s : values) {
					if (s.equals(a.name)) {
						Class<?> z = m.getReturnType();
						if (checkHandle(a, z)) {
							return true;
						}
					}
				}
			}

		}
		return false;
	}
	
	public ElementDefinition[] getExtendedElements()
	{
		if( extendsElements == null )
			initSuperElements() ;
		
		return extendsElements ;		
	}
	
	private void initSuperElements() 
	{
		ArrayList<ElementDefinition> superElementsArrayList = new ArrayList<ElementDefinition>() ;
		addSuperElements( extendedElementsString , superElementsArrayList ) ;
		extendsElements = new ElementDefinition[ superElementsArrayList.size() ] ;
		superElementsArrayList.toArray( extendsElements ) ;		
	}
	
	//TODO this function should have a better name ?
	private void addSuperElements( String superElementsString , ArrayList<ElementDefinition> superelEmentsArrayList )
	{
		if ( superElementsString != null && superElementsString.length() > 0)
		{
			int initPos = superelEmentsArrayList.size() ;	
			resolveSuper( superElementsString, superelEmentsArrayList );
			int termPos = superelEmentsArrayList.size() ;
			
			for( int i = initPos ; i < termPos ; i++ )
				addSuperElements( superelEmentsArrayList.get(i).extendedElementsString, superelEmentsArrayList) ;
		}		
	}
	
	//TODO this function should have a better name ?
	private void resolveSuper(String superElementsString , ArrayList<ElementDefinition> superelEmentsArrayList )
	{
		String strings[] = superElementsString.split(",") ;
		
		for( int i = 0 ; i < strings.length ; i++ )
		{
			String superElementName = strings[i] ;
			int lastIndexOf = superElementName.lastIndexOf('/');
			String namespaceName = namespace ;
			
			if ( lastIndexOf != -1 )
			{			
				namespaceName    = superElementName.substring(0, lastIndexOf + 1);
				superElementName = superElementName.substring(lastIndexOf + 1) ;
			}
			ElementDefinition superElementDef = DOMEvaluator.getElement( superElementName, namespaceName ) ;
			if( superElementDef != null )
				superelEmentsArrayList.add( superElementDef ) ;
		}
	}
	
	
	
	
	
	
	
	
	
}