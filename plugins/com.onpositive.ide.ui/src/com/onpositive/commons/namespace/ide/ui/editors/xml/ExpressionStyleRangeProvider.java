package com.onpositive.commons.namespace.ide.ui.editors.xml;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.onpositive.semantic.model.expressions.impl.ExpressionLexer;
import com.onpositive.semantic.model.expressions.impl.ExpressionLexer.Lexeme;

public class ExpressionStyleRangeProvider {
	
	static Color kewWordColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA) ;
	
	StyleRange[] computeStyleRanges( int offset, String string, String type ){
		
		ArrayList<Integer> positions = new ArrayList<Integer>() ;  
		int l = string.length() ;
		boolean awaitingNewExpression = true ;
		if (type!=null&&type.equals("expression")){
			positions.add(0);
			positions.add(string.length());
		}
		for( int i = 0 ; i < l ; i++ )
		{
			if( awaitingNewExpression ){
				if( string.charAt(i) == '{' ){
					awaitingNewExpression = false ;
					positions.add(i+1) ;
				}
			}
			else{
				if( string.charAt(i) == '}' ){
					awaitingNewExpression = true ;
					positions.add(i) ;
				}				
			}
		}
		if( ( positions.size() & 1 ) != 0 )
			positions.add(l) ;
		
		int expressionsCount = positions.size() >> 1 ;
		ArrayList<StyleRange> styleRangeArray = new ArrayList<StyleRange>() ;
		for( int i = 0 ; i < expressionsCount ; i++ )
		{
			int expressionOffset = offset + positions.get(2*i) ;
			String expressionString = string.substring( positions.get(2*i), positions.get(2*i+1) ) ;
			ExpressionLexer lexer = new ExpressionLexer( expressionString ) ;
			for(  ; lexer.validPosition() ; lexer.shiftForward0() )
			{				
				Lexeme lx = lexer.getLexeme0() ;
				int rangeOffset  = expressionOffset + lx.getOffset() ;
				int lexemeLength = lx.getContent().length() ;
				switch( lx.getKind() ){
				
				case ExpressionLexer.OPERATOR_KIND		  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, Display.getCurrent().getSystemColor(SWT.COLOR_BLACK), null ) ) ;
					break; }
				case ExpressionLexer.IDENTIFIER_KIND	  :{ 
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN), null ) ) ;
					break;
					}
				case ExpressionLexer.STRING_KIND 		  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, Display.getCurrent().getSystemColor(SWT.COLOR_BLUE), null ) ) ;
					break; }
//				case ExpressionLexer.INTEGER_KIND		  :{
//					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, foreground, null, null )) ;
//					break; }
//				case ExpressionLexer.lONG_KIND			  :{
//					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, foreground, null, null )) ;
//					break; }
//				case ExpressionLexer.DOUBLE_KIND		  :{
//					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, foreground, null, null )) ;
//					break; }
				case ExpressionLexer.BOOLEAN_KIND		  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, kewWordColor, null ) ) ;
					break; }
				case ExpressionLexer.CLASS_KEYWORD_KIND	  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, kewWordColor, null ) ) ;
					break; }
				case ExpressionLexer.CLASS_KIND			  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, Display.getCurrent().getSystemColor(SWT.COLOR_BLACK), null ) ) ;
					break; }
				case ExpressionLexer.NULL_KIND			  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, kewWordColor, null ) ) ;
					break; }
				case ExpressionLexer.OPEN_BRACKET_KIND	  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED), null ) ) ;
					break; }
				case ExpressionLexer.CLOSE_BRACKET_KIND	  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED), null ) ) ;
					break; }
				case ExpressionLexer.OPEN_BRACKET2_KIND	  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED), null ) ) ;
					break; }
				case ExpressionLexer.CLOSE_BRACKET2_KIND	  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED), null ) ) ;
					break; }
				case ExpressionLexer.COLON_KIND		  	  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED), null ) ) ;
					break; }
				case ExpressionLexer.QUESTION_KIND	  	  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED), null ) ) ;
					break; }
				case ExpressionLexer.NEW_OPERATOR_KIND	  :{
					styleRangeArray.add( new StyleRange( rangeOffset, lexemeLength, kewWordColor, null ) ) ;
					break; }
				}
			}
		}
		
		StyleRange[] result = new StyleRange[ styleRangeArray.size() ] ;
		for( int i = 0 ; i < result.length ; i++ )
			result[i] = styleRangeArray.get(i) ;
		
		return result ;
	}

}
