package com.onpositive.commons.namespace.ide.ui.completion;

import java.util.ArrayList;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.ide.ui.JavaTypeValueProvider;
import com.onpositive.semantic.model.expressions.impl.ExpressionLexer;
import com.onpositive.semantic.model.expressions.impl.ExpressionLexer.Lexeme;
import com.onpositive.semantic.model.expressions.operatorimplementations.BinaryOperator;
import com.onpositive.semantic.model.expressions.operatorimplementations.UnaryOperator;

public class ExpressionComplitionProvider {
	
	boolean gotExpressionError ;
	boolean insideExpression ;
	int pos ;
	
	String attributeName ;
	DomainEditingModelObject findElement ;
	ITextViewer viewer ;
	int offset ;
	String startString ;
	int lengthCompletion ;
	ArrayList<ICompletionProposal> result ;
	String fullString ;
	boolean addBraces ;
	String typeSpec ;
	int expressionStart, expressionEnd ;
	String expressionString, expressionStartString ;
	int regionOffset ;
	
	int proposalsKindsCount = 10 ;
	boolean[] proposalsFlags = new boolean[ proposalsKindsCount ] ;
	
	public ExpressionComplitionProvider(){
		for( int i = 0 ; i < proposalsKindsCount ; i++ )
			proposalsFlags[i] = false ;
	}
	
	public void fillProposals( String _attributeName, DomainEditingModelObject _findElement, ITextViewer _viewer,
			   int _offset, String _startString, int _lengthCompletion, ArrayList<ICompletionProposal> _result,
			   String _fullString, boolean _addBraces, String _typeSpec, String string )
	{
		attributeName = _attributeName ;
		findElement = _findElement ;
		viewer = _viewer ;
		offset = _offset ;
		startString = _startString ;
		lengthCompletion = _lengthCompletion ;
		result = _result ;
		fullString = _fullString ;
		addBraces = _addBraces ;
		typeSpec = _typeSpec ;
		
		
		
		checkPosition( startString ) ;
		if( gotExpressionError &&startString.length()>0) return ;
		if (!insideExpression){
			if (string!=null&&string.equals("expression")){
				insideExpression=true;
			}
		}
		if( insideExpression )
		{
			pos = offset - expressionStart ;
			regionOffset = offset - startString.length() ;
			
			expressionString = fullString.substring( expressionStart-regionOffset, expressionEnd-regionOffset ) ;
			expressionStartString = expressionString.substring( 0,pos ) ;
			
			ExpressionLexer lexer = new ExpressionLexer( expressionString ) ;
			if( !lexer.validPosition() ){				
				addExpressionBeginProposals(null) ;
				return ;
			}
			if( pos == 0 ){
				addExpressionBeginProposals(null) ;
				if( !lexer.validPosition() ){
					return ;
				}
				else{
					addLexemeProposals( lexer.getLexeme0() ,null) ;
					return ;
				}					
			}
	
			Lexeme lexeme = null, nextLexeme = null ; 
			String pBinding=null;
			String ls=null;
			for(  ;  ;  ){

				nextLexeme = lexer.getLexeme0() ;
				lexer.shiftForward0() ;
				if( nextLexeme.getOffset() > pos ){
					lexer.shiftBack0() ;
					 break ;
				}
				if (lexeme!=null&& lexeme.getContent().equals("]")){
					pBinding=null;
				}
				lexeme = nextLexeme ;
				if (lexeme.getKind()==ExpressionLexer.IDENTIFIER_KIND){
					ls=lexeme.getContent();
				}
				if (lexeme.getContent().equals("[")){
					pBinding=ls;
				}
				
				if( !lexer.validPosition() )
					break ;
			}
			
			if( lexeme.getOffset() == pos )
			{
//				lexer.shiftBack0() ;
				lexer.shiftBack0() ;
				lexer.shiftBack0() ;
				if( lexer.validPosition() ){
					Lexeme prevLexeme = lexer.getLexeme0() ;
					addLexemeProposals( prevLexeme,pBinding ) ;
				}
			}
			addLexemeProposals( lexeme,pBinding ) ;
			
		} else
			addExpressionInputProposal();
	}



	private void addExpressionBeginProposals(String p) {
		
		addBracketsProposals();
		addBindingProposals( null ,p) ;
		addClassKeywordProposals() ;		
	}
	
	private void addLexemeProposals( Lexeme lexeme, String pBinding ) {

		int lexemeOffset = lexeme.getOffset() ;
		int nextLexemeOffset = lexemeOffset + lexeme.getContent().length() ; 
		switch( lexeme.getKind() )
		{
			case ExpressionLexer.OPERATOR_KIND		  :{ 
				
				if( nextLexemeOffset == pos ){
					addBracketsProposals() ;
					addBindingProposals(null,pBinding) ;
					addClassKeywordProposals() ;
				}
				addUnaryOperatorProposals ( lexeme ) ;
				addBinaryOperatorProposals( lexeme ) ;				
							
				break;
				}
			case ExpressionLexer.IDENTIFIER_KIND	  :{
				
				addBindingProposals( lexeme,pBinding ) ;
				addReservedWordsProposals( lexeme ) ;
				if( lexemeOffset == pos )
					addUnaryOperatorProposals ( lexeme ) ;
				if( nextLexemeOffset == pos )
				{
					String[] binaryOperatorLables = BinaryOperator.getOperatorStringLabels() ;
					boolean b=false;
					for (String s:binaryOperatorLables){
						if (s.startsWith(lexeme.getContent())){
							addBinaryOperatorProposals (lexeme) ;
							b=true;
							break;
						}
					}
					if (!b){
						addBinaryOperatorProposals ( null ) ;
					}
				}
				
				break;
				}

			case ExpressionLexer.CLASS_KIND			  :{
				
				addClassNameProposals( lexeme ) ;
				break;
				}
			case ExpressionLexer.COLON_KIND		  	  :{ 
				
				addBracketsProposals() ;				
				break;
				}
			case ExpressionLexer.QUESTION_KIND	  	  :{ 
				
				addBracketsProposals() ;				
				break;
				}
			case ExpressionLexer.OPEN_BRACKET_KIND	  :{
				
				if( nextLexemeOffset == pos )
					addExpressionBeginProposals(pBinding) ;
			
				break;
				}
			case ExpressionLexer.OPEN_BRACKET2_KIND	  :{
				
				if( nextLexemeOffset == pos )
					addExpressionBeginProposals(pBinding) ;
			
				break;
				}
//			case ExpressionLexer.NEW_OPERATOR_KIND	  :{ break;}
//			case ExpressionLexer.CLASS_KEYWORD_KIND	  :{ break;}			
//			case ExpressionLexer.STRING_KIND 		  :{ break;}
//			case ExpressionLexer.INTEGER_KIND		  :{ break;}
//			case ExpressionLexer.lONG_KIND		      :{ break;}
//			case ExpressionLexer.DOUBLE_KIND		  :{ break;}
//			case ExpressionLexer.BOOLEAN_KIND		  :{ break;}			
//			case ExpressionLexer.NULL_KIND			  :{ break;}
//			case ExpressionLexer.CLOSE_BRACKET_KIND	  :{ break;}				
		}

		
	}

	

	void checkPosition( String startString )
	{
		
		insideExpression = false ;
		gotExpressionError = true ;
		int i = 0 ;
		int l = startString.length() ;
		for(  ; i < l ;  )
		{
			gotExpressionError = startString.charAt(i) == '}' ;
			
			if( startString.charAt(i++) == '{' ){
				insideExpression = true ;
				expressionStart = i ;
				for(  ; i < l ;  ){
					
					if( startString.charAt(i) == '{' ){
						gotExpressionError = true;
						break ;
					}
					if( startString.charAt(i++) == '}' ){
						insideExpression = false ;
						break ;
					}										
				}
			}
			if ( gotExpressionError ) break ;
		}

		l = fullString.length() ;
		expressionEnd = i ;
		for(  ; expressionEnd < l ; expressionEnd++ )
		{
			if( fullString.charAt(expressionEnd) == '}' )
				break ;
		}
		
		expressionStart += (offset-startString.length()) ;
		expressionEnd   += (offset-startString.length()) ;
	}
	
	private void addExpressionInputProposal()
	{
		result.add( new CompletionProposal( 
				"{}", offset, 0, 1,
				JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_IMPDECL),
				"{}", null, "input expression" )
		) ;
	}
	private void addBracketsProposals() {
		
		if( getBracketsProposalFlag() )	return ;
		setBracketsProposalFlag();
		
		result.add( new CompletionProposal( 
				"()", offset, 0, 1,
				JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_IMPDECL),
				"()", null, "input expression" )
		) ;
		
	}
	private void addClassKeywordProposals() {
		
		if( getClassKeywordProposalFlag() )	return ;
		setClassKeywordProposalFlag();
		
		result.add( new CompletionProposal( 
				"class:", offset, 0, 6,
				JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_ENUM_DEFAULT),
				"class:", null, "input expression" )
		) ;
		
	}
	private void addBinaryOperatorProposals( Lexeme lexeme) {
		
		if( getBinaryOperatorProposalFlag() )	return ;
		setBinaryOperatorProposalFlag();
		
		String lexemeStartString = "" ;
		if( lexeme != null )
				lexemeStartString = expressionString.substring( lexeme.getOffset(), pos ) ;

		String[] binaryOperatorLables = BinaryOperator.getOperatorStringLabels() ;
		
		for( String s : binaryOperatorLables )
		{			
			if( s != null && s.startsWith( lexemeStartString ) )
			{
				CompletionProposal cp = new CompletionProposal( 
										s, offset - lexemeStartString.length() , 
										lexemeStartString.length(), s.length(),
										JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_LOCAL_VARIABLE),
										s, null, null );
				result.add(cp);				
			}
		}		
	}
	private void addUnaryOperatorProposals( Lexeme lexeme) {
		
		if( getUnaryOperatorProposalFlag() )	return ;
		setUnaryOperatorProposalFlag();
		
		String lexemeStartString = "" ;
		if( lexeme != null )
				lexemeStartString = expressionString.substring( lexeme.getOffset(), pos ) ;

		String[] unaryOperatorLables = UnaryOperator.getOperatorStringLabels() ;
		
		for( String s : unaryOperatorLables )
		{			
			if( s != null && s.startsWith( lexemeStartString ) )
			{
				CompletionProposal cp = new CompletionProposal( 
										s, offset - lexemeStartString.length() , lexemeStartString.length(), s.length(),
										JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_LOCAL_VARIABLE),
										s, null, null );
				result.add(cp);				
			}
		}		
	}

	private void addReservedWordsProposals( Lexeme lexeme) {
		
		if( getReservedWordsProposalFlag() )	return ;
		setReservedWordsProposalFlag();
		
		String lexemeStartString = "" ;
		if( lexeme != null )
				lexemeStartString = expressionString.substring( lexeme.getOffset(), pos ) ;

		String[] reservedWords = ExpressionLexer.getReservedWords() ;
		
		for( String s : reservedWords )
		{			
			if( s != null && s.startsWith( lexemeStartString ) )
			{
				CompletionProposal cp = new CompletionProposal( 
										s, offset - lexemeStartString.length() , lexemeStartString.length(), s.length(),
										JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASSPATH_VAR_ENTRY),
										s, null, null );
				result.add(cp);				
			}
		}		
	}
	
	private void addBindingProposals( Lexeme lexeme, String pBinding )
	{
		if( getBindingProposalFlag() )	return ;
		setBindingProposalFlag();
		
		String lexemeFullString = "", lexemeStartString = "" ;
		if( lexeme != null ){
			lexemeFullString = lexeme.getContent() ;
			lexemeStartString = expressionString.substring( lexeme.getOffset(), pos ) ;
		}
		
		BindingCompletionProvider bindingProvider = new BindingCompletionProvider() ;
		bindingProvider.shift(pBinding);
		bindingProvider.fillProposals( null, findElement, null, offset, lexemeStartString, 0,
									   result, lexemeFullString,	false, null ) ;
	}
	
	private void addClassNameProposals( Lexeme lexeme )
	{
		if( getClassNameProposalFlag() )	return ;
		setClassNameProposalFlag();
		
		String lexemeFullString = "", lexemeStartString = "" ;
		if( lexeme != null ){
			lexemeFullString = lexeme.getContent() ;
			lexemeStartString = expressionString.substring( lexeme.getOffset(), pos ) ;
		}
		
		JavaTypeValueProvider provider = new JavaTypeValueProvider() ;		
		provider.fillProposals( null, findElement, null, offset, lexemeStartString, 0,
									   result, lexemeFullString,	false, null ) ;		
	}
	
	void setBindingProposalFlag()		 {	proposalsFlags[0] = true ;	}
	void setUnaryOperatorProposalFlag()	 {	proposalsFlags[1] = true ;	}
	void setBinaryOperatorProposalFlag() {	proposalsFlags[2] = true ;	}
	void setReservedWordsProposalFlag()	 {	proposalsFlags[3] = true ;	}
	void setClassKeywordProposalFlag()	 {	proposalsFlags[4] = true ;	}
	void setClassNameProposalFlag()		 {	proposalsFlags[5] = true ;	}
	void setBracketsProposalFlag()		 {	proposalsFlags[6] = true ;	}
	
	boolean getBindingProposalFlag()		 {	return proposalsFlags[0] ;	}
	boolean getUnaryOperatorProposalFlag()	 {	return proposalsFlags[1] ;	}
	boolean getBinaryOperatorProposalFlag()	 {	return proposalsFlags[2] ;	}
	boolean getReservedWordsProposalFlag()	 {	return proposalsFlags[3] ;	}
	boolean getClassKeywordProposalFlag()	 {	return proposalsFlags[4] ;	}
	boolean getClassNameProposalFlag()		 {	return proposalsFlags[5] ;	}
	boolean getBracketsProposalFlag()		 {	return proposalsFlags[6] ;	}	
}
