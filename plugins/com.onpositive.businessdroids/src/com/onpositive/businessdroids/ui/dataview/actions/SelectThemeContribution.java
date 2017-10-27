package com.onpositive.businessdroids.ui.dataview.actions;

import com.onpositive.businessdroids.ui.actions.AbstractCompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.SetCurrentThemeAction;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.themes.ITheme;
import com.onpositive.businessdroids.ui.themes.ThemeManager;

import android.graphics.drawable.Drawable;


public class SelectThemeContribution extends AbstractCompositeContributionItem {

	protected final StructuredDataView dataView;

	public SelectThemeContribution(Drawable icon, StructuredDataView dataView) {
		super("", icon);
		this.dataView = dataView;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getText() {
		return "Select theme";
	}

	@Override
	public IContributionItem[] getChildren() {
		ITheme[] availableThemes = ThemeManager.getAvailableThemes();
		IContributionItem[] result = new IContributionItem[availableThemes.length];
		for (int i = 0; i < availableThemes.length; i++) {
			result[i] = new SetCurrentThemeAction(
					availableThemes[i].getTitle(), this.icon,
					availableThemes[i], this.dataView);
		}
		return result;
	}

}
