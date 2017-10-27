package com.onpositive.semantic.ui.xml;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.w3c.dom.Element;

import com.onpositive.commons.Activator;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.MultiElementHandler;
import com.onpositive.semantic.model.api.roles.IObjectDecorator;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.generic.IMayHaveDecorators;
import com.onpositive.semantic.model.ui.generic.widgets.IActionInterceptor;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.columns.TableEnumeratedValueSelector;

public class ListChildrenHandler extends MultiElementHandler{

	public Object contentProvider(Element element, Object parentContext, Context context)
	{
		try{
			ListEnumeratedValueSelector<?>selector=(ListEnumeratedValueSelector<?>) parentContext;		
			Object newInstance = context.newInstance(element,"class");
			selector.setContentProvider((IContentProvider) newInstance);
		}catch (Exception e) {
			Activator.log(e);
		}
		return null;
	}
	
	public Object labelProvider(Element element, Object parentContext,
			Context context) {
		try{
			ListEnumeratedValueSelector<?>selector=(ListEnumeratedValueSelector<?>) parentContext;		
			Object newInstance = context.newInstance(element,"class");
			selector.setLabelProvider((ILabelProvider) newInstance);
		}catch (Exception e) {
			Activator.log(e);
		}
		return null;
	}
	public Object columns(Element element, Object parentContext, Context context)
	{
		try{
			TableEnumeratedValueSelector selector=(TableEnumeratedValueSelector) parentContext;		
			Object newInstance = context.newInstance(element,"class");
			selector.addColumn((Column) newInstance);
			
			}catch (Exception e) {
			Activator.log(e);
		}
		return null;
	}
	
	public Object decorator(Element element, Object parentContext,
			Context context) {
		final IObjectDecorator newInstance = (IObjectDecorator) context.newInstance(element,"value");
		if (parentContext instanceof IMayHaveDecorators<?>) {
			final IMayHaveDecorators<?> paDecorators = (IMayHaveDecorators<?>) parentContext;
			paDecorators.addDecorator(newInstance);
		}
		return null;
	}
	
	
	public Object interceptor( Element element, Object parentContext, Context context )
	{
		IListElement<?> selector = (IListElement<?>) parentContext;
		selector.addInterceptor( element.getAttribute("kind"),
								 ( IActionInterceptor) context.newInstance(element.getAttribute("class") ) );
		return null;
	}
}