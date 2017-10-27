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

@SuppressWarnings("unused")
public class BindingValidator implements ITypeValidator, ITypeValidatorDetailed {
	
	static String DEFAULT_ERROR_MESSAGE = "Invalid binding." ;
	static String BINDING_NOT_RECOGNISED_MESSAGE = "Binding not recognised." ;

	public ErrorInfo[] getErrors( IProject project,String value, DomainEditingModelObject element, String typeSpecialization ) {

		ErrorInfo[] resultingErrorArray = new ErrorInfo[1] ;
		process( project, value, element, typeSpecialization, resultingErrorArray );
		return resultingErrorArray ;
	}

	public String validate(IProject project, String value, DomainEditingModelObject element, String typeSpecialization) {

		return process( project, value, element, typeSpecialization, null );
	}

	public BindingValidator(){}

	public String process(IProject project, String value, DomainEditingModelObject element,
						  String typeSpecialization, ITypeValidatorDetailed.ErrorInfo[] errorsArray )
	{
		if( value == null || value.length() == 0 )
			return DEFAULT_ERROR_MESSAGE ;
		
		DomainEditingModelObject root = element.getRoot() ;		
		ValidationBindingLookup lookup = new ValidationBindingLookup( root, project) ;
		
		if( value.startsWith("{") )
		{//expression case
			int l = value.length();
			{
				
				int i = 1 ;
				char ch ;
				for( ; i < l ; i++ ){
					ch = value.charAt(i) ;
					if( ch == '{' )		return DEFAULT_ERROR_MESSAGE ;
					if( ch == '}' ) 	break ;
				}
				
				if( i != l-1 )
					return DEFAULT_ERROR_MESSAGE ;
			}
			//String string = value.substring(1, l-1) ;
			String string = value.substring( 0, l ) ;
			ExpressionValidator expressionValidator = new ExpressionValidator() ;
			
			if ( errorsArray != null && errorsArray.length > 0 ){
				
				ErrorInfo[] expressionErrorsArray = expressionValidator.getErrors(project, string, element, typeSpecialization) ;
				
				if( expressionErrorsArray == null || expressionErrorsArray.length == 0 || expressionErrorsArray[0] == null )
					return null ;
				
				errorsArray[0] = new ErrorInfo() ;					
				ErrorInfo targetErrorInfo = errorsArray[0] ;
				ErrorInfo sourceErrorInfo = expressionErrorsArray[0] ;
				
				targetErrorInfo.setOffset ( 1+sourceErrorInfo.getOffset() ) ;
				targetErrorInfo.setLength ( sourceErrorInfo.getLength() ) ;
				targetErrorInfo.setMessage( sourceErrorInfo.getMessage() ) ;
				return sourceErrorInfo.getMessage() ;
			}			
			return expressionValidator.validate( project, string, element, typeSpecialization ) ;
		}
		
		DomainEditingModelObject a = (DomainEditingModelObject) element.getParentNode();
		String ms = "";
		while (a != null) {
			IDocumentAttributeNode documentAttribute = a
					.getDocumentAttribute("bindTo");
			if (documentAttribute != null) {
				String attributeValue = documentAttribute.getAttributeValue();
				if (!attributeValue.equals("this")) {
					ms = attributeValue + "." + ms;
				}
			}
			a = (DomainEditingModelObject) a.getParentNode();
		}
		if (value.equals("this")){
			return null;
		}
		if( lookup.getBinding( ms+value ) == null )
		{
			if ( errorsArray != null && errorsArray.length > 0 ){
				
				errorsArray[0] = new ErrorInfo() ;
				ErrorInfo errorInfo = errorsArray[0] ;
				errorInfo.setOffset( 0 ) ;
				errorInfo.setLength( value.length() ) ;
				errorInfo.setMessage( BINDING_NOT_RECOGNISED_MESSAGE ) ;
				return BINDING_NOT_RECOGNISED_MESSAGE ;
			}
			return "BINDING_NOT_RECOGNISED_MESSAGE" ;
		}		
		return null ;
	}
}
