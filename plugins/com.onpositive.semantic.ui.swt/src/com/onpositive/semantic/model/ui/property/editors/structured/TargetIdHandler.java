package com.onpositive.semantic.model.ui.property.editors.structured;

import com.onpositive.commons.xml.language.AbstractContextDependentAttributeHandler;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IAttributeHandler;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;

public class TargetIdHandler extends AbstractContextDependentAttributeHandler {

	private final class HierarchyController extends ElementListenerAdapter {
		private final String vl;
		private final Object elementObject;

		private HierarchyController(String vl, Object elementObject) {
			this.vl = vl;
			this.elementObject = elementObject;
		}

		@Override
		public void hierarchyChanged( IUIElement<?> element) {
			doHandle((BasicUIElement<?>) elementObject, vl );
		}

		private IUIElement<?> oldValue;

		public void doHandle(BasicUIElement<?> bs, String vl) {

			ICompositeElement<?, ?> root = (ICompositeElement<?, ?>) bs.getRoot();
			if ( root == null )
				return ;
			
			IUIElement<?> element = root.getElement(vl);

			if (oldValue != element) {
				defaultHandler.handleAttribute(bs, element, null);
				oldValue = element;
			}
		}
	}

	public TargetIdHandler(IAttributeHandler defaultHandler) {
		super(defaultHandler);
	}

	public String handleAttribute(final Object elementObject, Object value, Context context)
	{
		BasicUIElement<?> el = (BasicUIElement<?>) elementObject;
		Object data = el.getData( defaultHandler.toString() );
		if (data != null) {
			HierarchyController c = (HierarchyController) data;
			el.removeElementListener(c);
		}
		if (value != null && value.toString().length() > 0) {
			final String vl = value.toString();
			HierarchyController disposeBindingListener = new HierarchyController(
					vl, elementObject);
			el.setData( defaultHandler.toString(), disposeBindingListener );
			el.addElementListener(disposeBindingListener);
			disposeBindingListener.doHandle(el, vl);
		}
		return null;
	}

	public String validate( String elementName, String attributeName ) {
		// TODO Auto-generated method stub
		return null;
	}

}
