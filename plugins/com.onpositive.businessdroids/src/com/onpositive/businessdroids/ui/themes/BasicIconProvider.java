package com.onpositive.businessdroids.ui.themes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

public class BasicIconProvider implements IIconProvider {

	protected Drawable unsortedDrawable;
	protected Drawable sortedUpDrawable;
	protected Drawable sortedDownDrawable;

	protected Drawable okDrawable;
	protected Drawable cancelDrawable;
	protected Drawable filterDrawableBlack;
	protected Drawable filterDrawable;
	protected Drawable filterEnabledDrawable;
	protected Drawable searchDrawable;
	protected Drawable searchDrawableBlack;
	protected Drawable searchEnabledDrawable;
	protected Drawable disableSearchDrawableBlack;
	protected Drawable addFilterDrawable;
	protected Drawable removeFilterDrawable;
	protected Drawable clearDrawable;
	protected Drawable listDrawable;
	protected Drawable sortUpDrawable;
	protected Drawable sortDownDrawable;
	protected Drawable groupDrawable;
	protected Drawable removeGroupDrawable;
	protected Drawable aggregateDrawable;
	protected Drawable homeDrawable;
	protected Drawable columnsDrawable;
	protected Drawable expandDrawable;
	protected Drawable collapseDrawable;
	protected Drawable closeDrawable;
	protected Drawable settingsDrawable;
	protected Drawable themeDrawable;
	protected Drawable hideDrawable;
	protected Drawable replaceDrawable;
	protected Drawable nextDrawable;
	protected Drawable prevDrawable;

	@Override
	public Drawable getUnsortedIcon(Context context) {
		if (this.unsortedDrawable == null) {
			this.unsortedDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.unsorted));
		}
		return this.unsortedDrawable;
	}

	@Override
	public Drawable getSortUpIcon(Context context) {
		if (this.sortedUpDrawable == null) {
			this.sortedUpDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.arrow_up));
		}
		return this.sortedUpDrawable;
	}

	@Override
	public Drawable getSortDownIcon(Context context) {
		if (this.sortedDownDrawable == null) {
			this.sortedDownDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.arrow_down));
		}
		return this.sortedDownDrawable;
	}

	@Override
	public Drawable getOkIcon(Context context) {
		if (this.okDrawable == null) {
			this.okDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.ok));
		}
		return this.okDrawable;
	}

	@Override
	public Drawable getCancelIcon(Context context) {
		if (this.cancelDrawable == null) {
			this.cancelDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.cancel));
		}
		return this.cancelDrawable;
	}

	@Override
	public Drawable getFilterIconBlack(Context context) {
		if (this.filterDrawableBlack == null) {
			this.filterDrawableBlack = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.filter));
		}
		return this.filterDrawableBlack;
	}

	@Override
	public Drawable getFilterIconWhite(Context context) {
		if (this.filterDrawable == null) {
			this.filterDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.filter_white));
		}
		return this.filterDrawable;
	}

	@Override
	public Drawable getFilterModifiedIcon(Context context) {
		if (this.filterEnabledDrawable == null) {
			this.filterEnabledDrawable = this
					.createBitmapDrawable(BitmapFactory.decodeResource(
							context.getResources(),
							com.onpositive.businessdroids.R.drawable.filter_modified));
		}
		return this.filterEnabledDrawable;
	}

	@Override
	public Drawable getRemoveFilterIcon(Context context) {
		if (this.removeFilterDrawable == null) {
			this.removeFilterDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.remove_filter));
		}
		return this.removeFilterDrawable;
	}

	@Override
	public Drawable getAddFilterIcon(Context context) {
		if (this.addFilterDrawable == null) {
			this.addFilterDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.add_filter));
		}
		return this.addFilterDrawable;
	}

	@Override
	public Drawable getClearIcon(Context context) {
		if (this.clearDrawable == null) {
			this.clearDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.clear));
		}
		return this.clearDrawable;
	}

	@Override
	public Drawable getListIcon(Context context) {
		if (this.listDrawable == null) {
			this.listDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.list));
		}
		return this.listDrawable;
	}

	@Override
	public BitmapDrawable createBitmapDrawable(Bitmap bitmap) {
		BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
		bitmapDrawable.setGravity(Gravity.CENTER);
		return bitmapDrawable;
	}

	@Override
	public Drawable getGroupIcon(Context context) {
		if (this.groupDrawable == null) {
			this.groupDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.group));
		}
		return this.groupDrawable;
	}

	@Override
	public Drawable getUngroupIcon(Context context) {
		if (this.removeGroupDrawable == null) {
			this.removeGroupDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.remove_group));
		}
		return this.removeGroupDrawable;
	}

	@Override
	public Drawable getAggregateIcon(Context context) {
		if (this.aggregateDrawable == null) {
			this.aggregateDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.aggreg));
		}
		return this.aggregateDrawable;
	}

	@Override
	public Drawable getHomeIcon(Context context) {
		if (this.homeDrawable == null) {
			this.homeDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.home));
		}
		return this.homeDrawable;
	}

	@Override
	public Drawable getSearchIcon(Context context) {
		if (this.searchDrawable == null) {
			this.searchDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.search));
		}
		return this.searchDrawable;
	}

	@Override
	public Drawable getSearchIconBlack(Context context) {
		if (this.searchDrawableBlack == null) {
			this.searchDrawableBlack = this
					.createBitmapDrawable(BasicIconProvider
							.doInvert(BitmapFactory.decodeResource(
									context.getResources(),
									com.onpositive.businessdroids.R.drawable.search)));
		}
		return this.searchDrawableBlack;
	}

	@Override
	public Drawable getSearchEnabledIcon(Context context) {
		if (this.searchEnabledDrawable == null) {
			this.searchEnabledDrawable = this
					.createBitmapDrawable(BitmapFactory.decodeResource(
							context.getResources(),
							com.onpositive.businessdroids.R.drawable.search_enabled));
		}
		return this.searchEnabledDrawable;
	}

	@Override
	public Drawable getColumnsIcon(Context context) {
		if (this.columnsDrawable == null) {
			this.columnsDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.columns));
		}
		return this.columnsDrawable;
	}

	@Override
	public Drawable getExpandIcon(Context context) {
		if (this.expandDrawable == null) {
			this.expandDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.expand));
		}
		return this.expandDrawable;
	}

	@Override
	public Drawable getCollapseIcon(Context context) {
		if (this.collapseDrawable == null) {
			this.collapseDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.collapse));
		}
		return this.collapseDrawable;
	}

	public static Bitmap doInvert(Bitmap src) {
		// create new bitmap with the same settings as source bitmap
		Config config = src.getConfig();
		if (config == null) {
			config = Config.ARGB_8888;
		}
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				config);
		// color info
		int A, R, G, B;
		int pixelColor;
		// image size
		int height = src.getHeight();
		int width = src.getWidth();

		// scan through every pixel
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// get one pixel
				pixelColor = src.getPixel(x, y);
				// saving alpha channel
				A = Color.alpha(pixelColor);
				// inverting byte for each R/G/B channel
				R = 255 - Color.red(pixelColor);
				G = 255 - Color.green(pixelColor);
				B = 255 - Color.blue(pixelColor);
				// set newly-inverted pixel to output image
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final bitmap
		return bmOut;
	}

	@Override
	public Drawable getDisableSearchIconBlack(Context context) {
		if (this.disableSearchDrawableBlack == null) {
			this.disableSearchDrawableBlack = this
					.createBitmapDrawable(BitmapFactory.decodeResource(
							context.getResources(),
							com.onpositive.businessdroids.R.drawable.search_clean));
		}
		return this.disableSearchDrawableBlack;
	}

	@Override
	public Drawable getCloseIcon(Context context) {
		if (this.closeDrawable == null) {
			this.closeDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.close));
		}
		return this.closeDrawable;
	}

	@Override
	public Drawable getSettingsIcon(Context context) {
		if (this.settingsDrawable == null) {
			this.settingsDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.gear));
		}
		return this.settingsDrawable;
	}

	@Override
	public Drawable getThemeIcon(Context context) {
		if (this.themeDrawable == null) {
			this.themeDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.image));
		}
		return this.themeDrawable;
	}

	@Override
	public Drawable getHideColumnIcon(Context context) {
		if (this.hideDrawable == null) {
			this.hideDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.hide_column));
		}
		return this.hideDrawable;
	}

	@Override
	public Drawable getReplaceColumnIcon(Context context) {
		if (this.replaceDrawable == null) {
			this.replaceDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.replace_column));
		}
		return this.replaceDrawable;
	}
	
	@Override
	public Drawable getNextIcon(Context context) {
		if (this.nextDrawable == null) {
			this.nextDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.next));
		}
		return this.nextDrawable;
	}
	
	@Override
	public Drawable getPreviousIcon(Context context) {
		if (this.prevDrawable == null) {
			this.prevDrawable = this.createBitmapDrawable(BitmapFactory
					.decodeResource(context.getResources(),
							com.onpositive.businessdroids.R.drawable.prev));
		}
		return this.prevDrawable;
	}

}
