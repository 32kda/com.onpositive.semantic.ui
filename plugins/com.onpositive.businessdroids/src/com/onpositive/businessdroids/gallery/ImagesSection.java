package com.onpositive.businessdroids.gallery;

import com.onpositive.businessdroids.ui.IContextAwareSection;
import com.onpositive.businessdroids.ui.IFormSection;

import android.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


public abstract class ImagesSection implements IContextAwareSection{

	protected Context context;
	protected ImageAdapter adapter;
	
	public String getTitle() {
		return "Images";
	}

	public View createView(final Context ctx, int width, int height) {
		this.context = ctx;
		//		ImageView gl = new ImageView(ctx);
//		IPropertyMetadata property = collection.getProperty(IPropertyConstants.MAIN_PICTURE_PROPERTY_METADATA_ID);
//		Object value = collection.getValue(number, property);
		GridView imageGrid = new GridView(ctx);
		imageGrid.setNumColumns(GridView.AUTO_FIT);
		imageGrid.setColumnWidth(getColumnWidth(width));
		adapter = createImageAdapter(ctx);
		imageGrid.setAdapter(adapter);
		imageGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		imageGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				startViewActivity(ctx, adapter, position);
			}
		});
		return imageGrid;
	}

	protected abstract int getColumnWidth(int totalWidth);
	
	protected abstract int getColumnHeight(int totalWidth);

	protected abstract ImageAdapter createImageAdapter(final Context ctx);

	public Drawable getIcon() {
		return Resources.getSystem().getDrawable(R.drawable.ic_menu_gallery);
	}


	protected abstract void startViewActivity(final Context ctx, final ImageAdapter adapter, int position);
	
	
	@Override
	public void setContext(Context ctx) {
		context = ctx;
	}
	
	public boolean isEnabled() {
		if (adapter == null)
			adapter = createImageAdapter(context);
		return adapter.getCount() > 0;
	}
}
