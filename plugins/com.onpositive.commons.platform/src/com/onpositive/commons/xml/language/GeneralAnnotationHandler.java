package com.onpositive.commons.xml.language;

import java.lang.reflect.Method;
import org.w3c.dom.Element;
import com.onpositive.core.runtime.Bundle;

public class GeneralAnnotationHandler extends ObjectReference implements IElementHandlerWithValidation {
	
	public GeneralAnnotationHandler(Bundle bundleContext, String className) {
		super(bundleContext, className);
	}
	
	@Override
	public Object getObject() {
		return this;
	}

	@Override
	protected Object newInstance() {		

		if (loadClass==null)
		{
			try{
				loadClass = this.bundleContext.loadClass(this.className);
			}		
			catch (final ClassNotFoundException e){
				e.printStackTrace();
				Activator.log(e);
				System.err.print("Error, cannot create instance of class " + loadClass.getName() );
				return null ;
			}
		}
		this.object = new AnnotationHandler(loadClass) ;
		return this.object;		
	}

	public Object handleElement(Element element, Object parentContext, Context context)
	{
		if( object == null )
			object = newInstance() ;
		return ((AnnotationHandler)this.object).handleElement(element, parentContext, context) ;
	}

	public Method getChildSetter(String childName) {
		// TODO Auto-generated method stub
		return null;
	}

}

