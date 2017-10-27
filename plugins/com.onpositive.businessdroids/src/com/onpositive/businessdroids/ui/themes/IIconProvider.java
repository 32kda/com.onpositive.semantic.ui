package com.onpositive.businessdroids.ui.themes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public interface IIconProvider {
	Drawable getUnsortedIcon(Context context);

	Drawable getSortUpIcon(Context context);

	Drawable getSortDownIcon(Context context);

	Drawable getOkIcon(Context context);

	Drawable getCancelIcon(Context context);

	Drawable getCloseIcon(Context context);

	Drawable getFilterIconBlack(Context context);

	Drawable getAddFilterIcon(Context context);

	Drawable getRemoveFilterIcon(Context context);

	Drawable getClearIcon(Context context);

	Drawable getListIcon(Context context);

	Drawable getGroupIcon(Context context);

	Drawable getUngroupIcon(Context context);

	Drawable getAggregateIcon(Context context);

	Drawable getHomeIcon(Context context);

	Drawable getSearchIcon(Context context);

	BitmapDrawable createBitmapDrawable(Bitmap bitmap);

	Drawable getColumnsIcon(Context context);

	Drawable getExpandIcon(Context context);

	Drawable getCollapseIcon(Context context);

	Drawable getSearchIconBlack(Context context);

	Drawable getDisableSearchIconBlack(Context context);

	Drawable getFilterIconWhite(Context context);

	Drawable getSearchEnabledIcon(Context context);

	Drawable getFilterModifiedIcon(Context context);

	Drawable getSettingsIcon(Context context);

	Drawable getThemeIcon(Context context);

	Drawable getHideColumnIcon(Context context);

	Drawable getReplaceColumnIcon(Context context);

	Drawable getNextIcon(Context context);
	
	Drawable getPreviousIcon(Context context);

}
