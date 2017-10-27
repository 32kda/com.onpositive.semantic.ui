package com.onpositive.businessdroids.ui.themes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.actions.ICompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IExtendedContributionItem;
import com.onpositive.businessdroids.ui.actions.presenters.DialogContributionPresenter;
import com.onpositive.businessdroids.ui.actions.presenters.IContributionPresenter;
import com.onpositive.businessdroids.ui.actions.presenters.QuickActionBarContributionPresenter;
import com.onpositive.businessdroids.ui.actions.presenters.TreeContributionPresenter;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.ArrowDrawable;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.ColumnSeparatorDrawable;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.HatchDrawable;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.RowSeparatorDrawable;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.SeparatorDrawable;

public class BasicTheme implements ITheme {

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;		
		return true;
	}

	protected static final int HEADER_COLOR = Color.GRAY;

	protected static final int RECORD_COLOR_1 = Color.LTGRAY;
	protected static final int RECORD_COLOR_2 = Color.WHITE;
	protected static final int SELECTED_RECORD_COLOR = Color.YELLOW;

	protected static final int FONT_COLOR = Color.BLACK;
	protected static final int DIALOG_BG_COLOR = Color.WHITE;
	protected static final int COLUMN_DIVIDER_COLOR = Color.DKGRAY;
	protected static final int ROW_DIVIDER_COLOR = Color.rgb(0xed, 0xed, 0xed);
	protected static final int COLUMN_DIVIDER_WIDTH = 2;

	// Background drawables for different items
	protected Drawable recordBgDrawable1;
	protected Drawable recordBgDrawable2;
	protected Drawable selectedRecordBgDrawable;
	protected Drawable selectedHeaderBgDrawable;
	protected Drawable headerBgDrawable;

	protected boolean showFilterIndicators = true;

	public boolean isShowFilterIndicators() {
		return this.showFilterIndicators;
	}

	Integer mp;
	Integer mf;

	private DisplayMetrics displayMetrics;
	
	@Override
	public int getMinListItemHeight(Context ct) {
		if (mf != null) {
			return mf;
		}
		TypedValue typedValue = new TypedValue();
		Theme theme=ct.getTheme();
		theme.resolveAttribute(android.R.attr.expandableListViewStyle, typedValue , true);
		TypedArray typedArray = theme.obtainStyledAttributes(typedValue.resourceId, new int[] { android.R.attr.groupIndicator });
		StateListDrawable groupIndicator = (StateListDrawable)typedArray.getDrawable(0);
		mf = groupIndicator.getIntrinsicHeight();		
		return (int) mf;
	}

	@Override
	public int getPreferedListItemHeight(Context ct) {
		if (mp != null) {
			return mp;
		}
		android.util.TypedValue value = new android.util.TypedValue();
		ct.getTheme().resolveAttribute(
				android.R.attr.listPreferredItemHeight, value, true);
		//String s = TypedValue.coerceToString(value.type, value.data);
		DisplayMetrics displayMetrics = getDisplayMetrics();
		int ret = TypedValue.complexToDimensionPixelSize(value.data,
				displayMetrics);
		mp = ret;
		return ret;
	}

	protected DisplayMetrics getDisplayMetrics() {
		if (displayMetrics!=null){
			return displayMetrics;
		}
		displayMetrics = Resources.getSystem()
				.getDisplayMetrics();
		return displayMetrics;
	}

	public void setShowFilterIndicators(boolean showFilterIndicators) {
		this.showFilterIndicators = showFilterIndicators;
	}

	@Override
	public boolean showFilterIndicators() {
		return this.showFilterIndicators;
	}

	// icons
	protected IIconProvider iconProvider;
	protected IApplicationMessagesProvider labelProvider = new BasicAppLabelProvider();

	// protected StateListDrawable recordBackgroundDrawable1;
	protected StateListDrawable recordBackgroundDrawable2;
	protected Drawable selectedRecordBackgroundDrawable;
	/**
	 * Drawable for first column header control
	 */
	protected StateListDrawable firstColumnDrawable;
	protected int[] columnWidths;

	protected boolean isColumnSeparatorsNeeded = true;
	protected boolean isRowSeparatorsNeeded = false;

	protected Drawable unselectedBgDrawable1;
	protected Drawable unselectedBgDrawable2;
	protected Drawable selectedBgDrawable;

	public BasicTheme() {
		this.initBgDrawables();
		this.initFirstColumnDrawable();
	}

	protected void initBgDrawables() {
		// t ImageView view = new ImageView(null);
		this.recordBgDrawable1 = new ColorDrawable(BasicTheme.RECORD_COLOR_1);
		this.recordBgDrawable2 = new ColorDrawable(BasicTheme.RECORD_COLOR_2);
		this.headerBgDrawable = new ColorDrawable(BasicTheme.HEADER_COLOR);
		this.selectedRecordBgDrawable = new ColorDrawable(
				BasicTheme.SELECTED_RECORD_COLOR);
		this.selectedHeaderBgDrawable = this.selectedRecordBgDrawable;
	}

	protected void initFirstColumnDrawable() {
		this.firstColumnDrawable = new StateListDrawable();
		this.firstColumnDrawable.addState(new int[] {
				-android.R.attr.state_pressed, -android.R.attr.state_selected,
				-android.R.attr.state_focused }, this.getHeaderBgDrawable());
		this.firstColumnDrawable.addState(
				new int[] { android.R.attr.state_pressed },
				this.getSelectedHeaderBgDrawable());
		this.firstColumnDrawable.addState(
				new int[] { android.R.attr.state_selected },
				this.getSelectedHeaderBgDrawable());
	}

	@Override
	public Drawable[] getHeaderBackgroundDrawables(AbstractViewer dataView,
			int[] columnWidths) {
		Drawable[] result = new Drawable[columnWidths.length];
		// Path path = new Path();
		// path.addRect(0,0,2,10,Direction.CCW);
		for (int i = 0; i < columnWidths.length; i++) {
			if (i == 0) {
				result[i] = this.firstColumnDrawable;
			} else {
				/*
				 * PathShape shape = new PathShape(path,columnWidths[i],10);
				 * ShapeDrawable shapeDrawable = new ShapeDrawable(shape);
				 * shapeDrawable.getPaint().setColor(COLUMN_DIVIDER_COLOR);
				 */
				Drawable separatorDrawable = this.createSeparatorDrawable();

				Drawable selectedDrawable = new LayerDrawable(new Drawable[] {
						this.getSelectedHeaderBgDrawable(), separatorDrawable });
				Drawable unSelectedDrawable = new LayerDrawable(new Drawable[] {
						this.getHeaderBgDrawable(), separatorDrawable });
				StateListDrawable headerControlBgDrawable = new ButtonStateDrawable(
						unSelectedDrawable, selectedDrawable, null);
				result[i] = headerControlBgDrawable;
				// new SeparatorDrawable();
			}
		}
		return result;
	}

	@Override
	public Drawable[] getFooterBackgroundDrawables(AbstractViewer dataView,
			int[] columnWidths) {
		Drawable[] result = new Drawable[columnWidths.length];
		for (int i = 0; i < columnWidths.length; i++) {
			if (i == 0) {
				result[i] = this.getHeaderBgDrawable();
			} else {
				Drawable separatorDrawable = this.createSeparatorDrawable();

				Drawable unSelectedDrawable = new LayerDrawable(new Drawable[] {
						this.getHeaderBgDrawable(), separatorDrawable });
				result[i] = unSelectedDrawable;
			}
		}
		return result;
	}

	protected SeparatorDrawable createSeparatorDrawable() {
		return new SeparatorDrawable(this.getColumnDividerColor());
	}

	@Override
	public int getHeaderFontColor() {
		return BasicTheme.FONT_COLOR;
	}

	@Override
	public int getDialogTitleFontColor() {
		return Color.WHITE;
	}

	@Override
	public int getRecordFontColor() {
		return BasicTheme.FONT_COLOR;
	}

	@Override
	public Drawable getRecordBackgroundDrawable(
			AbstractViewer recordDescriptor, int position, int[] columnWidths,
			int rowHeight) {
		if ((this.selectedBgDrawable == null) || (this.columnWidths == null)
				|| !Arrays.equals(this.columnWidths, columnWidths)) {
			this.createBgDrawables(columnWidths, rowHeight);
			this.columnWidths = columnWidths;
		}
		if (position % 2 != 0) {
			return this.createRecordStateListDrawable1();
		}
		return this.createRecordStateListDrawable2();
	}

	@Override
	public Drawable getUnselectableRecordBackgroundDrawable(
			AbstractViewer recordDescriptor, int position, int[] columnWidths,
			int rowHeight) {
		if ((this.selectedBgDrawable == null) || (this.columnWidths == null)
				|| !Arrays.equals(this.columnWidths, columnWidths)) {
			this.createBgDrawables(columnWidths, rowHeight);
			this.columnWidths = columnWidths;
		}
		if (position % 2 != 0) {
			return unselectedBgDrawable1;
		}
		return unselectedBgDrawable2;
	}

	@Override
	public Drawable getGroupBackgroundDrawable(AbstractViewer dataView,
			int position, int[] columnWidths, int rowHeight) {
		return this.getRecordBackgroundDrawable(dataView, position,
				columnWidths, rowHeight);
	}

	protected Drawable createRecordStateListDrawable2() {
		return new ButtonStateDrawable(this.unselectedBgDrawable2,
				this.selectedBgDrawable, null);
	}

	protected Drawable createRecordStateListDrawable1() {
		return new ButtonStateDrawable(this.unselectedBgDrawable1,
				this.selectedBgDrawable, null);
	}

	protected void createBgDrawables(int[] columnWidths, int rowHeight) {
		Drawable separatorLinesDrawable = this.getColumnSeparatorDrawable(
				columnWidths, rowHeight);
		this.unselectedBgDrawable1 = this
				.createBgDrawable1(separatorLinesDrawable);
		this.unselectedBgDrawable2 = this
				.createBgDrawable2(separatorLinesDrawable);
		this.selectedBgDrawable = this
				.createSelectedBgDrawable(separatorLinesDrawable);
	}

	protected Drawable createBgDrawable1(Drawable separatorLinesDrawable) {
		return this.createCellDrawable(this.getRecordBgDrawable1(),
				separatorLinesDrawable);
	}

	protected Drawable createBgDrawable2(Drawable separatorLinesDrawable) {
		return this.createCellDrawable(this.getRecordBgDrawable2(),
				separatorLinesDrawable);
	}

	protected Drawable createSelectedBgDrawable(Drawable separatorLinesDrawable) {
		return this.createCellDrawable(this.getSelectedRecordBgDrawable(),
				separatorLinesDrawable);
	}

	protected Drawable createCellDrawable(Drawable baseBgDrawable,
			Drawable separatorLinesDrawable) {
		List<Drawable> drawablesList = new ArrayList<Drawable>();
		drawablesList.add(baseBgDrawable);
		if (this.isRowSeparatorsNeeded) {
			drawablesList.add(new RowSeparatorDrawable(this
					.getRowDividerColor()));
		}
		if (this.isColumnSeparatorsNeeded) {
			drawablesList.add(separatorLinesDrawable);
		}
		if (drawablesList.size() == 1) {
			return drawablesList.get(0);
		} else {
			return new LayerDrawable(drawablesList.toArray(new Drawable[0]));
		}
	}

	public boolean isColumnSeparatorsNeeded() {
		return isColumnSeparatorsNeeded;
	}

	public void setColumnSeparatorsNeeded(boolean isColumnSeparatorsNeeded) {
		this.isColumnSeparatorsNeeded = isColumnSeparatorsNeeded;
	}

	public boolean isRowSeparatorsNeeded() {
		return isRowSeparatorsNeeded;
	}

	public void setRowSeparatorsNeeded(boolean isRowSeparatorsNeeded) {
		this.isRowSeparatorsNeeded = isRowSeparatorsNeeded;
	}

	protected Drawable getColumnSeparatorDrawable(int[] columnWidths,
			int rowHeight) {
		ColumnSeparatorDrawable drawable = new ColumnSeparatorDrawable(
				columnWidths, BasicTheme.COLUMN_DIVIDER_COLOR);
		return drawable;
	}

	@Override
	public Drawable getQuickActionBackgroundDrawable() {
		return new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
				0xff525552, 0xff8c8a8c, 0xffefebef, 0xffb5b6b5, 0xff525552 });
	}

	@Override
	public Drawable getSelectedQuickActionBackgroundDrawable() {
		return new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
				0xff525502, 0xff9d9a4d, 0xffcfcb8f, 0xff959645, 0xff525512 });
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
	public int getBaseRightPadding() {
		return 6;
	}

	@Override
	public int getBaseBottomPadding() {
		return 2;
	}

	@Override
	public int getHeaderTopPadding() {
		return 3;
	}

	@Override
	public int getHeaderBottomPadding() {
		return 3;
	}

	@Override
	public int getSortArrowOffset() {
		return 4;
	}

	@Override
	public int getSortArrowPadding() {
		return 30;
	}

	@Override
	public int getSortArrowColor() {
		return Color.GRAY;
	}

	@Override
	public int getSortArrowBorderColor() {
		return Color.BLACK;
	}

	@Override
	public int getColumnDividerColor() {
		return BasicTheme.COLUMN_DIVIDER_COLOR;
	}

	@Override
	public int getRowDividerColor() {
		return BasicTheme.ROW_DIVIDER_COLOR;
	}

	@Override
	public ArrowDrawable getArrowDrawable(IColumn column) {
		return new ArrowDrawable(column, this.getSortArrowColor(),
				this.getSortArrowBorderColor());
	}

	@Override
	public float getHeaderFontShadowRadius() {
		return 0;
	}

	@Override
	public PointF getHeaderFontShadowShift() {
		return new PointF(1, 1);
	}

	@Override
	public int getHeaderFontShadowColor() {
		return Color.DKGRAY;
	}

	@Override
	public float getFooterFontShadowRadius() {
		return this.getHeaderFontShadowRadius();
	}

	@Override
	public PointF getFooterFontShadowShift() {
		return this.getHeaderFontShadowShift();
	}

	@Override
	public int getFooterFontShadowColor() {
		return this.getHeaderFontShadowColor();
	}

	protected Drawable getRecordBgDrawable1() {
		return this.recordBgDrawable1;
	}

	protected Drawable getRecordBgDrawable2() {
		return this.recordBgDrawable2;
	}

	protected Drawable getSelectedRecordBgDrawable() {
		return this.selectedRecordBgDrawable;
	}

	protected Drawable getSelectedHeaderBgDrawable() {
		return this.selectedHeaderBgDrawable;
	}

	protected Drawable getHeaderBgDrawable() {
		return this.headerBgDrawable;
	}

	@Override
	public int getIndicatorBound() {
		return 35;
	}

	@Override
	public String getTitle() {
		return "Basic theme";
	}

	@Override
	public int getViewBackgroundColor() {
		return Color.LTGRAY;
	}

	@Override
	public int getViewBackgroundFontColor() {
		return Color.BLACK;
	}

	@Override
	public int getActionBarTextColor() {
		return Color.WHITE;
	}

	@Override
	public float getActionBarTextSize() {
		return 20;
	}

	@Override
	public Drawable getActionBarBackgroundDrawable() {
		return new HatchDrawable();
	}

	@Override
	public IIconProvider getIconProvider() {
		if (this.iconProvider == null) {
			this.iconProvider = new BasicIconProvider();
		}
		return this.iconProvider;
	}

	@Override
	public Drawable getActionBarButtonBackgroundDrawable() {
		StateListDrawable actionBarButtonDrawable = new ButtonStateDrawable(
				new ColorDrawable(0), new ColorDrawable(0xaa0055ff), null);
		return actionBarButtonDrawable;
	}

	@Override
	public IApplicationMessagesProvider getLabelProvider() {
		return this.labelProvider;
	}

	public void setLabelProvider(IApplicationMessagesProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	@Override
	public int getMaxFilterDialogItemCount() {
		return 10;
	}

	@Override
	public Drawable getDialogBackgroundDrawable() {
		return new ColorDrawable(BasicTheme.DIALOG_BG_COLOR);
	}

	@Override
	public IContributionPresenter getContributionPresenter(int level,
			ICompositeContributionItem item) {
		IContributionItem[] children = item.getChildren();
		if (children.length > 5) {
			int ec = 0;
			for (int a = 0; a < children.length; a++) {
				if (children[a] instanceof IExtendedContributionItem) {
					IExtendedContributionItem z = (IExtendedContributionItem) children[a];
					if (z.getGroupId() != null) {
						ec++;
					}
				}
			}
			if (ec > children.length / 2 && children.length > 15) {
				return new TreeContributionPresenter();
			}
			return new DialogContributionPresenter();
		}
		if (level == 1) {
			return new QuickActionBarContributionPresenter();
		}
		return new DialogContributionPresenter();
	}

}
