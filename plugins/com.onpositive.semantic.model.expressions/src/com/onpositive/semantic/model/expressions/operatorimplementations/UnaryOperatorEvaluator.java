package com.onpositive.semantic.model.expressions.operatorimplementations;

import java.util.ArrayList;
import java.util.HashMap;


public class UnaryOperatorEvaluator {

	static HashMap< Integer , HashMap<String, UnaryOperator<?>> > operatorMap = new HashMap<Integer, HashMap<String,UnaryOperator<?>>>() ;
	static String DEFAULT_OPERATOR = "$$$$ Default operator $$$$" ;

	
	static{
		Object[] mapParams = UnaryOperator.getCorrespondanceArray() ;
		for( int i = 0 ; (i <  mapParams.length) && (mapParams[i] != null) ; )
		{
			int kind = ( Integer )mapParams[ i++ ] ;
			Class<?> clazz = ( Class<?> )mapParams[ i++ ] ;
			UnaryOperator<?> operator = ( UnaryOperator<?> )mapParams[ i++ ] ;
			
			if( operatorMap.get( kind ) == null  )
				operatorMap.put( kind, new HashMap<String, UnaryOperator<?>> ()) ;
			
			operatorMap.get(kind).put( clazz.getCanonicalName(), operator ) ;			
		}
		Object[] defaultParams = UnaryOperator.getDefaultOperators() ;
		for( Object obj : defaultParams ){
			UnaryOperator<?> operator = (UnaryOperator<?>)obj ;
			int kind = operator.kind ;
			operatorMap.get(kind).put( DEFAULT_OPERATOR, operator ) ;	
		}
	}

	static final protected class ClassUpcaster{
		
		static{
			upcastMap = new HashMap<Class<?>, Class<?>>() ;
			fillMap();
		}
		
		static HashMap< Class<?>, Class<?> > upcastMap ;
	
		protected Class<?> upcast( Class<?> clazz ){
			
			Class<?> class1 = upcastMap.get( clazz ) ;
			return ( class1 == null ) ? clazz : class1 ;
		}
		
		protected static void fillMap() {
			
			upcastMap.put(  Byte.class, Integer.class ) ;
			upcastMap.put( Short.class, Integer.class ) ;
			upcastMap.put( Float.class,  Double.class ) ;
			upcastMap.put( Character.class, String.class ) ;
		}
	}
	
	public Object getOperatorValue( int kind, Object arg )
	{
		if( arg == null ) return null ;
		UnaryOperator<?> operator = getOperator( kind, arg.getClass() ) ;
		
		return operator.getValue( arg ) ;
	}

	private UnaryOperator<?> getOperator( int kind,
			Class<? extends Object> class1)
	{
		HashMap< String, UnaryOperator<?>> mapOfThePresentKind = operatorMap.get( kind ) ;
		UnaryOperator<?> result = mapOfThePresentKind.get( class1.getCanonicalName() );
		
		if( result != null )
			return result ;
		
		else{
			
			ArrayList<Class<?>> ClassList = new ArrayList<Class<?>>() ;
			ClassList.add(class1);			
			
			ArrayList<Class<?>> extractedClassList = new ArrayList<Class<?>>() ;
			extractSuperClasses( class1, extractedClassList ) ;
						
			int classListSize = extractedClassList.size() ;
			for( int i = 0 ; i < classListSize ; i++ ){
				
				class1 = extractedClassList.get(i) ;							
				result = mapOfThePresentKind.get( class1 );

				if( result == null )
					ClassList.add( class1 ) ;						
		
				else{						
					int CPListSize = ClassList.size() ;
					for( int k = 0 ; k < CPListSize ; k++ )
						mapOfThePresentKind.put( ClassList.get(k).getCanonicalName(), result ) ;
								
					return result ;
				}
			}					
		}
		if( result == null )
			result = mapOfThePresentKind.get( DEFAULT_OPERATOR );
		
		return result;
	}

	private void extractSuperClasses(Class<? extends Object> clazz, ArrayList<Class<?>> classList) {

		if ( clazz == null ) return ;
		
		classList.add(clazz) ;
		
		Class<?> superClass = clazz.getSuperclass() ;
		Class<?>[] interfaces = clazz.getInterfaces() ;
		
		extractSuperClasses( superClass, classList ) ;
		
		for( Class<?> i : interfaces )
			extractSuperClasses( i, classList ) ;
	}
}




