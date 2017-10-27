package com.onpositive.semantic.ui.xml;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.common.ui.roles.WidgetRegistry;
import com.onpositive.semantic.model.api.property.IObjectRealm;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.ITypedRealm;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionBinding;
import com.onpositive.semantic.model.ui.property.editors.structured.AbstractEnumeratedValueSelector;

public class AddElementActionBinding extends ActionBinding {

	AbstractEnumeratedValueSelector<?> selector;

	private String typeId;

	private String themeId;

	public String getTypeId() {
		return this.typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getThemeId() {
		return this.themeId;
	}

	public AddElementActionBinding() {
		this.setReadOnly(true);
	}

	public void doAction()
	{
		if (!this.selector.isValueAsSelection())
			throw new IllegalStateException();

		else{
			final IRealm<?> realm = this.selector.getRealm();
			if (realm instanceof IObjectRealm<?>)
			{
				if (realm instanceof ITypedRealm<?>)
				{
					IBinding binding = this.selector.getBinding();
					WidgetRegistry.getInstance().showAddToNewObjectRealmWidget(	(ITypedRealm<?>) realm, this.typeId ,
																				 this.themeId,binding==null?null:binding.getUndoContext() );
				}
			}
			else
				throw new UnsupportedOperationException();
		}
	}

	@HandlesAttributeDirectly("targetId")
	public void setSelector(final String object) {
		final IUIElement<?> adapter = getRoot().getAdapter(IUIElement.class);
		if (adapter != null) {
			adapter.addElementListener(new ElementListenerAdapter() {
				@SuppressWarnings("unchecked")
				@Override
				public void hierarchyChanged(IUIElement<?> element)
				{
					IUIElement<?> element2 = ((ICompositeElement<?, ?>) adapter).getElement(object);
					
					if (element2 instanceof AbstractEnumeratedValueSelector<?>)
						AddElementActionBinding.this.setSelector((AbstractEnumeratedValueSelector<Object>) element2);
				}
			});
		}
	}
	public void setSelector(AbstractEnumeratedValueSelector<Object> object) {
		this.selector = object;
		if (object != null) {
			this.setReadOnly(false);
		}
	}

	@HandlesAttributeDirectly("targetType")
	public void setTargetTypeid(String attribute) {
		this.typeId = attribute;
	}

	@HandlesAttributeDirectly("theme")
	public void setThemeId(String attribute) {
		this.themeId = attribute;
	}
}
