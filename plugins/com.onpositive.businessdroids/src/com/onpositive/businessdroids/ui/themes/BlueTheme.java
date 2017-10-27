package com.onpositive.businessdroids.ui.themes;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;

public class BlueTheme extends BasicTheme {

	public BlueTheme() {
		// isRowSeparatorsNeeded = true;
		this.isColumnSeparatorsNeeded = false;
	}

	@Override
	public int getBaseLeftPadding() {
		return 4;
	}

	@Override
	public int getBaseTopPadding() {
		return 4;
	}

	@Override
	public int getBaseBottomPadding() {
		return 4;
	}

	@Override
	public int getHeaderTopPadding() {
		return 4;
	}

	@Override
	public int getHeaderBottomPadding() {
		return 4;
	}

	@Override
	protected Drawable createBgDrawable1(Drawable separatorLinesDrawable) {
		// TODO Auto-generated method stub
		return super.createBgDrawable1(separatorLinesDrawable);
	}

	@Override
	protected void initBgDrawables() {
		this.recordBgDrawable1 = new ColorDrawable(Color.WHITE);
		this.recordBgDrawable2 = new ColorDrawable(0xfffafafa);
		// headerBgDrawable = new GradientDrawable(Orientation.TOP_BOTTOM,new
		// int[]{0xfff5f5f6,0xffe4e5e7});
		// selectedHeaderBgDrawable = new
		// GradientDrawable(Orientation.TOP_BOTTOM,new
		// int[]{0xffebf3fd,0xffabc7ec,0xffd9e8fb});
		this.selectedRecordBgDrawable = new ColorDrawable(Color.LTGRAY);
	}

	@Override
	protected Drawable getHeaderBgDrawable() {
		return new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
				0xfff5f5f6, 0xffe4e5e7 });
	}

	@Override
	protected Drawable getSelectedHeaderBgDrawable() {
		return new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
				0xffebf3fd, 0xffabc7ec, 0xffd9e8fb });
	}

	@Override
	public int getRowDividerColor() {
		return Color.rgb(0xc5, 0xc5, 0xc5);
	}

	@Override
	public int getColumnDividerColor() {
		return Color.LTGRAY;
	}

	@Override
	public float getHeaderFontShadowRadius() {
		return 2;
	}

	@Override
	public int getHeaderFontShadowColor() {
		return Color.LTGRAY;
	}

	@Override
	public PointF getHeaderFontShadowShift() {
		return new PointF(1, 1);
	}

	@Override
	public String getTitle() {
		return "White-blue theme";
	}

	@Override
	public int getViewBackgroundColor() {
		return 0xffdedede;
	}
}
