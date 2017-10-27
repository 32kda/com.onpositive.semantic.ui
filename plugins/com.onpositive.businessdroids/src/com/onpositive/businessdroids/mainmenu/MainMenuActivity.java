package com.onpositive.businessdroids.mainmenu;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onpositive.businessdroids.ui.actions.ActionContribution;

public abstract class MainMenuActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
//		GridView gridView = new GridView(this);
//		if (getWindowManager().getDefaultDisplay().getWidth() > getWindowManager().getDefaultDisplay().getHeight()) 
//			gridView.setNumColumns(3);
//		else
//			gridView.setNumColumns(2);
//		
//		BitmapDrawable icon = new BitmapDrawable(BitmapFactory.decodeResource(
//				getResources(),
//				com.onpositive.fighters.R.drawable.icon));
//		BitmapDrawable drawable = new BitmapDrawable(BitmapFactory.decodeResource(
//				getResources(),
//				com.onpositive.fighters.R.drawable.table_green));
//		gridView.setAdapter(new MainMenuAdapter(this, new LaunchActivityAction(this, "Open Table", drawable, FightersTable.class), new StubAction("Sample", icon), new StubAction("Sample", icon)));
//		gridView.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//		gridView.setBackgroundDrawable(new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{Color.LTGRAY, Color.GRAY, Color.DKGRAY}));
//		gridView.setBackgroundDrawable(new MainMenuBackgroundDrawable(this));
		RelativeLayout layout = new RelativeLayout(this);
		MainMenuViewer view = new MainMenuViewer(this);
		view.setItems(getItems());
		view.setTitle(getActionBarTitle());
		view.setId(101);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		TextView textView = new TextView(this);
		textView.setId(102);
		params.addRule(RelativeLayout.ABOVE,textView.getId());
		layout.addView(view, params);
		textView.setGravity(Gravity.CENTER);
		textView.setPadding(15,15,15,5);
		textView.setTextColor(Color.BLACK);
		textView.setBackgroundColor(Color.WHITE);
		textView.setText(getSubText());
		params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		params.alignWithParent = true;
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		textView.setLayoutParams(params);
		layout.addView(textView);
		setContentView(layout);
		super.onCreate(savedInstanceState);
	}

	protected abstract String getActionBarTitle();

	protected abstract CharSequence getSubText();

	protected abstract ActionContribution[] getItems();
	
}
