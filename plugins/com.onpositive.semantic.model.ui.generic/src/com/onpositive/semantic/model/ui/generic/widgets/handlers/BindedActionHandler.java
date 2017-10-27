package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.ContributionManager;
import com.onpositive.semantic.model.ui.actions.DelegatedBindedAction;
import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class BindedActionHandler extends AbstractActionElementHandler {

	public static class ManagerHolder extends ActionsSetting {

		public ManagerHolder(IUIElement<?> parentUI, boolean toToolbar) {
			super(parentUI, toToolbar);
		}

		ContributionManager man;
		ActionsSetting pcontext;
		
		@Override
		public void addAction(IContributionItem basicAction, Element element) {
			man.add(basicAction);
		}
	}

	protected Action contribute(ActionsSetting parentContext, Context context,
			Element element) {
		if (element.getNodeName().equals("menu")) {
			ContributionManager mq = new ContributionManager();
			handleAction(element, parentContext, mq, parentContext.gui);
			ManagerHolder mn = new ManagerHolder(parentContext.gui, false);
			mn.man = mq;
			mn.pcontext = parentContext;
			DOMEvaluator.evaluateChildren(element, mn, context);
			return mq;
		}
		BindedAction m = new BindedAction(new Binding(""));
		String attribute = element.getAttribute("class");
		if (attribute.length() > 0) {
			m = new DelegatedBindedAction(IAction.AS_PUSH_BUTTON, attribute,
					context.getClassLoader(), parentContext.getUI());
		}
		handleAction(element, parentContext, m, parentContext.gui);

		// super.contribute(parentContext, context, element);
		// parentContext.addAction(m, element);
		return m;
	}
}
