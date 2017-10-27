package com.onpositive.businessdroids.ui.actions;

import android.graphics.drawable.Drawable;

public abstract class ActionContribution extends AbstractListenable implements
		IContributionItem, IHasImage {

	public static final String ICON_PROP_ID = "icon";
	
	protected String text;
	protected Drawable icon;
	protected int style;

	public static final int NONE = 0;
	public static final int AS_CHECKBOX = 1;

	protected boolean selected;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		boolean oldSelected = this.selected;
		if (oldSelected != selected) {
			this.selected = selected;
			firePropertyChange("selected", oldSelected, selected);
		}
	}

	public ActionContribution(String text, Drawable icon, int style) {
		super();
		this.text = text;
		this.icon = icon;
		this.style = style;
	}

	public ActionContribution(String text, Drawable icon) {
		super();
		this.text = text;
		this.icon = icon;
	}

	@Override
	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public Drawable getIcon() {
		return this.icon;
	}

	public void setIcon(Drawable icon) {
		Drawable oldIcon = this.icon;
		this.icon = icon;
		firePropertyChange(ICON_PROP_ID,oldIcon,icon);
	}

	@Override
	public String getId() {
		return this.getText();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public void onRun() {
		if (style == AS_CHECKBOX) {
			setSelected(!selected);
		}
		run();
	}

	public int getStyle() {
		return style;
	}

	protected abstract void run();

}
