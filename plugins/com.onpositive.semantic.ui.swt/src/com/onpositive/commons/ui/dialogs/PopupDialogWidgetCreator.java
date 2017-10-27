package com.onpositive.commons.ui.dialogs;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.common.ui.roles.IWidgetCreator;
import com.onpositive.semantic.common.ui.roles.WidgetObject;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;

public class PopupDialogWidgetCreator implements IWidgetCreator {

	public void showWidget(Binding bnd, WidgetObject object) {
		IPropertyEditor<?> editor;
		try {

			editor = (IPropertyEditor<?>) DOMEvaluator.getInstance()
					.evaluateLocalPluginResource(object.getBundle(),
							object.getResource(), bnd);

			final InputElementDialog dlg = new InputElementDialog((IPropertyEditor<AbstractUIElement>) editor);
			dlg.open();
			dlg.getShell().setActive();
			DisposeBindingListener.linkBindingLifeCycle(bnd, (AbstractUIElement<?>) editor
					.getUIElement());
			//bnd.dispose();
		} catch (final Exception e) {
			Activator.log(e);
		}
	}
}
