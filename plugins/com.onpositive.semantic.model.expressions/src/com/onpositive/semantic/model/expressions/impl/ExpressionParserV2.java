package com.onpositive.semantic.model.expressions.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.GetPropertyLookup;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IExpressionParser;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.VariableExpression;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.expressions.impl.ExpressionLexer.Lexeme;
import com.onpositive.semantic.model.expressions.operatorimplementations.BinaryOperator;
import com.onpositive.semantic.model.expressions.operatorimplementations.UnaryOperator;

public class ExpressionParserV2 implements IExpressionParser{
	
	protected static String INVALID_BRACKETS_COUNT = "Input string does not contain expression or contains a nonclosed expression.";
	
	static abstract class ElementaryParser{
		
		protected static String ERROR_UNEXPECTED_END_OF_EXPRESSION = "Unexpected end of expression." ;
		protected static String ERROR_QUESTION_MARK_EXPECTED = "\'?\' expected." ;
		protected static String ERROR_COLON_EXPECTED = "\':\' expected." ;
		protected static String ERROR_OPERATOR_OR_SELECTOR_EXPECTED = "\'->\' or any other operator expected." ;
		protected static String ERROR_BINARY_OPERATOR_EXPECTED = "Binary operator expected." ;
		protected static String ERROR_UNARY_OPERATOR_EXPECTED = "Unary operator expected." ;
		protected static String ERROR_CLOSE_BRACKET = "Closing bracket expected." ;
		protected static String ERROR_EXPECTING_NUM_ID_CLASS_STRING_UNOP = "Expecting number, string, class, null or unary operator.";
		protected static String ERROR_BINDING_NOT_RECOGNIZED = "Binding not recognized.";
		protected static String ERROR_CLASS_NOT_FOUND = "Class not found." ;
		protected static String ERROR_INVALID_METHOD_CALL = "Invalid method call - " ;
		
		protected ExpressionLexer lexer ;
		protected ElementaryParser nextParser ;
		protected Lexeme invalidLexeme ;
		protected String errorMessage ;
		
		protected void setLexer( ExpressionLexer lexer ){
			this.lexer = lexer ;
		}
		protected void setNextParser( ElementaryParser nextParser ){
			this.nextParser = nextParser ;
		}		
		
		abstract IListenableExpression<?> parse();
		
		protected ElementaryParser getBrokenParser(){
			ElementaryParser parser = this ;
			if( parser.invalidLexeme != null ) return parser ;
			for( parser = this.nextParser ; parser != this ; parser = parser.nextParser )
				if( parser.invalidLexeme != null ) return parser ;
			
			return null ;
		}
		
	}

	static class ConditionTransactionParser extends ElementaryParser{		

		@Override
		IListenableExpression<?> parse() {
			
			IListenableExpression<?> condition = nextParser.parse() ;
			if( condition != null )
			{
				if( lexer.validPosition() )
				{
					Lexeme lx = lexer.getLexeme1() ;					
					if( lx.getKind() == ExpressionLexer.QUESTION_KIND )
					{
						lexer.shiftForward1() ;
						if( lexer.validPosition() )
						{
							IListenableExpression<?> successCase = nextParser.parse() ;
							if( successCase != null )
							{
								if( lexer.validPosition() )
								{
									lx = lexer.getLexeme1() ;
									if( lx.getKind() == ExpressionLexer.COLON_KIND )
									{
										lexer.shiftForward1() ;
										IListenableExpression<?> failCase = nextParser.parse() ;
										return failCase != null ? new ConditionalTransaction( condition, successCase, failCase ) : null ;
									}
									else{
										this.invalidLexeme = lx ;
										this.errorMessage = ERROR_COLON_EXPECTED ;
										return null ;
									}
								}
								else	return null ;
							}
							else  return null ;
						}
						else{
							this.invalidLexeme = lx ;
							this.errorMessage = ERROR_UNEXPECTED_END_OF_EXPRESSION ;
							return null ;
						}
					}
					else{
//						this.invalidLexeme = lx ;
//						this.errorMessage = ERROR_QUESTION_MARK_EXPECTED ;
						return condition ;
					}
				}
				else return condition ;
			}			
			else  return null ;			
		}
	}
	
	static class SelectorOperatorParser extends ElementaryParser{

		@Override
		IListenableExpression<?> parse() {
			
			IListenableExpression<?> left = nextParser.parse() ;
			if( left != null )
			{
				if( lexer.validPosition() )
				{
					Lexeme lx = lexer.getLexeme1() ;					
					if( lx.getKind() == ExpressionLexer.OPERATOR_KIND && lx.getContent().equals( SelectorOperatorExpression.SELECTOR_OPERATOR_STRING_LABEL ) )
					{
						lexer.shiftForward1() ;
						if( lexer.validPosition() )
						{
							IListenableExpression<?> member = nextParser.parse() ;
							if( member != null )
							{
								if( member instanceof ConstantExpression ){
									Object memberValue = member.getValue() ;
									if( memberValue instanceof String )
										return	new SelectorOperatorExpression( left, (String)memberValue ) ;
									if( memberValue instanceof IProperty )
										return	new SelectorOperatorExpression( left, member) ;
									else
										return null ;
								}
								else
									return new SelectorOperatorExpression( left, member ) ;								
							}
							else  return null ;
						}
						else{
							this.invalidLexeme = lx ;
							this.errorMessage = ERROR_UNEXPECTED_END_OF_EXPRESSION ;
							return null ;
						}
					}
					else{
//						this.invalidLexeme = lx ;
//						this.errorMessage = ERROR_OPERATOR_OR_SELECTOR_EXPECTED ;
						return left ;
					}
				}
				else return left ;
			}			
			else  return null ;			
		}
	}
	
	
	static class BinaryOperatorParser extends ElementaryParser{
		
		BinaryOperatorParser( HashSet<Integer> set)
		{
			super();
			this.acceptableOperatorsIdSet = set ;
		}
		
		HashSet<Integer> acceptableOperatorsIdSet ;
		int maxOperands ;
		
		protected void setMaxOperands(int i) {
			this.maxOperands = i ;			
		}
		
		@Override		
		IListenableExpression<?> parse()
		{
			ArrayList<IListenableExpression<?>> expressionsList = new ArrayList<IListenableExpression<?>>() ;
			ArrayList<Integer> operatorList = new ArrayList<Integer>() ;
			
			int i = 0 ;
			IListenableExpression<?> nextExpression = nextParser.parse() ;
			
			if( nextExpression != null )
			{
				expressionsList.add( nextExpression ) ;
				i++ ;
			
				for(  ; i < maxOperands ; )
				{					
					if ( lexer.validPosition() )
					{
						Lexeme lx = lexer.getLexeme1() ;
						if( lx.getKind() == ExpressionLexer.OPERATOR_KIND )
						{
							Integer operatorKind = BinaryOperator.getOperatorId( lx.getContent() ) ;
							if( operatorKind != null && acceptableOperatorsIdSet.contains( operatorKind ) )
							{
								operatorList.add( BinaryOperator.getOperatorId( lx.getContent() ) ) ;
								lexer.shiftForward1() ;
								if ( lexer.validPosition() )
								{
									nextExpression = nextParser.parse() ;
									if( nextExpression != null )
									{
										expressionsList.add( nextExpression ) ;
										i++ ;
									}								
									else
										return null ;
								}
								else{
									this.invalidLexeme = lx ;
									this.errorMessage = ERROR_UNEXPECTED_END_OF_EXPRESSION ;
									return null ;
								}
							}
							else break ;
						}
						else{
//							this.invalidLexeme = lx ;
//							this.errorMessage = ERROR_BINARY_OPERATOR_EXPECTED ;
							break ;
						}
					}
					else break ;
				}
			}
			if( i == 1 )
				return expressionsList.get(0) ;
						
			if( i > 1 )
			{
				BinaryExpression expr = new BinaryExpression( expressionsList.get(0), expressionsList.get(1), operatorList.get(0)) ;
				for( int j = 2 ; j < i ; j++ ){
					BinaryExpression tmpExpr = expr ;
					expr = new BinaryExpression( tmpExpr, expressionsList.get(j), operatorList.get(j-1)) ; 
				}
				return expr ;				
			}
			
			return null ;
		}
	}
	
	static class TermParser extends ElementaryParser{
		
		protected IExpressionEnvironment lookup ;
		protected IClassResolver classResolver ; 
		
		protected TermParser( IExpressionEnvironment lookup, IClassResolver classResolver ){
			super();
			this.classResolver = classResolver ;
			this.lookup = lookup ;
		}

		@SuppressWarnings("unchecked")
		@Override
		IListenableExpression<?> parse() {
			
			Lexeme lx = lexer.getLexeme1() ;
			if ( lx.getKind() == ExpressionLexer.OPEN_BRACKET2_KIND )
			{
				final IExpressionEnvironment oldLookup=lookup;
				try{
				lexer.shiftForward1() ;
				VariableExpression root = new VariableExpression();
				
				GetPropertyLookup gpl=new GetPropertyLookup(root, oldLookup.getClassResolver());
				gpl.setParentEnvironment(oldLookup);
				lookup=gpl;
				if( lexer.validPosition() ){
					IListenableExpression<?> result = nextParser.parse() ;
					if( lexer.validPosition() )
					{
						lx = lexer.getLexeme1() ;
						lexer.shiftForward1() ;
						if( lx.getKind() == ExpressionLexer.CLOSE_BRACKET2_KIND )
							return new ClosureExpression(root, (IListenableExpression<Object>) result) ;
						else{
							this.invalidLexeme = lx ;
							this.errorMessage = ERROR_CLOSE_BRACKET ;
							return null ;
						}
					}
					else{
						this.invalidLexeme = lx ;
						this.errorMessage = ERROR_CLOSE_BRACKET ;
						return null ;										
					}
				}
				else{
					this.invalidLexeme = lx ;
					this.errorMessage = ERROR_UNEXPECTED_END_OF_EXPRESSION ;				
					return null ;
				}
				}finally{
					lookup=oldLookup;
				}
			}
			else if ( lx.getKind() == ExpressionLexer.OPEN_BRACKET_KIND )
			{
				return parseBracketExpr(lx);
			}
			else{
				if( lx.getKind() == ExpressionLexer.OPERATOR_KIND )
				{
					lexer.shiftForward1() ;
					if( lexer.validPosition() ){
						IListenableExpression<?> operand = nextParser.parse() ;
						if( operand == null )
							return null ;
						String con=lx.getContent();
						if (con.equals("^")){
							return new HasValue(operand);
						}
						int operatorKind = UnaryOperator.getOperatorId( con ) ;
						return new UnaryExpression( operand, operatorKind ) ;																
					}
					else{
						this.invalidLexeme = lx ;
						this.errorMessage = ERROR_UNEXPECTED_END_OF_EXPRESSION ;						
						return null ;
					}
					
				}
				else{
					if( lx.getContent() == null ) return null ;
					
					lexer.shiftForward1() ;
					switch( lx.getKind() ){
					case ExpressionLexer.STRING_KIND : {					
						return new ConstantExpression( lx.getContent() ) ;
					}
					case ExpressionLexer.INTEGER_KIND : {
						return new ConstantExpression( Integer.parseInt(lx.getContent() ) ) ;
					}
					case ExpressionLexer.lONG_KIND : {
						return new ConstantExpression( Long.parseLong(lx.getContent() ) ) ;				
					}
					case ExpressionLexer.DOUBLE_KIND : {
						return new ConstantExpression( Double.parseDouble(lx.getContent() ) ) ;					
					}
					case ExpressionLexer.BOOLEAN_KIND : {
						return new ConstantExpression( Boolean.parseBoolean( lx.getContent() ) ) ;
					}				
					case ExpressionLexer.NULL_KIND : {
						return new ConstantExpression( null ) ;
					}
					case ExpressionLexer.IDENTIFIER_KIND : {
						Lexeme nextLexeme = lexer.getLexeme1();
						if (nextLexeme != null && nextLexeme.kind == ExpressionLexer.OPEN_BRACKET_KIND) { //handle method call
							if ("this".equals(lx.content)) {
								this.invalidLexeme = lx ;
								this.errorMessage = ERROR_INVALID_METHOD_CALL + lx.content;						
								return null ;
							}
							String token = lx.content;
							return parseMethodCall(token);
						}
						
						IListenableExpression<?> result = lookup.getBinding( lx.getContent() ) ; 
						if( result != null )
							return result ; 
						else{
							this.invalidLexeme = lx ;
							this.errorMessage = ERROR_BINDING_NOT_RECOGNIZED ;						
							return null ;
						}
					}
					case ExpressionLexer.CLASS_KIND : {
							if (lexer.getLexeme0() != null && lexer.getLexeme0().getKind() == ExpressionLexer.OPEN_BRACKET_KIND) {
								return parseMethodCall(lx.getContent());
							}
							Class<?> result=null;
							if (classResolver==null){
								try {
									result= ExpressionParserV2.class.getClassLoader().loadClass(lx.getContent());
								} catch (ClassNotFoundException e) {
									
								}
							}
							else{
								result = classResolver.resolveClass(lx.getContent() ) ;
							}
							if( result != null )								
								return new ConstantExpression( result ) ;								
							else{
								this.invalidLexeme = lx ;
								this.errorMessage = ERROR_CLASS_NOT_FOUND ;								
								return null ;
							}							
						}
					}
				}
				this.invalidLexeme = lx ;
				this.errorMessage = ERROR_EXPECTING_NUM_ID_CLASS_STRING_UNOP ;
				return null ;
			}
		}

		protected IListenableExpression<?> parseMethodCall(String token) {
			String methodName;
			int idx = token.lastIndexOf('.');
			if (idx < 0) {
				methodName = token;
				token = "this";
			} else {
				methodName = token.substring(idx + 1);
				token = token.substring(0,idx);
			}
			IListenableExpression<?> baseExpression = lookup.getBinding(token);
			if (baseExpression.getValue() == null) {
				try {
					Class<?> clazz = lookup.getClassResolver().resolveClass(token);
					if (clazz == null)
						clazz = Class.forName(token);
					baseExpression = new ConstantExpression(clazz);
				} catch (ClassNotFoundException e) {
					// Do nothing
				}
			}
			Lexeme nextLexeme2 = lexer.getNextLexeme(1);
			if (nextLexeme2 != null && nextLexeme2.kind == ExpressionLexer.CLOSE_BRACKET_KIND) {
				lexer.shiftForward1(); //skip (
				lexer.shiftForward1(); //skip )
				return new MethodCallExpression(baseExpression,methodName,null); //empty argument method call
			} else {
				return new MethodCallExpression(baseExpression,methodName,parseBracketExpr(lexer.getLexeme1()));
			}
		}
		
		protected IListenableExpression<?> parseBracketExpr(Lexeme lx) {
			lexer.shiftForward1() ;
			if( lexer.validPosition() ){
				IListenableExpression<?> result = nextParser.parse() ;
				if( lexer.validPosition() )
				{
					lx = lexer.getLexeme1() ;
					lexer.shiftForward1() ;
					if( lx.getKind() == ExpressionLexer.CLOSE_BRACKET_KIND )
						return result ;
					else{
						this.invalidLexeme = lx ;
						this.errorMessage = ERROR_CLOSE_BRACKET ;
						return null ;
					}
				}
				else{
					this.invalidLexeme = lx ;
					this.errorMessage = ERROR_CLOSE_BRACKET ;
					return null ;										
				}
			}
			else{
				this.invalidLexeme = lx ;
				this.errorMessage = ERROR_UNEXPECTED_END_OF_EXPRESSION ;				
				return null ;
			}
		}		
	}
	
	Lexeme invalidLexeme ;
	String errorMessage ;
	//TODO FIX NULLS in CALLS
	public IListenableExpression<?> parse( String inputString, IExpressionEnvironment lookup, IClassResolver classResolver )
	{
		String unitedString = uniteExpressions( inputString);
		if( unitedString == null )
		{
			if (inputString.indexOf('{')==-1){
				unitedString=inputString;
			}
			else{
			invalidLexeme = null ;
			errorMessage = INVALID_BRACKETS_COUNT ;
			}
		}
		ExpressionLexer lexer = new ExpressionLexer( unitedString ) ;
//		ExpressionLexer lexer = new ExpressionLexer( inputString.substring(1, inputString.length()-1) ) ;
		if ( lexer.isEmpty() )
			return null ;
		ElementaryParser elementaryParser = constructEntryParser( lexer, lookup, classResolver ) ;
		
		IListenableExpression<?> result = elementaryParser.parse() ;
		if( result != null )	
			return result ;
		
		ElementaryParser brokenParser = elementaryParser.getBrokenParser() ;
		if (brokenParser!=null){
		this.invalidLexeme = brokenParser.invalidLexeme ;
		this.errorMessage  = brokenParser.errorMessage  ;
		}
		return null ;
	}

	private ElementaryParser constructEntryParser( ExpressionLexer lexer, IExpressionEnvironment lookup, IClassResolver classResolver ) {

		ArrayList<HashSet<Integer>> HSList = BinaryOperator.getPriorityLayers() ;
		
		int parsersCount = HSList.size() + 3 ;
		int binaryParserStart = 2 ;
		ElementaryParser[] parserArray = new ElementaryParser[ parsersCount ] ;
	
		parserArray[0] = new ConditionTransactionParser() ;
		parserArray[1] = new SelectorOperatorParser() ;
		parserArray[parsersCount-1] = new TermParser(lookup,classResolver) ;
		
		for( int i = binaryParserStart ; i < parsersCount -1 ; i++ ){
			parserArray[i] = new BinaryOperatorParser( HSList.get( i-binaryParserStart )) ;
			((BinaryOperatorParser)parserArray[i]).setMaxOperands( Integer.MAX_VALUE );
		}
		((BinaryOperatorParser)parserArray[binaryParserStart+5]).setMaxOperands( 2 );
		((BinaryOperatorParser)parserArray[binaryParserStart+6]).setMaxOperands( 2 );
		
		for( int i = 0 ; i < parsersCount ; i++ ){
			parserArray[i].setLexer(lexer);
		}
		
		for( int i = 0 ; i < parsersCount-1 ; i++ )
			parserArray[i].setNextParser(parserArray[i+1]) ;
		
		parserArray[parsersCount-1].setNextParser(parserArray[0]) ;	

		return parserArray[0] ;
	}
	public Lexeme getInvalidLexeme(){ return invalidLexeme; }
	public String getErrorMessage(){ return errorMessage; }
	
	private String uniteExpressions( String _inputString ) 
	{
		String inputString = _inputString.replace("{", "{(") ;
		inputString =  inputString.replace("}", ")}") ;
		ArrayList<Integer> positions = new ArrayList<Integer>() ; 
		for( int p = 0 ; ; )
		{
			p = inputString.indexOf( '{', p ) ;
			if( p == -1 ) break ;
			positions.add(p) ;
			p = inputString.indexOf( '}', p ) ;
			if( p == -1 )
			{
				//report error
			}
			positions.add(p) ;			
		}
		positions.add(inputString.length()) ;
		
		if( positions.size() == 1 )
			return null ;
		
		String result = positions.get(0) == 0 ? "" : '\'' + includeEscBeforeQuote( inputString.substring(0, positions.get(0)) ) + "\' +" ;
		for( int i = 0 ; i < positions.size()-1 ; i+=2 )
		{
			result += inputString.substring( positions.get(i)+1, positions.get(i+1) ) ;
			int p1 = positions.get(i+1)+1 ;
			int p2 = positions.get(i+2)   ; 
			result += p1 == p2 ? "+" :"+\'" + includeEscBeforeQuote( inputString.substring( p1, p2) ) + "\'+" ;			
		}
		if( result.endsWith("+") )
			result = result.substring(0,result.length()-1) ;
		
		return result ;
	}
	private String includeEscBeforeQuote( String s )
	{
		String result = "" ;
		for( int i = 0 ; i < s.length() ; i++ ){
			char ch = s.charAt(i) ;
			result += ch != '\'' ? ch : "\\\'" ;
		}
		
		return result ;	
	}
	public String getCompleteErrorMessage(){
		
		return invalidLexeme == null ? "No errors" :
			   "Error. " + errorMessage + 
			   " Lexeme( " + invalidLexeme.content + ", " + 
			   invalidLexeme.offset + ", " + 
			   invalidLexeme.kind + ")." ;   
	}

	@SuppressWarnings("unchecked")
	@Override
	public IListenableExpression<Object> parse(String expresssion,
			IExpressionEnvironment env) {
		return (IListenableExpression<Object>) parse(expresssion, env,env.getClassResolver());
	}

}
