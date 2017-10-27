package com.onpositive.commons.namespace.ide.ui.completion;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeValidator;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeValidatorDetailed;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentAttributeNode;
import com.onpositive.ide.ui.bindings.BindingSchemeNode;
import com.onpositive.ide.ui.bindings.BindingSchemeTree;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.expressions.impl.ExpressionLexer;
import com.onpositive.semantic.model.expressions.impl.ExpressionParserV2;
import com.onpositive.semantic.model.expressions.impl.ExpressionLexer.Lexeme;

public class ExpressionValidator implements ITypeValidator, ITypeValidatorDetailed {
	
	static String NULL_EXPRESSION_ERROR = "Null expression." ;
	static String EMPTY_EXPRESSION_ERROR = "Empty expression." ;

	
	public ErrorInfo[] getErrors( IProject project,String value, DomainEditingModelObject element, String typeSpecialization ) {

		ErrorInfo[] resultingErrorArray = new ErrorInfo[1] ;
		process( project, value, element, typeSpecialization, resultingErrorArray );
		return resultingErrorArray ;
	}

	public String validate(IProject project, String value, DomainEditingModelObject element, String typeSpecialization) {

		return process( project, value, element, typeSpecialization, null );
	}

	public ExpressionValidator(){}

	public String process(IProject project, String value, DomainEditingModelObject element,
						  String typeSpecialization, ITypeValidatorDetailed.ErrorInfo[] errorsArray )
	{
		if( value == null )
			return NULL_EXPRESSION_ERROR ;
		
		if( value.length() == 0 )
			return EMPTY_EXPRESSION_ERROR ;
		
		DomainEditingModelObject root = element.getRoot() ;		
		ValidationBindingLookup lookup = new ValidationBindingLookup( root, project) ;
		lookup.tree.adjustTo(element);
		ValidationClassResolver classResolver = new ValidationClassResolver( JavaCore.create(project) ) ;
		ExpressionParserV2 parser = new ExpressionParserV2() ;		
		if( parser.parse( value, lookup, classResolver ) == null ){
			
			
			if ( errorsArray != null && errorsArray.length > 0 ){
				
				errorsArray[0] = new ErrorInfo() ;					
				ErrorInfo errorInfo = errorsArray[0] ;
				Lexeme invalidLexeme = parser.getInvalidLexeme() ;
				if( invalidLexeme == null )
					return null ;
				
				errorInfo.setOffset( invalidLexeme.getOffset() ) ;
				errorInfo.setLength( invalidLexeme.getContent().length() ) ;
				errorInfo.setMessage( parser.getErrorMessage() ) ;
				return parser.getErrorMessage() ;
			}				
			return parser.getErrorMessage() ;
		}
		else{
			//System.err.print( parser.getCompleteErrorMessage() ) ;
			return null ;
		}
	}
}
