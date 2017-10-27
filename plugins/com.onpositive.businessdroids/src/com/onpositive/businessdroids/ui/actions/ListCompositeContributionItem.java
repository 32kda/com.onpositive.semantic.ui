package com.onpositive.businessdroids.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.graphics.drawable.Drawable;

public class ListCompositeContributionItem extends ActionContribution implements
		ICompositeContributionItem {
	List<IContributionItem> childItems = new ArrayList<IContributionItem>();

	public ListCompositeContributionItem(String text, Drawable icon,
			Collection<IContributionItem> childItems) {
		super(text, icon);
		this.childItems.addAll(childItems);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public boolean addContributionItem(IContributionItem object) {
		return this.childItems.add(object);
	}

	public boolean removeContributionItem(Object object) {
		return this.childItems.remove(object);
	}

	public int getItemCount() {
		return this.childItems.size();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public IContributionItem[] getChildren() {
		return this.childItems.toArray(new IContributionItem[0]);
	}

}
