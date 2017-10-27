package com.onpositive.semantic.ui.android.customwidgets.actionbar;

import android.app.Activity;
import android.content.Context;

/**
 * Use this for setting activity to return to when pressed Toolbar home button 
 * @author 32kda
 */
public class HomeActivityManager {

	static Class<? extends Activity> homeActivity;

	public static Class<? extends Activity> getHomeActivity(Context ct) {		
		return homeActivity;
	}

	public static void setHomeActivity(Class<? extends Activity> homeActivity) {
		HomeActivityManager.homeActivity = homeActivity;
	}
}
