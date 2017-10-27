package com.onpositive.ide.ui;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeHyperlinkProvider;
import com.onpositive.semantic.model.expressions.impl.ExpressionLexer;
import com.onpositive.semantic.model.expressions.impl.ExpressionLexer.Lexeme;

public class ExpressionHyperlinkProvider implements ITypeHyperlinkProvider {

	public IHyperlink[] calculateHyperlinks(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String startString, int lengthCompletion,
			String fullString, String typeSpecialization) {
		
		ExpressionLexer lexer = new ExpressionLexer( fullString ) ;
		if( !lexer.validPosition() )
			return null ;
		
		int stringCursorPosition = startString.length() ;
		String pBinding=null;
		String ls=null;
		Lexeme lexeme = null ;
		for( ; lexer.validPosition() ; )
		{			
			lexeme = lexer.getLexeme0() ;
			
			if( lexeme.getOffset() < stringCursorPosition ){
				if (lexeme!=null&& lexeme.getContent().equals("]")){
					pBinding=null;
				}
				if (lexeme.getKind()==ExpressionLexer.IDENTIFIER_KIND){
					ls=lexeme.getContent();
				}
				if (lexeme.getContent().equals("[")){
					pBinding=ls;
				}
				lexer.shiftForward0() ;
			}
			else{
				lexer.shiftBack0() ;
				if( lexer.validPosition() )
					lexeme = lexer.getLexeme0();
				break ;
			}
		}
		
		String lexemeFullString = lexeme.getContent() ;
		String lexemeStartString = startString.substring( lexeme.getOffset() ) ; 
		
		switch( lexeme.getKind() ){
		
		case( ExpressionLexer.CLASS_KIND ) : {
			
			return (new JavaTypeHyperlinkProvider()).calculateHyperlinks(
					attributeName, findElement, viewer, offset + lexeme.getOffset(), lexemeStartString,
					lexemeFullString.length(), lexemeFullString, typeSpecialization)	;
		}
		case( ExpressionLexer.IDENTIFIER_KIND ) : {
			
			return (new BindingHyperlinkProvider()).calculateHyperlinks(
					attributeName, findElement, viewer, offset + lexeme.getOffset(), lexemeStartString,
					lexemeFullString.length(),pBinding!=null?pBinding+"."+ lexemeFullString:lexemeFullString, typeSpecialization)	;
		}
		}
		
		return null;
	}

}
