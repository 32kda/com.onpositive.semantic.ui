package com.onpositive.commons.spelling.engine;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.osgi.framework.Bundle;

import com.onpositive.internal.ui.text.spelling.PreferenceConstants;
import com.onpositive.semantic.ui.text.spelling.Activator;

public class SpellingPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public SpellingPreferenceInitializer() {
	}

	
	public void initializeDefaultPreferences() {
		final Bundle bundle = Platform.getBundle("org.eclipse.jdt.ui");
		final IPreferenceStore preferenceStore = Activator.getSpellingPreferenceStore();
		if ((bundle != null) && false) {
			preferenceStore
					.setDefault(SpellingService.PREFERENCE_SPELLING_ENGINE,
							"org.eclipse.jdt.internal.ui.text.spelling.DefaultSpellingEngine");
		} else {
			preferenceStore.setDefault(
					SpellingService.PREFERENCE_SPELLING_ENGINE,
					"com.onpositive.text.spelling.DefaultSpellingEngine");
		}
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_DIGITS,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_MIXED,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_NON_LETTERS,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_SENTENCE,false);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_UPPER,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_URLS,true);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_PROBLEMS_THRESHOLD,100);
		preferenceStore.setDefault(PreferenceConstants.SPELLING_IGNORE_SINGLE_LETTERS,true);
		preferenceStore.setDefault(SpellingService.PREFERENCE_SPELLING_ENABLED,
				true);

	}

}
