package com.onpositive.semantic.model.expressions.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.onpositive.semantic.model.expressions.operatorimplementations.BinaryOperator;
import com.onpositive.semantic.model.expressions.operatorimplementations.UnaryOperator;

public class ExpressionLexer {
	
	protected String inputString ;
	protected int pos = 0 ;
	protected ArrayList<Lexeme> lexemeList =new ArrayList<ExpressionLexer.Lexeme>();
	int lexemeListSize = 0 ;
	
	public static final int OPERATOR_KIND		  =  1 ;
	public static final int IDENTIFIER_KIND		  =  2 ;
	public static final int STRING_KIND 		  =  3 ;
	public static final int INTEGER_KIND		  =  4 ;
	public static final int lONG_KIND			  =  5 ;
	public static final int DOUBLE_KIND			  =  6 ;
	public static final int BOOLEAN_KIND		  =  7 ;
	public static final int CLASS_KEYWORD_KIND	  =  8 ;
	public static final int CLASS_KIND			  =  9 ;
	public static final int NULL_KIND			  = 10 ;
	public static final int OPEN_BRACKET_KIND	  = 11 ;
	public static final int CLOSE_BRACKET_KIND	  = 12 ;
	public static final int COLON_KIND		  	  = 13 ;
	public static final int QUESTION_KIND	  	  = 14 ;
	public static final int NEW_OPERATOR_KIND	  = 15 ;
	public static final int OPEN_BRACKET2_KIND	  = 16 ;
	public static final int CLOSE_BRACKET2_KIND	  = 17 ;
	
	protected static HashMap<String,Integer > kindMap = new HashMap<String, Integer>() ;
	
	static{
		kindMap.put("true"  , BOOLEAN_KIND ) ;
		kindMap.put("false" , BOOLEAN_KIND ) ;
		kindMap.put("null"  , NULL_KIND    ) ;
		kindMap.put("class" , CLASS_KEYWORD_KIND ) ;
		kindMap.put( BinaryOperator.INSTANCEOF_OPERATOR_STRING_LABEL , OPERATOR_KIND ) ;
		kindMap.put( BinaryOperator.CONTAINS_OPERATOR_STRING_LABEL   , OPERATOR_KIND ) ;
		kindMap.put( BinaryOperator.AND_LABEL   , OPERATOR_KIND ) ;
		kindMap.put( BinaryOperator.OR_LABEL   , OPERATOR_KIND ) ;
		kindMap.put( BinaryOperator.FILTER_BY_LABEL   , OPERATOR_KIND ) ;
		kindMap.put( BinaryOperator.TRANSFORM_LABEL  , OPERATOR_KIND ) ;
		kindMap.put( BinaryOperator.ORDER_BY_LABEL  , OPERATOR_KIND ) ;
		kindMap.put( UnaryOperator.NEW_OPERATOR_STRING_LABEL     , NEW_OPERATOR_KIND ) ;
		kindMap.put( "["  , OPEN_BRACKET2_KIND ) ;
		kindMap.put( "]"     , CLOSE_BRACKET2_KIND ) ;
	}
	
	public static String[] getReservedWords(){ return new String[]{ "true", "false", "null"} ; }
	
	
	public static class Lexeme{

		String content ;
		int kind ;
		int offset ;
		
		Lexeme( String s, int offset ){
			content = s ;
			this.offset = offset ;
		}
		Lexeme( char ch, int offset ){
			content = "" + ch ;
			this.offset = offset ;
		}
		Lexeme( String s, int offset, int kind ){
			content = s ;
			this.offset = offset ;
			this.kind = kind ;
		}
		Lexeme( char ch, int offset, int kind ){
			content = "" + ch ;
			this.offset = offset ;
			this.kind = kind ;
		}
		public String getContent(){	return content ; }
		public int getKind(){ return kind ;	}
		public int getOffset(){ return offset ;	}
	}
	public ExpressionLexer( String inputString){
		
		this.inputString = killWhiteSpacesInTheEnd( inputString ) ;
		this.parse() ;
		lexemeListSize = lexemeList.size() ;
		pos = 0 ;
	}
	
	private String killWhiteSpacesInTheEnd(String string) {
		pos = string.length()-1 ;
		for( ; pos >= 0 && Character.isWhitespace( string.charAt(pos) ) ; pos-- );
		return string.substring(0, ++pos );
	}

	protected void parse()
	{
		pos = 0 ;
		int ppos=-1;
		int inputStringLength = inputString.length() ;
		for( ; pos < inputStringLength ;  ){

			Lexeme lexeme = readLexeme() ;
			if (ppos==pos){
				pos++;
			}
			ppos=pos;
			if( lexeme.kind == CLASS_KEYWORD_KIND ){
			//here we process lexeme 3-sequences  "class:idetifier"
				lexemeList.add( lexeme ) ;
				if( pos >= this.inputString.length() ) return ;
				lexeme = readLexeme() ;
				if( lexeme.kind == COLON_KIND )
				{
					if( pos >= this.inputString.length() ) return ;
					lexeme = readLexeme() ;
					if( lexeme.kind == IDENTIFIER_KIND ){
						lexeme.kind = CLASS_KIND ;
						lexemeList.add( lexeme ) ;
					}
					else{
						//TODO reportError()
						lexeme.kind = CLASS_KIND ;
						lexemeList.add( lexeme ) ;
					}
				}
				else{
					//TODO reportError()
				}
				continue ;
			}
			if( lexeme.kind == NEW_OPERATOR_KIND ){
				//here we process lexeme sequences  "new idetifier"
				lexeme.kind = OPERATOR_KIND ;
				lexemeList.add( lexeme ) ;
				if( pos >= this.inputString.length() ) return ;
				lexeme = readLexeme() ;
				if( lexeme.kind == IDENTIFIER_KIND ){
					lexeme.kind = CLASS_KIND ;
					lexemeList.add( lexeme ) ;
				}
				else{
					if (lexeme.kind == CLASS_KEYWORD_KIND) { //Skip "class:" prefix for "new" operator
						Lexeme lx = readLexeme();
						if (lx.kind == COLON_KIND) {
							lexeme = readLexeme();
						}
					}
					//TODO reportError() ;
					lexeme.kind = CLASS_KIND ;
					lexemeList.add( lexeme ) ;
				}
				continue ;
			}
			if( lexeme.kind == IDENTIFIER_KIND ){
				int previousIndex = lexemeList.size()-1 ;
				if( previousIndex >= 0 )
				{ 
//					Lexeme previous = lexemeList.get( previousIndex ) ;				
//					if( previous.content.equals( SelectorOperatorExpression.SELECTOR_OPERATOR_STRING_LABEL ) )
//						lexeme.kind = STRING_KIND ;
				}
			}

			lexemeList.add( lexeme ) ;
		}
	}
	
	protected Lexeme readLexeme()
	{
		for( ; Character.isWhitespace( inputString.charAt(pos) ) && pos < inputString.length() ; pos++ );
		
		char ch = inputString.charAt( pos ) ;
		if( ch == '[' )
			return new Lexeme( ch, pos++, OPEN_BRACKET2_KIND ) ;
		
		if( ch == ']' )
			return new Lexeme( ch, pos++, CLOSE_BRACKET2_KIND ) ;
		
		if( ch == '(' )
			return new Lexeme( ch, pos++, OPEN_BRACKET_KIND ) ;
		
		if( ch == ')' )
			return new Lexeme( ch, pos++, CLOSE_BRACKET_KIND ) ;
		
		if( ch == ':' )
			return new Lexeme( ch, pos++, COLON_KIND ) ;
		
		if( ch == '?' )
			return new Lexeme( ch, pos++, QUESTION_KIND ) ;
				
		if( ch == '\'' )
			return readStringLexeme() ;
		
		if( Character.isJavaIdentifierPart(ch)||ch=='@' )
			return readGeneralLexeme() ;
			
		return readStandartOperatorLexeme() ;			
	}
	
	

	private Lexeme readStringLexeme() {
		StringBuilder builder = new StringBuilder() ;
		int pos_ = pos++ ;// we don't read the first char'\'' ;
		char curCh = ' ' , prevCh ;
		int inputStringLength = inputString.length();
		for( ; pos<inputStringLength ; )
		{
			prevCh = curCh ;
			curCh = inputString.charAt( pos++ ) ; 
			if( curCh == '\'' ){
				if( prevCh == '\\' )
					builder.deleteCharAt(builder.length()-1) ;
				else
					break ;
			}
			
			builder.append(curCh) ;	
		}
		return new Lexeme(builder.toString(), pos_, STRING_KIND );
	}
	private Lexeme readStandartOperatorLexeme() {

		int pos_ = pos ;
		StringBuilder builder = new StringBuilder()  ;
		int inputStringLength = inputString.length();
		for( ; pos<inputStringLength ; )
		{
			char ch = inputString.charAt(pos) ;
			if( Character.isJavaIdentifierPart(ch) || ch == ')' || ch == '@' || ch == '('  || Character.isWhitespace(ch) || ch == '\'' || ch == '\\' )	
				{
					
					break ;
				
				}
			pos++ ;
			builder.append( ch ) ;			
		}
		return new Lexeme( builder.toString(), pos_, OPERATOR_KIND );
	}

	private Lexeme readGeneralLexeme() {//class, boolean, number, operator, null, identifier

		int pos_ = pos ;
		StringBuilder builder = new StringBuilder()  ;
		int inputStringLength = inputString.length();
		for( ; pos<inputStringLength ; )
		{
			char ch = inputString.charAt(pos) ;
			if( !Character.isJavaIdentifierPart(ch) && ch != '.' && ch != '$'&& ch != '@' )	break ;
			pos++ ;
			builder.append( ch ) ;			
		}
		String string = builder.toString() ;
		return new Lexeme( string, pos_, detectLexemeKind(string) );
	}

	protected int detectLexemeKind(String string) {
		
		Integer kind = kindMap.get(string) ;
		if( kind != null )
			return kind ;

		try{
			long longValue = Long.parseLong( string ) ;
			return ( longValue < Integer.MAX_VALUE ) ? INTEGER_KIND : lONG_KIND ;
		}
		catch( NumberFormatException e ){}
		
		try{
			Double.parseDouble( string ) ;
			return DOUBLE_KIND ; 
		}
		catch( NumberFormatException e ){}
		
		//return STRING_KIND;
		
		return IDENTIFIER_KIND;
		
		
	}

	Lexeme getLexemeAndShiftForward0()
	{
		if( pos < lexemeListSize )
			return lexemeList.get( pos++ ) ;
		else
			return null ;
	}
	public Lexeme getLexeme0()
	{
		if( pos < lexemeListSize )
			return lexemeList.get( pos ) ;
		else
			return null ;
	}
	public void shiftBack0(){
		pos-- ;
	}
	public void shiftForward0(){
		pos++ ;
	}
	
	Lexeme getLexemeAndShiftForward1()
	{
		Lexeme res = getLexeme1() ;
		shiftForward1() ;
		return res ;
	}
	public Lexeme getLexeme1()
	{
		if( pos < lexemeListSize ){
			Lexeme lx = lexemeList.get( pos ) ;
			if( lx.kind == CLASS_KEYWORD_KIND ){
				pos++ ;
				return getLexeme1() ;
			}
			else return lx ;
		}
		else
			return null ;
	}
	public void shiftBack1(){
		pos-- ;
		if( lexemeList.get( pos ).getKind() == CLASS_KEYWORD_KIND )		shiftBack1() ;			
	}
	public void shiftForward1(){
		pos++ ;
		if( pos < lexemeListSize )
			if( lexemeList.get( pos ).getKind() == CLASS_KEYWORD_KIND )		shiftForward1() ;
	}
	
	public boolean validPosition(){
		return pos < lexemeListSize && pos >= 0 ;
	}
	
	public boolean isEmpty()
	{
		return lexemeList == null || lexemeList.size() == 0 ;
	}
	
	public Lexeme getNextLexeme(int shift) {
		int nextPos = pos + shift;
		if( nextPos < lexemeListSize )
			return lexemeList.get( nextPos ) ;
		return null;
	}

}
