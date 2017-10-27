package com.onpositive.semantic.ui.snippets.engine ; 

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.expressions.BinaryExpression;
import com.onpositive.semantic.model.api.expressions.ConditionalTransaction;
import com.onpositive.semantic.model.api.expressions.Controller;
import com.onpositive.semantic.model.api.expressions.ExpressionParserV2;
import com.onpositive.semantic.model.api.expressions.IClassResolver;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.ParserClassResolver;
import com.onpositive.semantic.model.api.expressions.operatorimplementations.BinaryOperator;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;
import com.onpositive.semantic.model.ui.property.editors.structured.ComboEnumeratedValueSelector;
import com.onpositive.semantic.ui.snippets.AbstractSnippet;

public class Snippet030EngineSnippet extends AbstractSnippet{

	@Required
	String type0 ;
	@Required
	String type1 ;
	
	@Override
	protected AbstractUIElement<?> createContent() {
		
		final Container el = new Container();
		el.setLayoutManager(new OneElementOnLineLayouter());
		Binding binding = new Binding(this);
		
		ExpressionParserV2 parser = new ExpressionParserV2() ;
		ClassLoader classLoader = (new EngineSpecs()).getClass().getClassLoader() ;
		IClassResolver parserClassResolver = new ParserClassResolver(classLoader) ; 
		
		String realmExpressionString = 
			"{type0=='Electric'?" +
			"(new com.onpositive.semantic.ui.snippets.engine.EngineSpecs$ElectricSubTypes)->realm:" +
			"(new com.onpositive.semantic.ui.snippets.engine.EngineSpecs$ExplosionSubTypes)->realm}" ;
		
		String captionExpressionString = "{type0 == 'Electric' ? 'Electric types' : 'Explosion types'}" ;
		
		IListenableExpression<?> realmExpr = parser.parse( realmExpressionString, binding, parserClassResolver ) ;
		if( realmExpr == null ){
			System.err.print( parser.getCompleteErrorMessage() ) ;
			return null ;
		}
		IListenableExpression<?> captionExpr = parser.parse( captionExpressionString, binding, null ) ;
		if( captionExpr == null ){
			System.err.print( parser.getCompleteErrorMessage() ) ;
			return null ;
		}
		
		final ComboEnumeratedValueSelector<String> combo0 = new ComboEnumeratedValueSelector<String>( binding.getBinding("type0") ) ;
		combo0.setRealm( new EngineSpecs.Types().getRealm( null ) ) ;
		combo0.setCaption( "Engine type" ) ;
		
		
		Binding binding2 = binding.getBinding("type1");
		
		final ComboEnumeratedValueSelector<String> combo1 = new ComboEnumeratedValueSelector<String>( binding2 ) ;
		Controller.bind(combo1,"Realm",realmExpr );
		Controller.bind(combo1,"Caption",captionExpr);
		combo1.setCaption( "none" ) ;
		
		el.add(combo0);
		el.add(combo1);	

		DisposeBindingListener.linkBindingLifeCycle(binding, el);
		return el;
	}

	@Override
	protected String getName() {
		return "conditional combo example";
	}

	@Override
	protected String getDescription() {
		return "Here we use ConditionalTransaction to switch combos";
	}

	@Override
	public String getGroup() {
		return "Java";
	}

}
