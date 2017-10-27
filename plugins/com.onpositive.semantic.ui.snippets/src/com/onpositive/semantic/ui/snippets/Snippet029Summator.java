package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.layout.GridLayout;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.commons.ui.dialogs.InputElementDialog;
import com.onpositive.commons.ui.dialogs.TitledDialog;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.common.ui.roles.WidgetRegistry;
import com.onpositive.semantic.model.api.expressions.BinaryExpression;
import com.onpositive.semantic.model.api.expressions.ExpressionParserV2;
import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.api.expressions.operatorimplementations.BinaryOperator;
import com.onpositive.semantic.model.api.property.java.JavaObjectManager;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.realm.AbstractFactory;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;

public class Snippet029Summator extends AbstractSnippet {

	String s1 = "qwer";
	String s2 = "12345";
	String res;
	
	BinaryExpression expr ;
	
	
	protected AbstractUIElement<?> createContent()
	{
//		final Container el = new Container();
//		el.setLayoutManager(new OneElementOnLineLayouter());
//		Binding binding = new Binding(this);
//		
//		final OneLineTextElement<String> str1 = new OneLineTextElement<String>(binding.getBinding("s1"));
//		final OneLineTextElement<String> str2 = new OneLineTextElement<String>(binding.getBinding("s2"));
//		ExpressionParserV2 parser = new ExpressionParserV2() ;
//		expr = (BinaryExpression)parser.parse( "s1+s2", binding, null ) ;
//		final OneLineTextElement<String> result = new OneLineTextElement<String>( new Binding(expr).getBinding("value"));
//		
//		el.add(str1);
//		el.add(str2);
//		el.add(result);
//
//		DisposeBindingListener.linkBindingLifeCycle(binding, el);
//		return el;
		
 
		{
			try {
				final Binding context = new Binding(this);
				ModelElement initial = new ModelElement();
				boolean createObject = WidgetRegistry.createObject(initial);
				System.out.println(initial);
				
				final AbstractUIElement<?> evaluateLocalPluginResource = (AbstractUIElement<?>) DOMEvaluator
						.getInstance().evaluateLocalPluginResource(	Snippet029Summator.class, "snippet029.dlf", context); //$NON-NLS-1$
				DisposeBindingListener.linkBindingLifeCycle(context, evaluateLocalPluginResource);
				//context.getBinding("textStr").setValue("",null);
				return evaluateLocalPluginResource;

			} catch (final Exception e) {
	 			e.printStackTrace();
				Activator.log(e);
			}
			return null;
		}
	}
	
	
	protected String getDescription() {
		return "Shows how to use operators"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "This samplle calculates sum"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}
}
