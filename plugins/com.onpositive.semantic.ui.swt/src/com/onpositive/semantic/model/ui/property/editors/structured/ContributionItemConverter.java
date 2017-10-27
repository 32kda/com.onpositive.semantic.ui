package com.onpositive.semantic.model.ui.property.editors.structured;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.BindedAction;

public class ContributionItemConverter {

	private static final class ActionWrapper extends
			org.eclipse.jface.action.Action {
		private final Action b;

		public int getStyle() {
			return b.getStyle();
		}		

		public void setId(String id) {
			b.setId(id);
		}

		public String getText() {
			return b.getText();
		}

		

		public boolean equals(Object obj) {
			return b.equals(obj);
		}

		public String getDescription() {
			return b.getDescription();
		}

		public void setText(String text) {
			b.setText(text);
		}

		

		public void setDescription(String text) {
			b.setDescription(text);
		}

		

		public void run() {
			b.run();
		}

		public org.eclipse.jface.resource.ImageDescriptor getImageDescriptor() {
			return SWTImageManager.getImageDescriptor(b.getImageDescriptor());
		}

		public org.eclipse.jface.resource.ImageDescriptor getDisabledImageDescriptor() {
			return SWTImageManager.getImageDescriptor(b.getDisabledImageDescriptor());
		}

		
		public int hashCode() {
			return b.hashCode();
		}

		public boolean isEnabled() {
			return b.isEnabled();
		}

		public void setChecked(boolean checked) {
			b.setChecked(checked);
		}

		public boolean isChecked() {
			return b.isChecked();
		}

		public String toString() {
			return b.toString();
		}

		private ActionWrapper(Action b) {
			this.b = b;
			b.addPropertyChangeListener(new PropertyChangeListener() {
				
				public void propertyChange(PropertyChangeEvent arg0) {
					firePropertyChange(arg0.getPropertyName(), arg0.getOldValue(), arg0.getNewValue());
				}
			});
		}
	}

	public static IContributionItem to(
			org.eclipse.jface.action.IContributionItem item) {
		if (item  instanceof ActionContributionItem){
			ActionContributionItem ac=(ActionContributionItem) item;
			IAction action = ac.getAction();
			if (action instanceof ActionWrapper){
				ActionWrapper bw=(ActionWrapper) action;
				return bw.b;
			}
		}
		return new SWTContributionItem(item);
	}

	public static org.eclipse.jface.action.IContributionItem from(IContributionItem item) {
		if (item instanceof SWTContributionItem){
			SWTContributionItem cm=(SWTContributionItem) item;
			return cm.getItem();
		}
//		if (item instanceof BindedAction) {
//			final BindedAction b = (BindedAction) item;
//			org.eclipse.jface.action.Action ac = new ActionWrapper(b);
//			return new ActionContributionItem(ac);
//		}
		if (item instanceof Action) {
			final Action a = (Action) item;
			org.eclipse.jface.action.Action ac = new ActionWrapper(a);
			return new ActionContributionItem(ac);
		}
		return null;
	}
}
