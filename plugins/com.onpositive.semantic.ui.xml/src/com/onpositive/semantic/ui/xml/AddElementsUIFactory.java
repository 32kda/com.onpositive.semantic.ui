package com.onpositive.semantic.ui.xml;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IInitializer;
import com.onpositive.semantic.common.ui.roles.WidgetRegistry;
import com.onpositive.semantic.model.api.property.IObjectRealm;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.ITypedRealm;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionBinding;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;
import com.onpositive.semantic.model.ui.property.editors.structured.AbstractEnumeratedValueSelector;

/**
 * @author kor
 */
public class AddElementsUIFactory extends UIElementHandler {

	public static class AddElementActionBinding extends ActionBinding {

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

		public void doAction() {
			if (!this.selector.isValueAsSelection()) {
				throw new IllegalStateException();
			} else {
				final IRealm<?> realm = this.selector.getRealm();
				if (realm instanceof IObjectRealm<?>) {
					if (realm instanceof ITypedRealm<?>) {
						IBinding binding = this.selector.getBinding();
						WidgetRegistry
								.getInstance()
								.showAddToNewObjectRealmWidget(
										(ITypedRealm<?>) realm, this.typeId, this.themeId,binding==null?null:binding.getUndoContext());
					}
				} else {
					throw new UnsupportedOperationException();
				}
			}
		}

		public void setSelector(AbstractEnumeratedValueSelector<Object> object) {
			this.selector = object;
			if (object != null) {
				this.setReadOnly(false);
			}
		}

		public void setTargetTypeid(String attribute) {
			this.typeId = attribute;
		}

		public void setThemeId(String attribute) {
			this.themeId = attribute;
		}
	}

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		final AddElementActionBinding handleElement = new AddElementActionBinding();
		((Binding) parentContext).setBinding(
				element.getAttribute("id"), handleElement); //$NON-NLS-1$
		handleElement.setTargetTypeid(element.getAttribute("targetType"));
		handleElement.setThemeId(element.getAttribute("theme"));
		final String attribute = element.getAttribute("targetId"); //$NON-NLS-1$

		if ((attribute != null) && (attribute.length() > 0)) {
			context.addInitializer(new IInitializer() {

				@SuppressWarnings("unchecked")
				public void init(Context context) {
					handleElement
							.setSelector((AbstractEnumeratedValueSelector<Object>) context
									.getObject(attribute));
				}

			});
		}
		super.configProxy(handleElement, element, context);
		return handleElement;
	}
}