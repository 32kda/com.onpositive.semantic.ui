package com.onpositive.commons.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.common.ui.roles.IWidgetCreator;
import com.onpositive.semantic.common.ui.roles.WidgetObject;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;

public class TitleDialogWidgetCreator implements IWidgetCreator {

	@SuppressWarnings("unchecked")
	public void showWidget(Binding bnd, WidgetObject object) {
		IPropertyEditor<?> editor;
		try {

			editor = (IPropertyEditor<AbstractUIElement<?>>) DOMEvaluator.getInstance()
					.evaluateLocalPluginResource(object.getBundle(),
							object.getResource(), bnd);
			
			final TitledDialog dlg = new TitledDialog((IPropertyEditor<AbstractUIElement<?>>) editor);
			dlg.setRole("new");
			int open = dlg.open();
			if (open==Dialog.OK){
				bnd.commit();
			}
			//dlg.getShell().setActive();
			bnd.dispose();
		} catch (final Exception e) {
			Activator.log(e);
		}
	}

}
