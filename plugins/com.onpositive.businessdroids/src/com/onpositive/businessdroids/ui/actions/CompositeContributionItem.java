package com.onpositive.businessdroids.ui.actions;

import java.util.ArrayList;
import java.util.Collection;

import android.graphics.drawable.Drawable;
/**
 * Composite contribution item containing randomly added item 
 * @author 32kda
 *
 */
public class CompositeContributionItem extends
		AbstractCompositeContributionItem {
	
	protected final Collection<IContributionItem> items;

	public CompositeContributionItem(String text, Drawable icon) {
		super(text, icon);
		items = new ArrayList<IContributionItem>();
	}
	
	public CompositeContributionItem(String text, Drawable icon, Collection<IContributionItem> items) {
		super(text, icon);
		this.items = items;
	}
	
	public void addItem(IContributionItem item) {
		items.add(item);
	}
	
	public void removeItem(IContributionItem item) {
		items.remove(item);
	}

	@Override
	public IContributionItem[] getChildren() {
		return items.toArray(new IContributionItem[0]);
	}

	@Override
	public boolean isEnabled() {
		for (IContributionItem item : items) {
			if (item.isEnabled())
				return true;
		}
		return false;
	}

}
