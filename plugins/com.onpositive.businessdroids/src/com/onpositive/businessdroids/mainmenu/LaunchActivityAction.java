package com.onpositive.businessdroids.mainmenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.onpositive.businessdroids.ui.actions.ActionContribution;

public class LaunchActivityAction extends ActionContribution {

	protected final Activity parentActivity;
	protected final String className;

	public LaunchActivityAction(Activity parentActivity,
			String text, Drawable icon, String className) {
		super(text, icon);
		this.parentActivity = parentActivity;
		this.className = className;
	}

	@Override
	protected void run() {
		Intent intent = new Intent();
		configureIntent(intent);
		intent.setClassName(parentActivity,className);
		intent.setAction(Intent.ACTION_VIEW);
		parentActivity.startActivity(intent);
	}

	protected void configureIntent(Intent intent) {
		// Do nothing; Override this to put extra data into intent
		
	}

}
