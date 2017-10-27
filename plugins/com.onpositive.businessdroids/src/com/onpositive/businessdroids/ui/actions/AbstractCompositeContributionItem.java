package com.onpositive.businessdroids.ui.actions;

import android.graphics.drawable.Drawable;

public abstract class AbstractCompositeContributionItem extends
		AbstractListenable implements ICompositeContributionItem, IHasImage {

	protected final String text;
	protected final Drawable icon;

	public AbstractCompositeContributionItem(String text, Drawable icon) {
		super();
		this.text = text;
		this.icon = icon;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public String getId() {
		return this.getText();
	}

	@Override
	public Drawable getIcon() {
		return this.icon;
	}

}