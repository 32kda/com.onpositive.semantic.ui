package com.onpositive.businessdroids.ui.themes;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

	private static ITheme currentTheme = new BlackTheme();
	private static List<ITheme> themes = new ArrayList<ITheme>();

	static {
		ThemeManager.themes.add(new BasicTheme());
		ThemeManager.themes.add(new BlackTheme());
		ThemeManager.themes.add(new BlueTheme());
	}

	public ITheme getCurrentTheme() {
		return ThemeManager.currentTheme;
	}

	public static void setCurrentTheme(ITheme currentTheme) {
		ThemeManager.currentTheme = currentTheme;
	}

	public void registerTheme(ITheme theme) {
		ThemeManager.themes.add(theme);
	}

	public void unregisterTheme(ITheme theme) {
		ThemeManager.themes.remove(theme);
	}

	public static ITheme[] getAvailableThemes() {
		return ThemeManager.themes.toArray(new ITheme[0]);
	}

}
