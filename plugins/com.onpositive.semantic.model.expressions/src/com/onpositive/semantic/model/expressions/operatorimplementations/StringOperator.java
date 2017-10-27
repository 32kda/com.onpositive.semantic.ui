package com.onpositive.semantic.model.expressions.operatorimplementations;

public class StringOperator extends BinaryOperator<Object, Object> {

	public StringOperator(int kind ) {
		super(kind, Object.class, Object.class );
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Object getValue( Object arg1 , Object arg2 )
	{
		return doGetValue( arg1, arg2 ) ;
	}

	@Override
	protected Object doGetValue(Object _arg1, Object _arg2) {
	//comparison operators consider null as the least possible value and null is equal to null, i.e. null == null is true.

		if ( kind == BinaryOperator.PLUS ){
			
			StringBuilder stringBuffer = new StringBuilder();
			stringBuffer.append(_arg1);
			stringBuffer.append(_arg2);
			return stringBuffer.toString();
		}
		else{
		
			String arg1 = _arg1 != null ? _arg1.toString() : null ;
			String arg2 = _arg2 != null ? _arg2.toString() : null ;
			
			boolean arg1IsNull = ( arg1 == null) ;
			boolean arg2IsNull = ( arg2 == null) ;
			
			boolean bothAreNull = arg1IsNull && arg2IsNull ;
			boolean bothAreNotNull = !arg1IsNull && !arg2IsNull ;
			
			switch (kind){
			
// 				case BinaryOperator.MINUS   :{ return arg1 -  arg2 ; }// 2  ; // "-" ;
//				case BinaryOperator.MULT    :{ return arg1 *  arg2 ; }// 3  ; // "*" ;
//				case BinaryOperator.DIV		:{ return arg1 /  arg2 ; }// 4  ; //"/" ;
//				case BinaryOperator.BACKSLASH:{return arg1    arg2 ; }// 5  ;// "\\" ;
				case BinaryOperator.GREATER :{ return bothAreNotNull ?( arg1.compareTo(arg2)>0  ) :!arg1IsNull &&  arg2IsNull ; }// 6  ;// ">" ;
				case BinaryOperator.LOWER   :{ return bothAreNotNull ?( arg1.compareTo(arg2)<0  ) : arg1IsNull && !arg2IsNull ; }// 7  ;//"<" ;
				case BinaryOperator.GEQ 	:{ return bothAreNotNull ?( arg1.compareTo(arg2)>=0 ) :!arg1IsNull ; }// 8  ;//">=" ;
				case BinaryOperator.LEQ 	:{ return bothAreNotNull ?( arg1.compareTo(arg2)<=0 ) :!arg2IsNull ; }// 9  ;//"<=" ;
				case BinaryOperator.EQ  	:{ return bothAreNull ? true  : (bothAreNotNull? arg1.compareTo(arg2)==0:false ); }// 10 ;//"==" ;
				case BinaryOperator.NEQ 	:{ return bothAreNull ? false : (bothAreNotNull? arg1.compareTo(arg2)!=0:true  ); }// 11 ;//"!=" ;
//				case BinaryOperator.MOD 	:{ return arg1 %  arg2 ; }// 12 ;//"%" ;
//				case BinaryOperator.AND 	:{ return arg1 &  arg2 ; }// 13 ;//"&" ;
//				case BinaryOperator.OR  	:{ return arg1 |  arg2 ; }// 14 ;//"|" ;
//				case BinaryOperator.L_AND	:{ return arg1 && arg2 ; }// 15 ;//"&&" ;
//				case BinaryOperator.L_OR	:{ return arg1 || arg2 ; }// 16 ;//"||" ;
//				case BinaryOperator.XOR     :{ return arg1 ^  arg2 ; }// 17 ;//"^" ;
//				case BinaryOperator.LSHIFT  :{ return arg1 << arg2 ; }// 18 ;//"<<" ;
//				case BinaryOperator.RSHIFT  :{ return arg1 >> arg2 ; }// 19 ;//">>" ;
//				case BinaryOperator.TRSHIFT :{ return arg1 >>>arg2 ; }// 20 ;//">>>" ;
			}
		}
		return null;
	}


}
