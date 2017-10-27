package com.onpositive.businessdroids.ui;

import android.app.Activity;
import android.content.Context;

public class BD {

	static Class<? extends Activity> homeActivity;

	public static Class<? extends Activity> getHomeActivity(Context ct) {		
		return homeActivity;
	}

	public static void setHomeActivity(Class<? extends Activity> homeActivity) {
		BD.homeActivity = homeActivity;
	}
}
