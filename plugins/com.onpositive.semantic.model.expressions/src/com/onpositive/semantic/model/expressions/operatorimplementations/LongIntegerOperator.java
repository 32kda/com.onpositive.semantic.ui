package com.onpositive.semantic.model.expressions.operatorimplementations;

public class LongIntegerOperator extends BinaryOperator<Number, Number>{

	public LongIntegerOperator(int kind){
		super(kind, Number.class, Number.class);

	}

	@Override
	protected Object doGetValue(Number _arg1, Number _arg2) {
	//null == null is true. null != smthElse is true. Null being an argument of another operator makes it return false ;	
	
		boolean arg1IsNull  = _arg1 == null ;
		boolean arg2IsNull  = _arg2 == null ;
		boolean bothAreNull = arg1IsNull && arg2IsNull ;
		boolean bothAreNotNull = !arg1IsNull && !arg2IsNull ;
		
		long arg1 = 0, arg2 = 0 ;
		
		if( bothAreNotNull ){
			
			arg1 = _arg1.longValue() ;
			arg2 = _arg2.longValue() ;
		}
		else{
			if( !bothAreNull ){
				if ( kind == BinaryOperator.EQ  ) return false ;
				if ( kind == BinaryOperator.NEQ ) return true ;
				
				boolean greater = ( kind == BinaryOperator.GREATER ) || ( kind == BinaryOperator.GEQ ) ;
				boolean lower   = ( kind == BinaryOperator.LOWER   ) || ( kind == BinaryOperator.LEQ ) ;
				
				if( greater ) return arg2IsNull ;
				if( lower   ) return arg1IsNull ;
				
				if( arg1IsNull )
					arg2 = _arg2.longValue() ;
				else
					arg1 = _arg1.longValue() ;				
			}
		}
		
		switch (kind){
		
			case BinaryOperator.PLUS    :{ return arg1 +  arg2 ; }// 1  ; //"+" ;
			case BinaryOperator.MINUS   :{ return arg1 -  arg2 ; }// 2  ; // "-" ;
			case BinaryOperator.MULT    :{ return arg1 *  arg2 ; }// 3  ; // "*" ;
			case BinaryOperator.DIV		:{ return arg1 /  arg2 ; }// 4  ; //"/" ;
//			case BinaryOperator.BACKSLASH:{return arg1    arg2 ; }// 5  ;// "\\" ;
			case BinaryOperator.GREATER :{ return arg1 >  arg2 ; }// 6  ;// ">" ;
			case BinaryOperator.LOWER   :{ return arg1 <  arg2 ; }// 7  ;//"<" ;
			case BinaryOperator.GEQ 	:{ return arg1 >= arg2 ; }// 8  ;//">=" ;
			case BinaryOperator.LEQ 	:{ return arg1 <= arg2 ; }// 9  ;//"<=" ;
			case BinaryOperator.EQ  	:{ return arg1 == arg2 ; }// 10 ;//"==" ;
			case BinaryOperator.NEQ 	:{ return arg1 != arg2 ; }// 11 ;//"!=" ;
			case BinaryOperator.MOD 	:{ return arg1 %  arg2 ; }// 12 ;//"%" ;
			case BinaryOperator.AND 	:{ return arg1 &  arg2 ; }// 13 ;//"&" ;
			case BinaryOperator.OR  	:{ return arg1 |  arg2 ; }// 14 ;//"|" ;
//			case BinaryOperator.L_AND	:{ return arg1 && arg2 ; }// 15 ;//"&&" ;
//			case BinaryOperator.L_OR	:{ return arg1 || arg2 ; }// 16 ;//"||" ;
			case BinaryOperator.XOR     :{ return arg1 ^  arg2 ; }// 17 ;//"^" ;
			case BinaryOperator.LSHIFT  :{ return arg1 << arg2 ; }// 18 ;//"<<" ;
			case BinaryOperator.RSHIFT  :{ return arg1 >> arg2 ; }// 19 ;//">>" ;
			case BinaryOperator.TRSHIFT :{ return arg1 >>>arg2 ; }// 20 ;//">>>" ;
		}
		return null;
	}
}
