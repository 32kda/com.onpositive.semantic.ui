package com.onpositive.businessdroids.ui.themes;

import com.onpositive.businessdroids.ui.AbstractViewer;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;


public class BlackTheme extends BasicTheme {

	public BlackTheme() {
		this.isRowSeparatorsNeeded = true;
		this.isColumnSeparatorsNeeded = false;
	}

	@Override
	public int getBaseLeftPadding() {
		return 4;
	}

	@Override
	public int getBaseTopPadding() {
		return 2;
	}

	@Override
	public int getBaseBottomPadding() {
		return 2;
	}

	@Override
	public int getHeaderTopPadding() {
		return 6;
	}

	@Override
	public int getHeaderBottomPadding() {
		return 6;
	}

	@Override
	protected void initBgDrawables() {
		this.recordBgDrawable1 = new ColorDrawable(0xff1a232b);
		this.recordBgDrawable2 = new ColorDrawable(0xff232d38);
		this.selectedRecordBgDrawable = new ColorDrawable(0xffe48627);
	}

	@Override
	public int getRowDividerColor() {
		return 0xff101010;
	}

	@Override
	public int getColumnDividerColor() {
		return 0xff373c4b;
	}

	@Override
	public int getHeaderFontColor() {
		return 0xffffffff;
	}

	@Override
	public int getRecordFontColor() {
		return 0xffffffff;
	}

	@Override
	public int getSortArrowColor() {
		return 0xffffffff;
	}

	@Override
	public int getSortArrowBorderColor() {
		return Color.GRAY;
	}

	// We override methods below because gradient drawable works badly
	// when shared to several instances, need to create it one again for every
	// bg

	@Override
	protected Drawable getHeaderBgDrawable() {
		return new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
				0xff545b73, 0xff2c303c });
	}

	@Override
	public Drawable[] getFooterBackgroundDrawables(AbstractViewer dataView,
			int[] columnWidths) {
		return super.getFooterBackgroundDrawables(dataView, columnWidths);
	}

	@Override
	protected Drawable getSelectedHeaderBgDrawable() {
		return new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
				0xff6882aa, 0xff475d81, 0xff405475 });
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
	public String getTitle() {
		return "Black theme";
	}

	@Override
	public int getViewBackgroundColor() {
		return Color.BLACK;
	}

	@Override
	public int getViewBackgroundFontColor() {
		return Color.WHITE;
	}

	@Override
	public Drawable getDialogBackgroundDrawable() {
		return new ColorDrawable(Color.BLACK);
	}

}
