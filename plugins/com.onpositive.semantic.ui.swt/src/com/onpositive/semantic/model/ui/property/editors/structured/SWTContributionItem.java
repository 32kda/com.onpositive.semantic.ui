package com.onpositive.semantic.model.ui.property.editors.structured;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

import com.onpositive.semantic.model.ui.actions.IContributionItem;

public class SWTContributionItem implements IContributionItem{

	org.eclipse.jface.action.IContributionItem item;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SWTContributionItem other = (SWTContributionItem) obj;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		return true;
	}

	public SWTContributionItem(org.eclipse.jface.action.IContributionItem item) {
		super();
		this.item = item;
	}

	public SWTContributionItem(
			IAction createAddFromRealm) {
		this.item=new ActionContributionItem(createAddFromRealm);
	}

	public org.eclipse.jface.action.IContributionItem getItem(){
		return item;
	}

	public void dispose() {
		item.dispose();
	}

	public void fill(Composite parent) {
		item.fill(parent);
	}

	public void fill(Menu parent, int index) {
		item.fill(parent, index);
	}

	public void fill(ToolBar parent, int index) {
		item.fill(parent, index);
	}

	public void fill(CoolBar parent, int index) {
		item.fill(parent, index);
	}

	public String getId() {
		return item.getId();
	}

	public boolean isEnabled() {
		return item.isEnabled();
	}

	public boolean isDirty() {
		return item.isDirty();
	}

	public boolean isDynamic() {
		return item.isDynamic();
	}

	public boolean isGroupMarker() {
		return item.isGroupMarker();
	}

	public boolean isSeparator() {
		return item.isSeparator();
	}

	public boolean isVisible() {
		return item.isVisible();
	}

	public void saveWidgetState() {
		item.saveWidgetState();
	}

	public void setParent(IContributionManager parent) {
		item.setParent(parent);
	}

	public void setVisible(boolean visible) {
		item.setVisible(visible);
	}

	public void update() {
		item.update();
	}

	public void update(String id) {
		item.update(id);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		
	}

	public void setEnabled(boolean b) {
		if (item instanceof ActionContributionItem){
			ActionContributionItem m=(ActionContributionItem) item;
			m.getAction().setEnabled(b);
		}
	}

	public String id() {
		return item.getId();
	}
}
