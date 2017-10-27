package com.onpositive.businessdroids.ui.themes;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.actions.ICompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.presenters.IContributionPresenter;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.ArrowDrawable;


public interface ITheme {

	public Drawable[] getHeaderBackgroundDrawables(AbstractViewer dataView,
			int[] fieldWidths);

	public Drawable getRecordBackgroundDrawable(AbstractViewer dataView,
			int position, int[] columnWidths, int rowHeight);

	public Drawable[] getFooterBackgroundDrawables(AbstractViewer dataView,
			int[] columnWidths);

	public Drawable getGroupBackgroundDrawable(AbstractViewer dataView,
			int position, int[] columnWidths, int measuredHeight);

	public Drawable getQuickActionBackgroundDrawable();

	public Drawable getSelectedQuickActionBackgroundDrawable();

	public Drawable getActionBarBackgroundDrawable();

	public Drawable getActionBarButtonBackgroundDrawable();

	public Drawable getDialogBackgroundDrawable();

	public int getHeaderFontColor();

	public int getRecordFontColor();

	public int getDialogTitleFontColor();

	public int getBaseLeftPadding();

	public int getBaseTopPadding();

	public int getBaseRightPadding();

	public int getBaseBottomPadding();

	public int getHeaderTopPadding();

	public int getHeaderBottomPadding();

	public int getSortArrowPadding();

	public int getSortArrowColor();

	public int getSortArrowOffset();

	public abstract int getColumnDividerColor();

	public ArrowDrawable getArrowDrawable(IColumn column);

	public int getViewBackgroundColor();

	public int getRowDividerColor();

	public float getHeaderFontShadowRadius();

	public PointF getHeaderFontShadowShift();

	public int getHeaderFontShadowColor();

	public float getFooterFontShadowRadius();

	public PointF getFooterFontShadowShift();

	public int getFooterFontShadowColor();

	public int getSortArrowBorderColor();

	public int getIndicatorBound();

	/**
	 * @return This theme's human readable title
	 */
	public String getTitle();

	public int getViewBackgroundFontColor();

	public int getActionBarTextColor();

	public IIconProvider getIconProvider();

	public float getActionBarTextSize();

	public boolean showFilterIndicators();

	public IApplicationMessagesProvider getLabelProvider();

	public int getMaxFilterDialogItemCount();

	

	Drawable getUnselectableRecordBackgroundDrawable(
			AbstractViewer recordDescriptor, int position, int[] columnWidths,
			int rowHeight);

	IContributionPresenter getContributionPresenter(int level,
			ICompositeContributionItem item);

	

	int getPreferedListItemHeight(Context ct);

	int getMinListItemHeight(Context ct);
}
