package com.onpositive.semantic.ui.xml;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.w3c.dom.Element;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.EditorBindingController;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.property.editors.IAllowsRegisterCommands;

public class CommandHandler implements IElementHandler {

	private final class KeyHandler extends AbstractHandler implements IBindable {
		private IBinding binding;

		public IBinding getBinding() {
			return this.binding;
		}

		public void setBinding(IBinding binding) {
			this.binding = binding;

		}

		public Object execute(ExecutionEvent arg0) throws ExecutionException {
			if (this.binding != null) {
				this.binding.actionPerformed(this, null);
			}
			return null;
		}
	}

	public CommandHandler() {
	}

	@SuppressWarnings("unchecked")
	public Object handleElement(Element element, Object parentContext,
			Context context) {
		if (parentContext instanceof AbstractUIElement<?>) {
			final IUIElement el = (IUIElement) parentContext;
			final KeyHandler keyHandler = new KeyHandler();
			final String attribute = element.getAttribute("bindTo");
			final String command = element.getAttribute("command");
			final EditorBindingController editorBindingController = new EditorBindingController(
					keyHandler, attribute) {

				boolean inited;

				public void hierarchyChanged(IUIElement element) {
					if (!this.inited) {
						final IAllowsRegisterCommands dd = (IAllowsRegisterCommands) element
								.getService(IAllowsRegisterCommands.class);
						if (dd != null) {
							dd.activateHandler(command, keyHandler);
							;
							this.inited = true;
						}
					}
					super.hierarchyChanged(element);
				}

			};
			editorBindingController
					.hierarchyChanged(el);
			el.addElementListener(editorBindingController);
		}
		return null;
	}
}
